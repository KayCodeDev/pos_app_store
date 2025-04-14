package com.kaydev.appstore.handlers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.RemoteConnectionObj;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.RemoteConnection;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.ResourceService;

@Component
public class DeskHandler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<BaseResponse> validateConnection(String connectionId) {
        GenericResponse response = new GenericResponse();

        RemoteConnection remote = resourceService.getRemoteConnectionByConnectionId(connectionId);

        if (remote == null) {
            response.setStatus("error");
            response.setMessage("Remote connection not found");

            return ResponseEntity.ok(response);
        }

        if (remote.getStatus() == StatusType.EXPIRED) {
            response.setStatus("error");
            response.setMessage("Remote connection is expired");

            return ResponseEntity.ok(response);
        }

        response.getData().put("remote", new RemoteConnectionObj(remote));
        response.setMessage("Connection Validated Successfully");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> updateConnection(String connectionId, StatusType status, int time) {
        GenericResponse response = new GenericResponse();

        RemoteConnection remote = resourceService.getRemoteConnectionByConnectionId(connectionId);

        if (remote == null) {
            response.setStatus("error");
            response.setMessage("Remote connection not found");

            return ResponseEntity.ok(response);
        }

        if (remote.getStatus() == StatusType.EXPIRED) {
            response.setStatus("error");
            response.setMessage("Remote connection is expired");

            return ResponseEntity.ok(response);
        }

        Developer developer = remote.getDeveloper();

        int totalTime = remote.getDuration() + time;

        if (totalTime >= (30 * 60000)) {
            status = StatusType.EXPIRED;
        }

        remote.setStatus(status);
        remote.setDuration(totalTime);
        resourceService.getRemoteConnectionRepository().save(remote);

        developer.setExhaustedRemoteHours(calculateExhaustedHours(developer, time));

        developerService.getDeveloperRepository().save(developer);

        Map<String, Object> data = new HashMap<>();
        data.put("action", "update_remote");
        notificationService.sendWebSocket(data, "remote_update_" + remote.getDeveloper().getUuid());

        response.setMessage("Connection Updated Successfully");
        return ResponseEntity.ok(response);
    }

    private double calculateExhaustedHours(Developer developer, int timeInMillis) {

        double exhaustedInMillis = developer.getExhaustedRemoteHours() * 60 * 60000;
        double totalExhaustedInMillis = exhaustedInMillis + timeInMillis;
        double hours = totalExhaustedInMillis / 3600000;
        BigDecimal bd = new BigDecimal(hours).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
