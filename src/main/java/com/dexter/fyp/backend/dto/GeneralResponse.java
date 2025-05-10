package com.dexter.fyp.backend.dto;

import java.util.Map;

import com.dexter.fyp.backend.enums.Status;

import lombok.Data;

@Data
public class GeneralResponse {
    private Status status;
    private String message;
    private Map<String, String> errors;
}
