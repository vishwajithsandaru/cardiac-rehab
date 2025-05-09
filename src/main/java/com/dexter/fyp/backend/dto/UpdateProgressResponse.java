package com.dexter.fyp.backend.dto;

import com.dexter.fyp.backend.enums.Status;

import lombok.Data;

@Data
public class UpdateProgressResponse {
    
    private Status status;
    private int totalCount;
    private int completedCount;
    private String message;

}
