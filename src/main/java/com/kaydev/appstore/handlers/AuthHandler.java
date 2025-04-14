package com.kaydev.appstore.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.UserObj;
import com.kaydev.appstore.models.dto.request.auth.ForgotPasswordRequest;
import com.kaydev.appstore.models.dto.request.auth.LoginRequest;
import com.kaydev.appstore.models.dto.request.auth.ResetPasswordRequest;
import com.kaydev.appstore.models.dto.request.auth.VerifyRequest;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.security.jwt.JwtTokenProvider;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.UserService;
import com.kaydev.appstore.utils.GenericUtil;

@Component
public class AuthHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<GenericResponse> login(LoginRequest request) {
        GenericResponse response = new GenericResponse();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userDetails.getUser();
        if (!user.isVerified()) {
            String otp = GenericUtil.generateOtp();

            user.setVerificationToken(otp);
            userService.getUserRepository().save(user);

            notificationService.sendVerificationEmail(user);

            response.setStatus("verify");
            response.setMessage(
                    "Verification code has been sent to your registered email to verify your account");
            response.getData().put("userId", user.getId());

            return ResponseEntity.ok(response);

        } else if (!user.isActive()) {
            response.setStatus("error");
            response.setMessage("Your account is not active, contact support");

            return ResponseEntity.status(401).body(response);
        } else if (user.getUserType().equals(UserType.DEVELOPER)
                && !user.getDeveloper().getStatus().equals(StatusType.ACTIVE)) {
            response.setStatus("error");
            if (user.getDeveloper().getStatus().equals(StatusType.EXPIRED)) {
                response.setMessage(
                        "Your developer account is expired, Contact support ITEXStore support to reenew your organization subscription");
            } else if (user.getDeveloper().getStatus().equals(StatusType.INACTIVE)) {
                response.setMessage("Your developer account is inactive.");
            }

            return ResponseEntity.status(401).body(response);
        }

        String token = jwtTokenProvider.generateJwtToken(authentication);

        List<String> permissions = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        response.setMessage("Login successful");
        response.getData().put("token", token);
        response.getData().put("user", new UserObj(userDetails.getUser(), true));
        response.getData().put("permissions", permissions);

        userService.saveUserLog(userDetails.getUser(), "Login successful");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GenericResponse> verify(VerifyRequest request) {
        GenericResponse response = new GenericResponse();
        User user = userService.getUserRepository().findById(request.getUserId()).orElse(null);

        if (user == null) {
            response.setStatus("error");
            response.setMessage("No user account found");
            return ResponseEntity.status(404).body(response);
        }

        if (!user.getVerificationToken().equals(request.getOtp())) {
            response.setStatus("error");
            response.setMessage("Invalid veerification code");
            return ResponseEntity.status(401).body(response);
        }

        user.setVerificationToken(null);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setActive(true);
        user.setVerified(true);
        userService.getUserRepository().save(user);

        response.setMessage("Account verified successfully");

        userService.saveUserLog(user, "Account verified successfully");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GenericResponse> forgotPassword(ForgotPasswordRequest request) {
        GenericResponse response = new GenericResponse();
        User user = userService.getUserByEmail(request.getUsername());

        if (user == null) {
            response.setStatus("error");
            response.setMessage("No user account found");
            return ResponseEntity.ok(response);
        }

        String otp = GenericUtil.generateOtp();

        user.setVerificationToken(otp);
        userService.getUserRepository().save(user);

        notificationService.sendVerificationEmail(user);

        response.setMessage(
                "Verification code has been sent to your registered email to resse your password");
        response.getData().put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GenericResponse> resetPassword(ResetPasswordRequest request) {
        GenericResponse response = new GenericResponse();
        User user = userService.getUserRepository().findById(request.getUserId()).orElse(null);

        if (user == null) {
            response.setStatus("error");
            response.setMessage("No account found");
            return ResponseEntity.status(404).body(response);
        }

        if (!user.getVerificationToken().equals(request.getOtp())) {
            response.setStatus("error");
            response.setMessage("Invalid verification code");
            return ResponseEntity.status(401).body(response);
        }

        user.setVerificationToken(null);
        user.setPassword(encoder.encode(request.getPassword()));

        userService.getUserRepository().save(user);

        userService.saveUserLog(user, "Reset password successfully");

        response.setMessage("Your password has been updated successfully");

        return ResponseEntity.ok(response);
    }

}
