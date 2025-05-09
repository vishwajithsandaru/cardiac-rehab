package com.dexter.fyp.backend.dto;

import com.dexter.fyp.backend.enums.Status;

import lombok.Data;

@Data
public class FeedbackResponseDto {

    private Status status;
    private String message;
    private Feedback feedback;

    @Data
    public static class Feedback{
        private Long id;

        private Integer userPlanId;

        private String feedback;

        private int exerciseHelpFulness;

        private int exerciseEffectiveness;

        private int howWellDidyouFare;

        private String suggestedImprovement;

        private int overallRating;
    }

}
