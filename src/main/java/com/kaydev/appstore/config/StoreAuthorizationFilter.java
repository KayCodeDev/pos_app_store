package com.kaydev.appstore.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.utils.GenericUtil;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StoreAuthorizationFilter implements Filter {
    private String storeApiKey;

    public StoreAuthorizationFilter(String storeApiKey) {
        this.storeApiKey = storeApiKey;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String apiKey = httpServletRequest.getHeader("x-api-key");
        if (apiKey == null) {
            unauthorized(httpServletResponse);
            return;
        }
        if (!storeApiKey.equals(apiKey)) {
            unauthorized(httpServletResponse);
            return;
        }
        try {
            chain.doFilter(request, response);
        } catch (java.io.IOException | ServletException e) {
            log.error("Exception in StoreAuthorizationFilter", e);
            return;
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * @param httpServletResponse
     * @throws IOException
     */
    private void unauthorized(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(401);

        BaseResponse response = new BaseResponse();
        response.setStatus("error");
        response.setMessage("Unauthorized request");
        String json = GenericUtil.convertToJsonString(response);

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            httpServletResponse.getWriter().write(json);
        } catch (java.io.IOException e) {
            log.error("Exception in StoreAuthorizationFilter:unauthorized", e);
        }
    }
}
