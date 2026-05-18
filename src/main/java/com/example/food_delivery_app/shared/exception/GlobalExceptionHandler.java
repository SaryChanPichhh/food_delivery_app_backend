package com.example.food_delivery_app.shared.exception;

import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(
            Exception ex) {

        log.error("Unhandled exception", ex);

        return ResponseEntity
                .status(500)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message("Internal Server Error")
                                .build()
                );
    }
}
