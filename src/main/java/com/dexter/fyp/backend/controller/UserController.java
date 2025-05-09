package com.dexter.fyp.backend.controller;


import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.dexter.fyp.backend.dto.CurrentPlanAndProgressResponse;
import com.dexter.fyp.backend.dto.TimeSeriesGraphDto;
import com.dexter.fyp.backend.dto.UpdateProgressResponse;
import com.dexter.fyp.backend.dto.UserPlanHistoryResponse;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.enums.Status;
import com.dexter.fyp.backend.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not fsound"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID " + id);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID " + id));

        User updatedUser = userService.updateUser(existingUser, user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("{id}/currentPlanAndProgress")
    public ResponseEntity<CurrentPlanAndProgressResponse> getCurrentPlanAndProgress(@PathVariable Long id) {
        try{
            CurrentPlanAndProgressResponse response = userService.currentPlanAndProgress(id);
            return ResponseEntity.ok(response);
        }catch(Exception e){
            CurrentPlanAndProgressResponse response = new CurrentPlanAndProgressResponse();
            response.setStatus(Status.FAIL);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("{id}/planHistory")
    public ResponseEntity<UserPlanHistoryResponse> getPatientPlanHistory(@PathVariable Long id) {
        try {
            UserPlanHistoryResponse history = userService.getPatientPlanHistory(id);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("{id}/updateProgress/{userPlanId}")
    public ResponseEntity<UpdateProgressResponse> updateProgress(@PathVariable Long id, @PathVariable Integer userPlanId){

        try {
            UpdateProgressResponse response = userService.updateProgress(userPlanId, id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            UpdateProgressResponse response = new UpdateProgressResponse();
            response.setMessage(e.getMessage());
            response.setStatus(Status.FAIL);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            UpdateProgressResponse response = new UpdateProgressResponse();
            response.setMessage(e.getMessage());
            response.setStatus(Status.FAIL);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @GetMapping("{id}/graphData")
    public ResponseEntity<TimeSeriesGraphDto> getGraphData(@PathVariable Long id,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        try {
           TimeSeriesGraphDto graphData = userService.getGraphData(id, startDate, endDate);
           return ResponseEntity.ok(graphData);
        } catch (Exception e) {
            TimeSeriesGraphDto graphData = new TimeSeriesGraphDto();
            graphData.setStatus(Status.FAIL);
            graphData.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(graphData);
        }
    }

    
}
