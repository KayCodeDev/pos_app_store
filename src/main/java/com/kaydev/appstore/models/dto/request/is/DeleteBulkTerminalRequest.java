package com.kaydev.appstore.models.dto.request.is;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeleteBulkTerminalRequest {
    @NotNull(message = "Terminal list is required")
    private List<Long> terminals;

}
