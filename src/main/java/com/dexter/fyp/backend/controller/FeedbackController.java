package com.dexter.fyp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.FeedbackDto;
import com.dexter.fyp.backend.dto.FeedbackResponseDto;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.enums.Status;
import com.dexter.fyp.backend.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // Submit new feedback
    @PostMapping
    public ResponseEntity<GeneralResponse> submitFeedback(@RequestBody FeedbackDto feedback) {
        try {
            return ResponseEntity.ok(feedbackService.submitFeedback(feedback));
        } catch (Exception e) {
            GeneralResponse response = new GeneralResponse();
            response.setStatus(Status.FAIL);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/userPlan/{userPlanId}")
    public ResponseEntity<FeedbackResponseDto> getLatestFeedbackByUserPlanId(@PathVariable Integer userPlanId) {
        try {
            FeedbackResponseDto feedbackResponse = feedbackService.getLatestFeedbackForUserPlan(userPlanId);
            return ResponseEntity.ok(feedbackResponse);
        } catch (Exception e) {
            FeedbackResponseDto response = new FeedbackResponseDto();
            response.setStatus(Status.FAIL);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
