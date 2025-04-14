package com.kaydev.appstore.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SendHttp {
    public Map<String, Object> post(@NonNull String requestBody, @NonNull String url, HttpHeaders headers) {

        Map<String, Object> responseBody = new HashMap<>();
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response;
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            responseBody = GenericUtil.convertJsonStringToMap(response.getBody());
            responseBody.put("statuscode", response.getStatusCode().toString());

        } catch (HttpClientErrorException ex) {

            String response = ex.getResponseBodyAsString();

            responseBody = GenericUtil.convertJsonStringToMap(response);
            responseBody.put("statuscode", ex.getStatusCode().toString());

        } catch (Exception ex) {
            // Handle other types of exceptions, e.g., network errors
            log.error("Exception at Post request to: " + url, ex);
        }

        return responseBody;

    }

    public Map<String, Object> get(@NonNull String url, @NonNull HttpHeaders headers) {

        Map<String, Object> responseBody = new HashMap<>();
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            responseBody = GenericUtil.convertJsonStringToMap(response.getBody());
            responseBody.put("statuscode", response.getStatusCode().toString());

        } catch (HttpClientErrorException ex) {

            String response = ex.getResponseBodyAsString();

            responseBody = GenericUtil.convertJsonStringToMap(response);
            responseBody.put("statuscode", ex.getStatusCode().toString());

        } catch (Exception ex) {
            // Handle other types of exceptions, e.g., network errors
            log.error("Exception at Get request to: " + url, ex);
        }
        return responseBody;
    }
}
