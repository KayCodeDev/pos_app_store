package com.kaydev.appstore.controllers;

import org.springframework.validation.annotation.Validated;

// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.Semaphore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kaydev.appstore.handlers.StoreHandler;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.entities.Terminal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/store")
@Validated
@Slf4j
public class StoreController {
    // private final ConcurrentHashMap<String, Semaphore> deviceRequestLimiters =
    // new ConcurrentHashMap<>();

    @Autowired
    private StoreHandler storeHandler;

    @GetMapping("/apps/{serialNumber}")
    public ResponseEntity<BaseResponse> getApps(HttpServletRequest request,
            @PathVariable String serialNumber) {
        Terminal terminal = (Terminal) request.getAttribute("terminal");
        return storeHandler.getApps(terminal, serialNumber);
    }

    // @PostMapping("/terminal/sync")
    // public ResponseEntity<BaseResponse> syncTerminal(HttpServletRequest request,
    // @RequestBody TerminalSyncRequest requestBody) {
    // Terminal terminal = (Terminal) request.getAttribute("terminal");

    // String uniqueKey = terminal.getSerialNumber() + request.getLocalAddr();
    // Semaphore semaphore = deviceRequestLimiters.computeIfAbsent(uniqueKey, k ->
    // new Semaphore(1));

    // if (semaphore.tryAcquire()) {
    // try {
    // return storeHandler.syncTerminal(terminal, requestBody);
    // } finally {
    // semaphore.release();
    // }
    // } else {
    // // log.error("Too many requests from the same IP and device:" +
    // // terminal.getSerialNumber()
    // // + request.getLocalAddr());
    // BaseResponse errorResponse = new BaseResponse();
    // errorResponse.setStatus("error");
    // errorResponse.setMessage("Exception occured, Kindly try again");
    // return ResponseEntity.ok(errorResponse);
    // }

    // }

    // @PostMapping("/task/update")
    // public ResponseEntity<BaseResponse> updateTask(
    // HttpServletRequest request,
    // @RequestParam("serialNumber") String serialNumber,
    // @RequestParam("taskId") String taskId,
    // @RequestParam("status") String status,
    // @RequestParam("message") String message,
    // @RequestParam(value = "capture", required = false) MultipartFile capture) {
    // Terminal terminal = (Terminal) request.getAttribute("terminal");
    // return storeHandler.updateTask(terminal, serialNumber, taskId, status,
    // message, capture);
    // }

    @PostMapping("/task/upload")
    public ResponseEntity<BaseResponse> taskUpload(
            HttpServletRequest request,
            @RequestParam("serialNumber") String serialNumber,
            @RequestParam("taskId") String taskId,
            @RequestParam(value = "capture", required = false) MultipartFile capture) {
        Terminal terminal = (Terminal) request.getAttribute("terminal");
        return storeHandler.taskUpload(terminal, serialNumber, taskId, capture);
    }

    // @GetMapping("/notify/download/{appUuid}/{versionUuid}")
    // public ResponseEntity<BaseResponse> notifyDownload(HttpServletRequest
    // request,
    // @PathVariable String appUuid, @PathVariable String versionUuid) {

    // Terminal terminal = (Terminal) request.getAttribute("terminal");
    // return storeHandler.notifyDownload(terminal, appUuid, versionUuid);
    // }

}
