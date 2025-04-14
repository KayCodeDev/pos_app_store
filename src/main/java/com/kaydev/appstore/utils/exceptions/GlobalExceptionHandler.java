package com.kaydev.appstore.utils.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kaydev.appstore.models.dto.response.BaseResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    public final BaseResponse response = new BaseResponse();

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableExceptionException(HttpMessageNotReadableException ex,
            WebRequest request) {
        log.error("HttpMessageNotReadableException Occurred", ex.getMessage());

        response.setStatus("error");
        response.setMessage("Invalid request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex,
            WebRequest request) {
        log.error("BadCredentialsException Occurred", ex.getMessage());

        response.setStatus("error");
        response.setMessage("Incorrect username or password");

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
            WebRequest request) {
        log.error("MethodArgumentNotValidException Occurred", ex.getMessage());

        response.setStatus("error");
        ObjectError firstError = ex.getBindingResult().getAllErrors().getFirst();

        response.setMessage(firstError.getDefaultMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationExceptions(ConstraintViolationException ex, WebRequest request) {
        log.error("ConstraintViolationException Occurred", ex.getMessage());

        String error = "";
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            error = cv.getPropertyPath().toString().split("\\.")[2] + " " + cv.getMessage();
            break;
        }

        response.setStatus("error");
        response.setMessage("Validation Error: " + error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException ex,
            RedirectAttributes redirectAttributes) {

        log.error("MultipartException Occurred", ex.getMessage());

        response.setStatus("error");
        response.setMessage("File size exceeds the limit!");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request) {

        log.error("DataIntegrityViolationException Occurred in " + request.getClass().getName(), ex.getMessage());

        response.setStatus("error");
        response.setMessage("Data Integrity Violation: Record already exists");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request) {
        log.error("HttpRequestMethodNotSupportedException Occurred", ex.getMessage());

        response.setStatus("error");
        response.setMessage("Request Method not supported for this request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsatisfiedLinkError.class)
    public ResponseEntity<Object> handleUnsatisfiedLinkError(UnsatisfiedLinkError ex, WebRequest request) {
        log.error("UnsatisfiedLinkError Occurred", ex.getMessage());

        response.setStatus("error");
        response.setMessage("A little glitch, please try again.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Exception Occurred in " + request.getClass().getName(), ex);

        response.setStatus("error");
        response.setMessage("Excepetion Occurred: Internal Server Error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
