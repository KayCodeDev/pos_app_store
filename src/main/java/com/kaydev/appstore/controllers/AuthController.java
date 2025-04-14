package com.kaydev.appstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.AuthHandler;
import com.kaydev.appstore.models.dto.request.auth.ForgotPasswordRequest;
import com.kaydev.appstore.models.dto.request.auth.LoginRequest;
import com.kaydev.appstore.models.dto.request.auth.ResetPasswordRequest;
import com.kaydev.appstore.models.dto.request.auth.VerifyRequest;
import com.kaydev.appstore.models.dto.response.GenericResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
// @RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthHandler authHandler;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody @Valid LoginRequest request) {
        return authHandler.login(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@RequestBody @Valid VerifyRequest request) {
        return authHandler.verify(request);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<GenericResponse> forgetPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        return authHandler.forgotPassword(request);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return authHandler.resetPassword(request);
    }

}
