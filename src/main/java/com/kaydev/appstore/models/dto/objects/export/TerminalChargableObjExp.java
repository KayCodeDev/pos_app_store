package com.kaydev.appstore.models.dto.objects.export;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalChargableObjExp {
    private String terminalSerial;
    private Long syncCount;
    private LocalDateTime lastHeartBeat;
    private String developer;
    private String distributor;
}
