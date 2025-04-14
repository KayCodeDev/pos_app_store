package com.kaydev.appstore.handlers;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.GenericUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class StoreInterceptor implements HandlerInterceptor {
    @Autowired
    private TerminalService terminalService;

    @Override
    @Transactional
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {

        String serialNumber = request.getHeader("x-serial-number");
        Terminal terminal = terminalService.getTerminalBySerialNumber(serialNumber);

        if (terminal != null) {
            request.setAttribute("terminal", terminal);
            return true;
        } else {
            sendErrorResponse(response, "Terminal not listed in store");
            return false;
        }
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        // Post-handle logic if needed
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex)
            throws Exception {
        // Terminal terminal = (Terminal) request.getAttribute("terminal");
        // if (terminal != null) {
        // terminal.setLastHeartbeat(LocalDateTime.now());

        // if (terminal.getStatus() == StatusType.INACTIVE) {
        // terminal.setStatus(StatusType.ACTIVE);
        // }
        // terminalService.getTerminalRepository().save(terminal);
        // }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);

        PrintWriter writer = response.getWriter();
        writer.write(GenericUtil.convertMapToJsonString(errorResponse));
        writer.flush();
    }

}