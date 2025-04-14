package com.kaydev.appstore.handlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.UserLogObj;
import com.kaydev.appstore.models.dto.objects.UserMinObj;
import com.kaydev.appstore.models.dto.objects.UserObj;
import com.kaydev.appstore.models.dto.request.is.ChangePasswordRequest;
import com.kaydev.appstore.models.dto.request.is.CreateUserRequest;
import com.kaydev.appstore.models.dto.request.is.EditUserRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Role;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.entities.UserLog;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.services.data.UserService;
import com.kaydev.appstore.utils.GenericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder encoder;

    public ResponseEntity<BaseResponse> getUsers(UserDetailsImpl userDetails, SearchParams searchParams) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = searchParams.getDeveloperId();
            if (developer != null) {
                devId = developer.getId();
            }

            Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id"));

            Page<UserMinObj> users = userService.getAllUserByFilter(pageable, searchParams.getSearch(),
                    searchParams.getUserType(), devId, searchParams.getStatus(), searchParams.getRole());

            // List<UserMinObj> usersObj = users.getContent().stream().filter(u -> u.getId()
            // != user.getId())
            // .map(u -> new UserMinObj(u))
            // .collect(Collectors.toList());

            response.setMessage("Users retrieved successfully");
            response.getData().put("users", users.getContent());
            response.getData().put("currentPageNumber", users.getNumber());
            response.getData().put("totalPages", users.getTotalPages());
            response.getData().put("totalItems", users.getTotalElements());
            response.getData().put("hasNext", users.hasNext());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("getUsers", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while gettting users");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> createUser(UserDetailsImpl userDetails, CreateUserRequest request) {
        GenericResponse response = new GenericResponse();

        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            User existingUser = userService.getUserByEmail(request.getEmail());

            if (existingUser != null) {
                response.setStatus("error");
                response.setMessage("Email already exists");
                return ResponseEntity.ok(response);
            }

            if (developer != null) {
                request.setDeveloperId(developer.getId());
                request.setUserType(UserType.DEVELOPER);
            } else if (request.getDeveloperId() > 0) {
                developer = developerService.getDeveloperRepository().findById(request.getDeveloperId())
                        .orElseThrow(() -> new RuntimeException("Developer not found"));
            }

            Role role = resourceService.getRoleRepository().findByName(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            if (!role.getRoleType().equals(request.getUserType())) {
                response.setStatus("error");
                response.setMessage("Role not allowed for user type");
                return ResponseEntity.ok(response);
            }

            String password = GenericUtil.generateSecurePassword();

            User newUser = new User();
            newUser.setDeveloper(developer);
            newUser.setEmail(request.getEmail());
            newUser.setFullName(request.getFullName());
            newUser.setUserType(request.getUserType());
            newUser.setPassword(encoder.encode(password));
            newUser.setRole(role);
            newUser.setPermissions(new HashSet<>());
            newUser.getPermissions().addAll(role.getPermissions());

            userService.getUserRepository().save(newUser);

            notificationService.sendWelcomeEmail(newUser, password);

            response.setMessage("User created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("createUser", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while creating user");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> viewUser(UserDetailsImpl userDetails, String userUuid) {

        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            User existingUser = userService.getUserByUuid(userUuid);

            if (existingUser == null) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && existingUser.getDeveloper() != null
                    && developer.getId() != existingUser.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            UserObj userMinObj = new UserObj(existingUser);
            response.setMessage("User retrieved successfully");
            response.getData().put("user", userMinObj);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("viewUser", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while gettting user");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> modifyUserStatus(UserDetailsImpl userDetails, String userUuid, String status) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            User existingUser = userService.getUserByUuid(userUuid);

            if (existingUser == null) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && existingUser.getDeveloper() != null
                    && developer.getId() != existingUser.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            existingUser.setActive(status.equals("active"));
            userService.getUserRepository().save(existingUser);

            response.setMessage("User status modified successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("modifyUserStatus", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while modifying user status");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> userLogs(UserDetailsImpl userDetails, String userUuid, int page, int size,
            LocalDate date) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            User existingUser = userService.getUserByUuid(userUuid);

            if (existingUser == null) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && existingUser.getDeveloper() != null
                    && developer.getId() != existingUser.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

            LocalDateTime start = null;
            LocalDateTime end = null;
            if (date != null) {
                start = date.atStartOfDay();
                end = date.atTime(LocalTime.MAX);
            }

            Page<UserLog> userLogs = userService.getUserLogs(pageable, existingUser.getId(), start, end);

            List<UserLogObj> userLogObjs = userLogs.getContent().stream().map(UserLogObj::new)
                    .collect(Collectors.toList());

            response.setMessage("User logs retrieved successfully");
            response.getData().put("userLogs", userLogObjs);
            response.getData().put("currentPageNumber", userLogs.getNumber());
            response.getData().put("totalPages", userLogs.getTotalPages());
            response.getData().put("totalItems", userLogs.getTotalElements());
            response.getData().put("hasNext", userLogs.hasNext());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("deleteUser", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while getting user logs");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> editUser(UserDetailsImpl userDetails, String userUuid,
            EditUserRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            User existingUser = userService.getUserByUuid(userUuid);

            if (existingUser == null) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            if (developer != null && developer.getId() != existingUser.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("User not found");
                return ResponseEntity.ok(response);
            }

            if (request.getFullName() != null) {
                existingUser.setFullName(request.getFullName());
            }
            if (request.getEmail() != null) {
                User existingEmail = userService.getUserByEmail(request.getEmail());
                if (existingEmail != null && existingEmail.getId() != existingUser.getId()) {
                    response.setStatus("error");
                    response.setMessage("Email already exist");
                    return ResponseEntity.ok(response);
                }

                existingUser.setEmail(request.getEmail());
            }
            if (request.getRole() != null) {
                Role role = resourceService.getRoleRepository().findByName(request.getRole())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                existingUser.setRole(role);
            }
            userService.getUserRepository().save(existingUser);

            response.setMessage("User updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("editUser", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while updating user");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> changePassword(UserDetailsImpl userDetails, ChangePasswordRequest request) {
        GenericResponse response = new GenericResponse();
        User user = userDetails.getUser();

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            response.setStatus("error");
            response.setMessage("Incorrect current password");

            return ResponseEntity.status(401).body(response);
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userService.getUserRepository().save(user);

        response.setMessage("Password changed successfully");

        userService.saveUserLog(user, "changed password");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getMe(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();

        User user = userService.getUserByUuid(userDetails.getUser().getUuid());

        response.setMessage("User retrieved successfully");
        response.getData().put("user", new UserMinObj(user));

        return ResponseEntity.ok(response);
    }

}
