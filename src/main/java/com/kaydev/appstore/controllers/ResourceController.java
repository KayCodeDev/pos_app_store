package com.kaydev.appstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.ResourceHandler;
import com.kaydev.appstore.models.dto.request.is.resource.ManageResourceRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.enums.ListType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;

@RestController
@RequestMapping("/v1/is/resource")
public class ResourceController {

    @Autowired
    private ResourceHandler resourceHandler;

    @PostMapping("/management")
    @PreAuthorize("hasAuthority('MANAGE_RESOURCE')")
    public ResponseEntity<BaseResponse> manageResource(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ManageResourceRequest<Object> request) {

        return resourceHandler.manageResource(userDetails, request);
    }

    @GetMapping("/manufacturer/{type}")
    public ResponseEntity<BaseResponse> getManufacturers(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable ListType type, @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return resourceHandler.getManufacturers(userDetails, type, search, page, size);
    }

    @GetMapping("/roles")
    public ResponseEntity<BaseResponse> getRoles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return resourceHandler.getRoles(userDetails);
    }

    @GetMapping("/model/{manufacturerId}/{type}")
    public ResponseEntity<BaseResponse> getModels(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long manufacturerId, @PathVariable ListType type,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return resourceHandler.getModels(userDetails, manufacturerId, type, search, page, size);
    }

    @GetMapping("/category/{type}")
    public ResponseEntity<BaseResponse> getCategories(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable ListType type, @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return resourceHandler.getCategories(userDetails, type, search, page, size);
    }

    @GetMapping("/country/{type}")
    public ResponseEntity<BaseResponse> getCountries(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable ListType type, @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return resourceHandler.getCountries(userDetails, type, search, page, size);
    }

    @GetMapping("/developer")
    public ResponseEntity<BaseResponse> getDevelopers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return resourceHandler.getDevelopers(userDetails);
    }

    @GetMapping("/distributor")
    public ResponseEntity<BaseResponse> getDistributors(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return resourceHandler.getDistributors(userDetails);
    }

    @GetMapping("/groups/{distributorId}")
    public ResponseEntity<BaseResponse> getGroups(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long distributorId) {
        return resourceHandler.getGroups(userDetails, distributorId);
    }

}
