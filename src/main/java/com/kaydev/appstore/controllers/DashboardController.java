package com.kaydev.appstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.DashboardHandler;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

@RestController
@RequestMapping("/v1/is/dashboard")
public class DashboardController {
    @Autowired
    private DashboardHandler dashboardHandler;

    @GetMapping("/summary")
    public ResponseEntity<BaseResponse> storeSummary(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardHandler.storeSummary(userDetails);
    }

    @GetMapping("/apps/top")
    public ResponseEntity<BaseResponse> getTopApps(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardHandler.getTopApps(userDetails);
    }

    @GetMapping("/apps/new")
    public ResponseEntity<BaseResponse> getNewApps(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardHandler.getNewApps(userDetails);
    }

    @GetMapping("/downloads/monthly")
    public ResponseEntity<BaseResponse> getMonthlyDownloads(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardHandler.getMonthlyDownloads(userDetails);
    }

    @GetMapping("/downloads/app/versions")
    public ResponseEntity<BaseResponse> getAppVersionDownloadSummary(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String appUuid) {
        return dashboardHandler.getAppVersionDownloadSummary(userDetails, appUuid);
    }

    @GetMapping("/terminal/manufacturers")
    public ResponseEntity<BaseResponse> getTerminalManufacturerSummary(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardHandler.getTerminalManufacturerSummary(userDetails);
    }

    @GetMapping("/terminal/model")
    public ResponseEntity<BaseResponse> getTerminalManufacturerModelSummary(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String manufacturerUuid) {
        return dashboardHandler.getTerminalManufacturerModelSummary(userDetails, manufacturerUuid);
    }

}
