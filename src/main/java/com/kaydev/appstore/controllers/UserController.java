package com.kaydev.appstore.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.UserHandler;
import com.kaydev.appstore.models.dto.request.is.ChangePasswordRequest;
import com.kaydev.appstore.models.dto.request.is.CreateUserRequest;
import com.kaydev.appstore.models.dto.request.is.EditUserRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/is/user")
public class UserController {

    @Autowired
    private UserHandler userHandler;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('USER_LIST')")
    public ResponseEntity<BaseResponse> getUsers(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams search) {
        return userHandler.getUsers(userDetails, search);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<BaseResponse> createUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateUserRequest request) {
        return userHandler.createUser(userDetails, request);
    }

    @GetMapping("/{userUuid}")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    public ResponseEntity<BaseResponse> viewUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String userUuid) {
        return userHandler.viewUser(userDetails, userUuid);
    }

    @PatchMapping("/{userUuid}/status/{status:^(?:active|inactive)$}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<BaseResponse> modifyUserStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String userUuid, @PathVariable String status) {
        return userHandler.modifyUserStatus(userDetails, userUuid, status);
    }

    @GetMapping("/{userUuid}/logs")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    public ResponseEntity<BaseResponse> userLogs(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String userUuid, @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestParam(name = "date", required = false) LocalDate date) {
        return userHandler.userLogs(userDetails, userUuid, page, size, date);
    }

    @PostMapping("/{userUuid}/edit")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<BaseResponse> editUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String userUuid, @RequestBody EditUserRequest request) {
        return userHandler.editUser(userDetails, userUuid, request);
    }

    @PostMapping("/password/change")
    public ResponseEntity<BaseResponse> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChangePasswordRequest request) {
        return userHandler.changePassword(userDetails, request);
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse> getMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userHandler.getMe(userDetails);
    }

}
