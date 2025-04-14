package com.kaydev.appstore.models.dto.objects;

import java.time.LocalDateTime;

import com.kaydev.appstore.models.entities.RemoteConnection;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RemoteConnectionObj {
    private Long id;
    private String uuid;
    private TerminalRemoteObj terminal;
    private UserMinObj user;
    private DeveloperMinObj developer;
    private String connectionId;
    private int duration;
    private StatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RemoteConnectionObj(RemoteConnection remoteConnection) {
        this.id = remoteConnection.getId();
        this.uuid = remoteConnection.getUuid();
        this.terminal = new TerminalRemoteObj(remoteConnection.getTerminal());
        this.user = new UserMinObj(remoteConnection.getUser());
        this.developer = new DeveloperMinObj(remoteConnection.getDeveloper());
        this.connectionId = remoteConnection.getConnectionId();
        this.duration = remoteConnection.getDuration();
        this.status = remoteConnection.getStatus();
        this.createdAt = remoteConnection.getCreatedAt();
        this.updatedAt = remoteConnection.getUpdatedAt();
    }
}
