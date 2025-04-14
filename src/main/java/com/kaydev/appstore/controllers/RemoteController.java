package com.kaydev.appstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.RemoteHandler;
import com.kaydev.appstore.models.dto.request.is.CreateRemoteConnectionRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

@RestController
@RequestMapping("/v1/is/remote")
public class RemoteController {
    @Autowired
    private RemoteHandler remoteHandler;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_REMOTE')")
    public ResponseEntity<BaseResponse> createRemote(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateRemoteConnectionRequest request) {
        return remoteHandler.createRemote(userDetails, request);
    }

    @PostMapping("/connections")
    @PreAuthorize("hasAuthority('REMOTE_LIST')")
    public ResponseEntity<BaseResponse> connectionList(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams searchParams) {
        return remoteHandler.connectionList(userDetails, searchParams);
    }
}
