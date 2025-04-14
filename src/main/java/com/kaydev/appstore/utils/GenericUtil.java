package com.kaydev.appstore.utils;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GenericUtil {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertListToJsonString(List<String> stringList) {
        try {
            return objectMapper.writeValueAsString(stringList);
        } catch (JsonProcessingException e) {
            log.error("convertListToJsonString Exception", e);
            return null;
        }
    }

    public static String convertMapToJsonString(Map<String, Object> dataMap) {
        try {
            return objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            // Handle exception appropriately (e.g., log or throw)
            log.error("convertMapToJsonString Exception", e);
            return "{}"; // Return an empty JSON object in case of an error
        }
    }

    public static String convertToJsonString(Object dataMap) {
        try {
            return objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            // Handle exception appropriately (e.g., log or throw)
            log.error("convertMapToJsonString Exception", e);
            return "{}"; // Return an empty JSON object in case of an error
        }
    }

    public static Map<String, Object> convertObjectToMap(Object obj) {
        return objectMapper.convertValue(obj, Map.class);
    }

    public static Map<String, Object> convertJsonStringToMap(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });

        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasSpecialCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> targetType) {
        try {
            return modelMapper.map(map, targetType);
        } catch (Exception e) {
            log.error("convertMapToObject Exception", e);
            return null;
        }
    }

    public static <T> T convertObjectToClass(Object object, Class<T> targetType) {
        return objectMapper.convertValue(object, targetType);
    }

    public static <T, U> U convertEntityToObject(T entity, Class<U> dtoClass) {
        try {
            return modelMapper.map(entity, dtoClass);
        } catch (Exception e) {
            log.error("convertEntityToObject Exception", e);
            return null;
        }
    }

    public static String generateUniqueNumber() {
        long currentTimeMillis = System.currentTimeMillis();
        int randomInt = secureRandom.nextInt(1000000);

        String uniqueNumber = String.format("%d%06d", currentTimeMillis, randomInt);

        return uniqueNumber;
    }

    public static String generateSecurePassword() {
        int passwordLength = 12;

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";

        SecureRandom secureRandom = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }

    public static String generateConnectionId(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom secureRandom = new SecureRandom();

        StringBuilder connectionId = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            connectionId.append(characters.charAt(randomIndex));
        }

        return connectionId.toString();
    }

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000);
        return String.valueOf(otp);
    }

    public static String generateFullUrl(@NonNull String path) {
        try {
            String contextPath = Optional.ofNullable(UriComponentsBuilder.fromPath("/").build().getPath()).orElse("/");

            return UriComponentsBuilder.fromPath(path)
                    .path(contextPath)
                    .build()
                    .toUriString();
        } catch (Exception e) {
            log.error("generateFullUrl Exception", e);
            return null;
        }
    }

    public static LocalDateTime convertIntToDateTime(Long secondsSinceEpoch) {
        try {
            Instant instant = Instant.ofEpochSecond(secondsSinceEpoch);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return dateTime;
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    public static File convertBase64ToFile(String base64String) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64String);
            File tempFile = File.createTempFile("convertedBase64ToFile", ".png");

            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            return tempFile;
        } catch (Exception e) {
            log.error("convertBase64ToFile Exception", e);
            return null;
        }
    }

    public static File convertMultiPartFileToFile(MultipartFile parts, String ext) {
        try {
            File tempFile = File.createTempFile("convertMultiPartFileToFile", ext);
            parts.transferTo(tempFile);

            return tempFile;
        } catch (Exception e) {
            log.error("convertMultiPartFileToFile Exception", e);
            return null;
        }
    }
}
