package com.kaydev.appstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kaydev.appstore.handlers.AppHandler;
import com.kaydev.appstore.models.dto.request.is.EditAppRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.request.is.SubmitAppRequest;
import com.kaydev.appstore.models.dto.request.is.SubmitAppVersionRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

@RestController
@RequestMapping("/v1/is/app")
@Validated
public class AppController {
    @Autowired
    private AppHandler appHandler;

    @PostMapping("/store")
    @PreAuthorize("hasAuthority('STORE_LIST')")
    public ResponseEntity<BaseResponse> storeList(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams searchParams) {
        return appHandler.storeList(userDetails, searchParams);
    }

    @GetMapping("/store/{appUuid}")
    @PreAuthorize("hasAuthority('VIEW_STORE_APP')")
    public ResponseEntity<BaseResponse> getApp(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String appUuid) {
        return appHandler.getApp(userDetails, appUuid);
    }

    @GetMapping("/store/{appUuid}/versions")
    @PreAuthorize("hasAuthority('VIEW_STORE_APP')")
    public ResponseEntity<BaseResponse> getAppVersions(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String appUuid) {
        return appHandler.getAppVersions(userDetails, appUuid);
    }

    @PostMapping("/parser")
    @PreAuthorize("hasAuthority('SUBMIT_APP')")
    public ResponseEntity<BaseResponse> parseApp(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("osType") String osType, @RequestParam("file") MultipartFile file) {
        return appHandler.parseApp(userDetails, osType, file);
    }

    @PostMapping("/screenshoot/upload")
    @PreAuthorize("hasAuthority('SUBMIT_APP')")
    public ResponseEntity<BaseResponse> screenShotUpload(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("file") MultipartFile file) {
        return appHandler.screenShotUpload(userDetails, file);
    }

    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('SUBMIT_APP')")
    public ResponseEntity<BaseResponse> submitApp(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SubmitAppRequest request) {
        try {
            return appHandler.submitApp(userDetails, request);
        } catch (Exception e) {
            GenericResponse response = new GenericResponse();
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('SUBMIT_APP')")
    public ResponseEntity<BaseResponse> editApp(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody EditAppRequest request) {
        return appHandler.editApp(userDetails, request);
    }

    @PostMapping("/submit/version")
    @PreAuthorize("hasAuthority('SUBMIT_APP')")
    public ResponseEntity<BaseResponse> submitAppVersion(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SubmitAppVersionRequest request) {
        return appHandler.submitAppVersion(userDetails, request);
    }

    @DeleteMapping("/{appUuid}")
    @PreAuthorize("hasAuthority('DELETE_APP')")
    public ResponseEntity<BaseResponse> deleteApp(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String appUuid) {
        return appHandler.deleteApp(userDetails, appUuid);
    }

    @PatchMapping("/{appUuid}/status/{status:^(?:active|inactive)$}")
    @PreAuthorize("hasAuthority('SUBMIT_APP')")
    public ResponseEntity<BaseResponse> modifyAppStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String appUuid, @PathVariable String status) {
        return appHandler.modifyAppStatus(userDetails, appUuid, status);
    }

}
