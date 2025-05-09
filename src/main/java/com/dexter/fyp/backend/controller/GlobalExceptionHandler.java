package com.dexter.fyp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handle(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getReason()); // Return readable body
    }
}
