package com.kaydev.appstore.models.dto.request.is;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocketMessage {
    List<Map<String, Object>> tasks;
    String action;
    String message;
}
