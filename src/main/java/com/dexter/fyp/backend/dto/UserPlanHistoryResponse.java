package com.dexter.fyp.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UserPlanHistoryResponse {
    

    private List<UserPlans> userPlans;

    @Data
    public static class UserPlans{
        private Integer userPlanId;
        private String planName;
        private String planDescription;
        private String planNotes;
        private boolean isCurrent;
        private LocalDateTime createdDateTime;
        private String updatedBy;
        private List<WorkoutDto> workouts;
    }

}
