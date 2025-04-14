package com.kaydev.appstore.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.RemoteConnectionObj;
import com.kaydev.appstore.models.dto.request.is.CreateRemoteConnectionRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.RemoteConnection;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.GenericUtil;

@Component
public class RemoteHandler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private TerminalService terminalService;

    public ResponseEntity<BaseResponse> createRemote(UserDetailsImpl userDetails,
            CreateRemoteConnectionRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            if (developer == null) {
                response.setStatus("error");
                response.setMessage("Developer account is required");
                return ResponseEntity.ok(response);
            }

            if (!developer.getSetting().isCanRemote()) {
                response.setStatus("error");
                response.setMessage("Remote connection not allowed on your account");
                return ResponseEntity.ok(response);
            }

            Terminal terminal = terminalService.getTerminalByIdAndDeveloper(request.getTerminalId(), developer);

            if (terminal == null) {
                response.setStatus("error");
                response.setMessage("Terminal not found");
                return ResponseEntity.ok(response);
            }

            if (!terminal.getModel().getOsType().equals(OsType.ANDROID)) {
                response.setStatus("error");
                response.setMessage("Remote connection only support on android terminal");
                return ResponseEntity.ok(response);
            }

            if (terminal.getStatus() != StatusType.ACTIVE) {
                response.setStatus("error");
                response.setMessage("Terminal is not active or has not synced yet");
                return ResponseEntity.ok(response);
            }

            if (Integer.parseInt(terminal.getTerminalInfo().getSdkVersion()) < 24) {
                response.setStatus("error");
                response.setMessage("Terminal android sdk version not supported");
                return ResponseEntity.ok(response);
            }

            if (developer.getRemoteHours() <= developer.getExhaustedRemoteHours()) {
                response.setStatus("error");
                response.setMessage(
                        "You have exhausted your remote hours. To create remote connection, subscribe for more remote hours. Contact customer support.");
                return ResponseEntity.ok(response);
            }

            RemoteConnection connection = new RemoteConnection();
            connection.setDeveloper(developer);
            connection.setTerminal(terminal);
            connection.setUser(user);
            connection.setConnectionId(GenericUtil.generateConnectionId(15));

            resourceService.getRemoteConnectionRepository().save(connection);

            response.setMessage("Remote connection created successfully");
            response.getData().put("connectionId", connection.getConnectionId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> connectionList(UserDetailsImpl userDetails, SearchParams searchParams) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = searchParams.getDeveloperId();
            if (developer != null) {
                devId = developer.getId();
            }

            Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id"));

            Page<RemoteConnectionObj> connections = resourceService.getAllRemoteConnectionByFilter(pageable, devId,
                    searchParams.getConnectionId(),
                    searchParams.getTerminalId(), searchParams.getUserId(), searchParams.getStatus());

            // List<RemoteConnectionObj> connectionObjs =
            // connections.getContent().stream().map(
            // RemoteConnectionObj::new).collect(Collectors.toList());

            response.setMessage("Connections retrieved successfully");
            response.getData().put("connections", connections.getContent());
            response.getData().put("currentPageNumber", connections.getNumber());
            response.getData().put("totalPages", connections.getTotalPages());
            response.getData().put("totalItems", connections.getTotalElements());
            response.getData().put("hasNext", connections.hasNext());

            if (developer != null) {
                Map<String, Object> remote = new HashMap<>();
                remote.put("remoteHours", developer.getRemoteHours());
                remote.put("exhaustedHours", developer.getExhaustedRemoteHours());
                response.getData().put("remote", remote);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }

    }
}
