package com.kaydev.appstore.server;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketServer {
    @Autowired
    SimpMessagingTemplate template;

    @SendTo("/topic/{channelId}")
    public Map<String, Object> broadcastAction(@Payload Map<String, Object> socketMessage) {
        return socketMessage;
    }
}
