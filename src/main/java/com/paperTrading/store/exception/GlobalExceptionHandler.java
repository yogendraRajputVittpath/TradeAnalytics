package com.paperTrading.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.paperTrading.store.dto.response.ApiResponse;

//import com.user.advertisement.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyInterestedException.class)
    public ResponseEntity<ApiResponse<?>> handleAlreadyInterested(
            AlreadyInterestedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) 
                .body(ApiResponse.error(ex.getMessage(), 403));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), 400));
    }
}
