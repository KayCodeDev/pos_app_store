package com.kaydev.appstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaydev.appstore.handlers.DeskHandler;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.enums.StatusType;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/desk")
public class DeskController {

    @Autowired
    DeskHandler deskHandler;

    @GetMapping("/remote/connection/validate/{connectionId}")
    public ResponseEntity<BaseResponse> validateConnection(HttpServletRequest request,
            @PathVariable String connectionId) {
        return deskHandler.validateConnection(connectionId);
    }

    @GetMapping("/remote/connection/update/{connectionId}/{status}/{time}")
    public ResponseEntity<BaseResponse> updateConnection(HttpServletRequest request,
            @PathVariable String connectionId, @PathVariable StatusType status, @PathVariable int time) {

        return deskHandler.updateConnection(connectionId, status, time);
    }

}
