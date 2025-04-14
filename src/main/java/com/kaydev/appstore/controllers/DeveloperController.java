package com.kaydev.appstore.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.DeveloperHandler;
import com.kaydev.appstore.models.dto.request.is.AddSubscriptionRequest;
import com.kaydev.appstore.models.dto.request.is.CreateDeveloperRequest;
import com.kaydev.appstore.models.dto.request.is.CreateDistributorRequest;
import com.kaydev.appstore.models.dto.request.is.CreateGroup;
import com.kaydev.appstore.models.dto.request.is.EditDeveloperRequest;
import com.kaydev.appstore.models.dto.request.is.EditDistributorRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.request.is.UpdateDeveloperSettingRequest;
import com.kaydev.appstore.models.dto.request.is.UpdateDeveloperStatusRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/is/developer")
public class DeveloperController {

    @Autowired
    private DeveloperHandler developerHandler;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('VIEW_DEVELOPER')")
    public ResponseEntity<BaseResponse> getDevelopers(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams search) {
        return developerHandler.getDevelopers(userDetails, search);
    }

    @GetMapping("/{developerUuid}")
    @PreAuthorize("hasAuthority('VIEW_DEVELOPER')")
    public ResponseEntity<BaseResponse> getDeveloper(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String developerUuid) {
        return developerHandler.getDeveloper(userDetails, developerUuid);
    }

    @PostMapping("/subscription/list")
    @PreAuthorize("hasAuthority('VIEW_SUBCRIPTIONS')")
    public ResponseEntity<BaseResponse> getDeveloperSubscription(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams search) {
        return developerHandler.getDeveloperSubscription(userDetails, search);
    }

    @PostMapping("/{developerUuid}/edit")
    @PreAuthorize("hasAuthority('CREATE_DEVELOPER')")
    public ResponseEntity<BaseResponse> editDeveloper(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String developerUuid, @RequestBody EditDeveloperRequest request) {
        return developerHandler.editDeveloper(userDetails, developerUuid, request);
    }

    @PostMapping("/{developerUuid}/settings")
    @PreAuthorize("hasAuthority('CREATE_DEVELOPER')")
    public ResponseEntity<BaseResponse> updateDeveloperSettings(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String developerUuid, @RequestBody UpdateDeveloperSettingRequest request) {
        return developerHandler.updateDeveloperSettings(userDetails, developerUuid, request);
    }

    @PostMapping("/{developerUuid}/subscribe")
    @PreAuthorize("hasAuthority('CREATE_DEVELOPER') && hasAuthority('ADD_SUBSCRIPTIONS')")
    public ResponseEntity<BaseResponse> addDeveloperSubscription(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String developerUuid, @RequestBody AddSubscriptionRequest request) {
        return developerHandler.addDeveloperSubscription(userDetails, developerUuid, request);
    }

    @PostMapping("/{developerUuid}/status")
    @PreAuthorize("hasAuthority('CREATE_DEVELOPER')")
    public ResponseEntity<BaseResponse> updateDeveloperStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String developerUuid, @RequestBody UpdateDeveloperStatusRequest request) {
        return developerHandler.updateDeveloperStatus(userDetails, developerUuid, request);
    }

    @PostMapping("/{developerUuid}/chargable")
    @PreAuthorize("hasAuthority('CREATE_DEVELOPER') && hasAuthority('ADD_SUBSCRIPTIONS')")
    public void exportTerminals(HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String developerUuid,
            @RequestBody SearchParams searchParams) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=developer_chargable_report_"
                + searchParams.getFrom() + "_" + searchParams.getTo() + ".xlsx");

        try (ByteArrayOutputStream outputStream = developerHandler.exportDeveloperChargable(userDetails, developerUuid,
                searchParams)) {
            if (outputStream != null) {
                response.getOutputStream().write(outputStream.toByteArray());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @PostMapping("/distributor/list")
    @PreAuthorize("hasAuthority('VIEW_DISTRIBUTOR')")
    public ResponseEntity<BaseResponse> getDeveloperDistributors(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams search) {
        return developerHandler.getDeveloperDistributors(userDetails, search);
    }

    @GetMapping("/distributor/{distributorUuid}")
    @PreAuthorize("hasAuthority('VIEW_DISTRIBUTOR')")
    public ResponseEntity<BaseResponse> getDeveloperDistributor(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String distributorUuid) {
        return developerHandler.getDeveloperDistributor(userDetails, distributorUuid);
    }

    @PostMapping("/distributor/{distributorUuid}/edit")
    @PreAuthorize("hasAuthority('CREATE_DISTRIBUTOR')")
    public ResponseEntity<BaseResponse> editDeveloperDistributor(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String distributorUuid, @RequestBody EditDistributorRequest request) {
        return developerHandler.editDeveloperDistributor(userDetails, distributorUuid, request);
    }

    @DeleteMapping("/distributor/{distributorUuid}")
    @PreAuthorize("hasAuthority('CREATE_DISTRIBUTOR')")
    public ResponseEntity<BaseResponse> deleteDeveloperDistributor(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String distributorUuid) {
        return developerHandler.deleteDeveloperDistributor(userDetails, distributorUuid);
    }

    @PostMapping("/group/list")
    @PreAuthorize("hasAuthority('GROUP_LIST')")
    public ResponseEntity<BaseResponse> getDeveloperGroups(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SearchParams search) {
        return developerHandler.getDeveloperGroups(userDetails, search);
    }

    @GetMapping("/group/{groupUuid}")
    @PreAuthorize("hasAuthority('VIEW_GROUP')")
    public ResponseEntity<BaseResponse> getDeveloperGroup(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUuid) {
        return developerHandler.getDeveloperGroup(userDetails, groupUuid);
    }

    @DeleteMapping("/group/{groupUuid}")
    @PreAuthorize("hasAuthority('VIEW_GROUP')")
    public ResponseEntity<BaseResponse> deleteDeveloperGroup(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUuid) {
        return developerHandler.deleteDeveloperGroup(userDetails, groupUuid);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_DEVELOPER')")
    public ResponseEntity<BaseResponse> createDevelopr(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateDeveloperRequest request) {
        return developerHandler.createDeveloper(userDetails, request);
    }

    @PostMapping("/distributor/create")
    @PreAuthorize("hasAuthority('CREATE_DISTRIBUTOR')")
    public ResponseEntity<BaseResponse> createDeveloperDistributor(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateDistributorRequest request) {
        return developerHandler.createDistibutor(userDetails, request);
    }

    @PostMapping("/group/create")
    @PreAuthorize("hasAuthority('CREATE_GROUP')")
    public ResponseEntity<BaseResponse> createDeveloperGroup(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateGroup request) {
        return developerHandler.createGroup(userDetails, request);
    }
}
