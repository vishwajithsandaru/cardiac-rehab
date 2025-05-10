package com.dexter.fyp.backend.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.enums.Status;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GeneralResponse> handle(ResponseStatusException ex) {

        GeneralResponse response = new GeneralResponse();
        response.setStatus(Status.FAIL);
        response.setMessage(ex.getReason());

        return ResponseEntity.status(ex.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handle(Exception ex) {

        GeneralResponse response = new GeneralResponse();
        response.setStatus(Status.FAIL);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response); 
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GeneralResponse> handle(IllegalArgumentException ex) {

        GeneralResponse response = new GeneralResponse();
        response.setStatus(Status.FAIL);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response); 
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse> handle(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage(),
                (existing, replacement) -> existing 
            ));

        GeneralResponse response = new GeneralResponse();
        response.setStatus(Status.FAIL);
        response.setMessage("Validation failed for one or more fields.");
        response.setErrors(fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
