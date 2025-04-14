package com.kaydev.appstore.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import com.kaydev.appstore.handlers.TerminalHandler;
import com.kaydev.appstore.models.dto.request.is.AssignTerminalsRequest;
import com.kaydev.appstore.models.dto.request.is.CreateTerminalRequest;
import com.kaydev.appstore.models.dto.request.is.DeleteBulkTerminalRequest;
import com.kaydev.appstore.models.dto.request.is.EditTerminalRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.request.is.TerminalGeoFenceRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/is/terminal")
public class TerminalController {
    @Autowired
    private TerminalHandler terminalHandler;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('TERMINAL_LIST')")
    public ResponseEntity<BaseResponse> getTerminals(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams search) {
        return terminalHandler.getTerminals(userDetails, search);
    }

    @PostMapping("/list/export")
    @PreAuthorize("hasAuthority('TERMINAL_LIST')")
    public void exportTerminals(HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams searchParams) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=terminal_report.xlsx");

        try (ByteArrayOutputStream outputStream = terminalHandler.exportTerminal(userDetails, searchParams)) {
            if (outputStream != null) {
                response.getOutputStream().write(outputStream.toByteArray());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @GetMapping("/{terminalUuid}")
    @PreAuthorize("hasAuthority('VIEW_TERMINAL')")
    public ResponseEntity<BaseResponse> getTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid) {
        return terminalHandler.getTerminal(userDetails, terminalUuid);
    }

    @GetMapping("/{terminalUuid}/apps")
    @PreAuthorize("hasAuthority('VIEW_TERMINAL')")
    public ResponseEntity<BaseResponse> getTerminalApps(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid) {
        return terminalHandler.getTerminalApps(userDetails, terminalUuid);
    }

    @GetMapping("/{terminalUuid}/logs")
    @PreAuthorize("hasAuthority('VIEW_TERMINAL')")
    public ResponseEntity<BaseResponse> getTerminalLogs(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid, @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return terminalHandler.getTerminalLogs(userDetails, terminalUuid, page, size);
    }

    @GetMapping("/{terminalUuid}/tasks")
    @PreAuthorize("hasAuthority('VIEW_TERMINAL')")
    public ResponseEntity<BaseResponse> getTerminalTasks(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid, @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestParam(name = "date", required = false) String date) {
        return terminalHandler.getTerminalTasks(userDetails, terminalUuid, page, size, date);
    }

    @GetMapping("/{terminalUuid}/sync")
    @PreAuthorize("hasAuthority('VIEW_TERMINAL')")
    public ResponseEntity<BaseResponse> syncTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid) {
        return terminalHandler.syncTerminal(userDetails, terminalUuid);
    }

    @PatchMapping("/{terminalUuid}/task/{taskUuid}/cancel")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> cancelTerminalTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid, @PathVariable String taskUuid) {
        return terminalHandler.cancelTerminalTask(userDetails, terminalUuid, taskUuid);
    }

    @PatchMapping("/{terminalUuid}/task/{taskUuid}/push")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> pushTerminalTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid, @PathVariable String taskUuid) {
        return terminalHandler.pushTerminalTask(userDetails, terminalUuid, taskUuid);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> assignTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AssignTerminalsRequest request) {
        return terminalHandler.assignTerminal(userDetails, request);
    }

    @PostMapping("/{terminalUuid}/geoFence")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> geoFenceTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid, @RequestBody TerminalGeoFenceRequest request) {
        return terminalHandler.geoFenceTerminal(userDetails, terminalUuid, request);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> createTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateTerminalRequest request) {
        return terminalHandler.createTerminal(userDetails, request);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> editTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody EditTerminalRequest request) {
        return terminalHandler.editTerminal(userDetails, request);
    }

    @DeleteMapping("/{terminalUuid}")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> deleteTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String terminalUuid) {
        return terminalHandler.deleteTerminal(userDetails, terminalUuid);
    }

    @PostMapping("/delete/bulk")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> deeleteBulkTerminal(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody DeleteBulkTerminalRequest request) {
        return terminalHandler.deleteBulkTerminal(userDetails, request);
    }

    @PostMapping("/create/bulk")
    @PreAuthorize("hasAuthority('CREATE_TERMINAL')")
    public ResponseEntity<BaseResponse> createTerminalBulk(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("developerId") Long developerId, @RequestParam("distributorId") Long distributorId,
            @RequestParam("manufacturerId") Long manufacturerId,
            @RequestParam("modelId") Long modelId, @RequestParam("osType") String osType,
            @RequestParam("file") MultipartFile file) {
        return terminalHandler.createTerminalBulk(userDetails, developerId, distributorId, manufacturerId, modelId,
                osType, file);
    }
}
