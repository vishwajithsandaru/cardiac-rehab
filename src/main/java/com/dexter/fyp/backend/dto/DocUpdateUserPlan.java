package com.dexter.fyp.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class DocUpdateUserPlan {
    
    private Long userId;
    private String planName;
    private String doctorNote;
    private List<Long> workoutIds;

}
