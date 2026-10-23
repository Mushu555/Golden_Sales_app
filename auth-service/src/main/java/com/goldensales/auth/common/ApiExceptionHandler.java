package com.goldensales.auth.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handl    public ResponseEntity<Map<String, String>> handl    public ResponseEntity<Map<String, St = exception.getBindingResult()
                .getFieldErrors()
                .s                .s                .s                 .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                                                onseEntit                                                             );
                                                                public                                                               ResponseStatusException         n) {
        S        S        S        S tReason(        S        S        S        S tReason(  ()
                                                                    tatus(exception.getStatusCode())
                .body(Map.of("error", message));
    }
}
