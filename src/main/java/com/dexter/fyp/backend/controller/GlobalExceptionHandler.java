package com.dexter.fyp.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .body(response); // Return readable body
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handle(Exception ex) {

        GeneralResponse response = new GeneralResponse();
        response.setStatus(Status.FAIL);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response); // Return readable body
    }

}
