package com.dexter.fyp.backend.service;


import org.springframework.stereotype.Service;

import com.dexter.fyp.backend.dto.FeedbackDto;
import com.dexter.fyp.backend.dto.FeedbackResponseDto;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.entity.Feedback;
import com.dexter.fyp.backend.entity.UserPlan;
import com.dexter.fyp.backend.enums.Status;
import com.dexter.fyp.backend.repository.FeedbackRepository;
import com.dexter.fyp.backend.repository.UserPlanRepository;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserPlanRepository userPlanRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserPlanRepository userPlanRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userPlanRepository = userPlanRepository;
    }

    // Submit feedback with validation and optional duplicate prevention
    public GeneralResponse submitFeedback(FeedbackDto feedback) throws Exception{
        validateFeedback(feedback);

        UserPlan userPlan = userPlanRepository.findById(feedback.getUserPlanId())
                .orElseThrow(() -> new Exception("User plan not found with id: " + feedback.getUserPlanId()));

        Feedback newFeedback = new Feedback();
        newFeedback.setUserPlan(userPlan);
        newFeedback.setFeedback(feedback.getFeedback());
        newFeedback.setExerciseEffectiveness(feedback.getExerciseEffectiveness());
        newFeedback.setExerciseHelpFulness(feedback.getExerciseHelpFulness());
        newFeedback.setHowWellDidyouFare(feedback.getHowWellDidyouFare());
        newFeedback.setOverallRating(feedback.getOverallRating());
        newFeedback.setCreatedDateTime(LocalDateTime.now());

        feedbackRepository.save(newFeedback);

        GeneralResponse response = new GeneralResponse();
        response.setStatus(Status.SUCCESS);
        response.setMessage("Feedback submitted successfully");
        return response;
    }

    public FeedbackResponseDto getLatestFeedbackForUserPlan(Integer userPlanId) throws Exception {

        UserPlan userPlan = userPlanRepository.findById(userPlanId)
                .orElseThrow(() -> new IllegalArgumentException("User plan not found with id: " + userPlanId));

        Feedback latestFeedback = feedbackRepository.findTopByUserPlanOrderByCreatedDateTimeDesc(userPlan);

        if(latestFeedback == null) {
            throw new Exception("No feedback found for user plan with id: " + userPlanId);
        }
        FeedbackResponseDto.Feedback fb = new FeedbackResponseDto.Feedback();
        fb.setId(latestFeedback.getId());
        fb.setExerciseEffectiveness(latestFeedback.getExerciseEffectiveness());
        fb.setExerciseHelpFulness(latestFeedback.getExerciseHelpFulness());
        fb.setHowWellDidyouFare(latestFeedback.getHowWellDidyouFare());
        fb.setOverallRating(latestFeedback.getOverallRating());
        fb.setFeedback(latestFeedback.getFeedback());
        fb.setSuggestedImprovement(latestFeedback.getSuggestedImprovement());
        fb.setUserPlanId(latestFeedback.getUserPlan().getUserPlanId());

        FeedbackResponseDto response = new FeedbackResponseDto();
        response.setStatus(Status.SUCCESS);
        response.setMessage("Feedback retrieved successfully");
        response.setFeedback(fb);
        return response;
        
    }
   


    private void validateFeedback(FeedbackDto feedback) {
        if (feedback.getOverallRating() < 1 || feedback.getOverallRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (feedback.getFeedback() == null || feedback.getFeedback().isBlank()) {
            throw new IllegalArgumentException("Comment must not be empty");
        }
    }
}
