package com.dexter.fyp.backend.dto;

import lombok.Data;

@Data
public class FeedbackDto {
    
    private Integer userPlanId;

    private String feedback;

    private int exerciseHelpFulness;

    private int exerciseEffectiveness;

    private int howWellDidyouFare;

    private String suggestedImprovement;

    private int overallRating;

}
