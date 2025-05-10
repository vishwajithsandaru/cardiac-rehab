package com.dexter.fyp.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dexter.fyp.backend.dto.CurrentPlanAndProgressResponse;
import com.dexter.fyp.backend.dto.TimeSeriesGraphDto;
import com.dexter.fyp.backend.dto.UpdateProgressResponse;
import com.dexter.fyp.backend.dto.UserPlanHistoryResponse;
import com.dexter.fyp.backend.dto.WorkoutDto;
import com.dexter.fyp.backend.entity.CustomPlanWorkout;
import com.dexter.fyp.backend.entity.MedicalRecords;
import com.dexter.fyp.backend.entity.Plan;
import com.dexter.fyp.backend.entity.UserPlanProgress;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.entity.UserPlan;
import com.dexter.fyp.backend.entity.Workout;
import com.dexter.fyp.backend.enums.Status;
import com.dexter.fyp.backend.repository.CustomPlanWorkoutRepository;
import com.dexter.fyp.backend.repository.UserPlanProgressRepository;
import com.dexter.fyp.backend.repository.PlanRepository;
import com.dexter.fyp.backend.repository.UserPlanRepository;
import com.dexter.fyp.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final UserPlanRepository userPlanRepository;
    private final CustomPlanWorkoutRepository customPlanWorkoutRepository;
    private final UserPlanProgressRepository userPlanProgressRepository;

    public UserService(UserRepository userRepository, PlanRepository planRepository, UserPlanRepository userPlanRepository, CustomPlanWorkoutRepository customPlanWorkoutRepository, UserPlanProgressRepository userPlanProgressRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.userPlanRepository = userPlanRepository;
        this.customPlanWorkoutRepository = customPlanWorkoutRepository;
        this.userPlanProgressRepository = userPlanProgressRepository;
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Create user and assign plan
    @Transactional
    public User createUser(@Valid User user) {
        validateUser(user);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists");
        }

        // First save: This makes the user entity managed by JPA
        User savedUser = userRepository.save(user);

        // Modifications to the managed 'savedUser' object within the same transaction
        assignPlanToClient(savedUser);
        

        // REMOVE OR COMMENT OUT THIS LINE:
        // This second save triggers a merge operation which likely causes the exception
        // Changes made above should be automatically persisted when the transaction commits.
        // return userRepository.save(savedUser);

        // Return the user object (changes will be saved on transaction commit)
        return savedUser;
    }

    // Assign plan based on medical record risk level (priority: 1 > 2 > 3)
    public void assignPlanToClient(User user) {
        if (user.getMedicalRecords() == null) return;

        int riskLevel = getRiskLevel(user.getMedicalRecords());

        String planName = switch (riskLevel) {
            case 1 -> "Level 1"; // High Risk
            case 2 -> "Level 2";  // Moderate Risk
            case 3 -> "Level 3";     // Low Risk
            default -> null;
        };

        if (planName != null) {
            Plan plan = planRepository.findByName(planName)
                    .orElseThrow(() -> new EntityNotFoundException("Plan not found with name: " + planName));
            UserPlan userPlan = new UserPlan(null, planName, null, null, user, plan, true, null, LocalDateTime.now(), null);
            userPlanRepository.save(userPlan);
        }
    }

    // Risk Level Computation based on scoring system
    private int getRiskLevel(MedicalRecords mr) {
        if (mr == null) return 3; // Default to low risk if no medical records

        double totalScore = 0;
        int fieldsEvaluated = 0;

        // LVEF (%)
        if (mr.getLvef() != null) {
            fieldsEvaluated++;
            if (mr.getLvef() < 40) totalScore += 1;
            else if (mr.getLvef() <= 49) totalScore += 2;
            else totalScore += 3;
        }

        // NYHA Class
        if (mr.getNyhaClass() != null) {
            fieldsEvaluated++;
            String nyha = mr.getNyhaClass().toLowerCase();
            if (nyha.contains("iii") || nyha.contains("iv")) totalScore += 1;
            else if (nyha.contains("ii")) totalScore += 2;
            else totalScore += 3;
        }

        // Blood Pressure (DBP)
        if (mr.getHypertensionLower() != null) {
            fieldsEvaluated++;
            int dbp = mr.getHypertensionLower();
            if (dbp > 100) totalScore += 1;
            else if (dbp >= 90 && dbp <= 99) totalScore += 2;
            else totalScore += 3;
        }

        // LDL Cholesterol
        if (mr.getCholesterolLevel() != null) {
            fieldsEvaluated++;
            double cholesterol = mr.getCholesterolLevel();
            if (cholesterol > 160) totalScore += 1;
            else if (cholesterol >= 100 && cholesterol <= 159) totalScore += 2;
            else totalScore += 3;
        }

        // Oxygen Saturation
        if (mr.getOxygenSaturation() != null) {
            fieldsEvaluated++;
            int oxygenSat = mr.getOxygenSaturation();
            if (oxygenSat < 90) totalScore += 1;
            else if (oxygenSat >= 90 && oxygenSat <= 95) totalScore += 2;
            else totalScore += 3;
        }

        // Smoking Status
        if (mr.getSmokingHistory() != null) {
            fieldsEvaluated++;
            String smoking = mr.getSmokingHistory().toLowerCase();
            if (smoking.contains("current")) totalScore += 1;
            else if (smoking.contains("former")) totalScore += 2;
            else totalScore += 3;
        }

        // Diabetes (HbA1c)
        if (mr.getDiabetes() != null) {
            fieldsEvaluated++;
            double hba1c = mr.getDiabetes();
            if (hba1c > 8.5) totalScore += 1;
            else if (hba1c >= 7.0 && hba1c <= 8.5) totalScore += 2;
            else totalScore += 3;
        }

        // Exercise Tolerance (METs)
        if (mr.getExerciseTolerance() != null) {
            fieldsEvaluated++;
            double mets = mr.getExerciseTolerance();
            if (mets < 5) totalScore += 1;
            else if (mets >= 5 && mets <= 7) totalScore += 2;
            else totalScore += 3;
        }

        // Calculate average score if any fields were evaluated
        if (fieldsEvaluated > 0) {
            double averageScore = totalScore / fieldsEvaluated;

            // Determine risk level based on average score
            if (averageScore < 1.7) return 1;      // High Risk
            else if (averageScore < 2.5) return 2;  // Moderate Risk
            else return 3;                          // Low Risk
        }

        return 3; // Default to low risk if no fields could be evaluated
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Delete user by ID
    public boolean deleteUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    // Update user info
    public User updateUser(User existingUser, User updatedData) {
        if (updatedData.getFirstName() != null) {
            existingUser.setFirstName(updatedData.getFirstName().trim());
        }
        if (updatedData.getLastName() != null) {
            existingUser.setLastName(updatedData.getLastName().trim());
        }
        if (updatedData.getEmail() != null) {
            String newEmail = updatedData.getEmail().trim();
            if (!newEmail.equals(existingUser.getEmail()) &&
                    userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("Another user with this email already exists");
            }
            existingUser.setEmail(newEmail);
        }
        if (updatedData.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedData.getPhoneNumber().trim());
        }
        if (updatedData.getAge() != null) {
            existingUser.setAge(updatedData.getAge());
        }
        if (updatedData.getHeight() != null) {
            existingUser.setHeight(updatedData.getHeight());
        }
        if (updatedData.getWeight() != null) {
            existingUser.setWeight(updatedData.getWeight());
        }

        return userRepository.save(existingUser);
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.trim());
    }

    // Field validation
    private void validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }


    public UserPlanHistoryResponse getPatientPlanHistory(Long userId) {
        List<UserPlan> userPlans = userPlanRepository.findByUser_IdOrderByCreatedDateTimeDesc(userId);

        List<UserPlanHistoryResponse.UserPlans> userPlanHistory = new ArrayList<>();

        for (UserPlan userPlan : userPlans) {
            UserPlanHistoryResponse.UserPlans p = new UserPlanHistoryResponse.UserPlans();
            if(userPlan.getName() == null || userPlan.getName().isEmpty()){
                p.setPlanName(userPlan.getDefaultPlan().getName());
            } else {
                p.setPlanName(userPlan.getName());
            }

            p.setPlanNotes(userPlan.getDoctorNotes());
            p.setPlanDescription(userPlan.getDefaultPlan().getDescription());
            
            
            if(userPlan.getUpdatedBy() != null){
                p.setUpdatedBy(userPlan.getUpdatedBy().getFirstName() + " " + userPlan.getUpdatedBy().getLastName());
            } else {
                p.setUpdatedBy("N/A");
            }
           
            p.setUserPlanId(userPlan.getUserPlanId());

            p.setCreatedDateTime(userPlan.getCreatedDateTime());

            List<WorkoutDto> workouts = new ArrayList<>();

            if(userPlan.getCustomPlanId() != null && !userPlan.getCustomPlanId().isBlank()){
                List<CustomPlanWorkout> customPlanWorkouts = customPlanWorkoutRepository.findByUserPlan_UserPlanId(userPlan.getUserPlanId());
                for(CustomPlanWorkout cpw : customPlanWorkouts){
                    Workout workout = cpw.getWorkout();
                    if(workout != null){
                        WorkoutDto workoutDto = new WorkoutDto();
                        workoutDto.setId(workout.getId());
                        workoutDto.setName(workout.getName());
                        workoutDto.setDescription(workout.getDescription());
                        workoutDto.setDuration(workout.getDuration());
                        workoutDto.setImageUrl(workout.getImageUrl());
                        workoutDto.setDifficulty(workout.getDifficulty());
                        workoutDto.setReps(workout.getReps());
                        workoutDto.setSets(workout.getSets());
                        workoutDto.setVideoUrl(workout.getVideoUrl());
                        workoutDto.setAlternateExerciseId(workout.getAlternateExerciseId());
                        workouts.add(workoutDto);
                    }
                }
            } else {

                List<Workout> planWorkouts = userPlan.getDefaultPlan().getWorkouts();
                for (Workout workout : planWorkouts) {
                    WorkoutDto workoutDto = new WorkoutDto();
                    workoutDto.setId(workout.getId());
                    workoutDto.setName(workout.getName());
                    workoutDto.setDescription(workout.getDescription());
                    workoutDto.setDuration(workout.getDuration());
                    workoutDto.setImageUrl(workout.getImageUrl());
                    workoutDto.setDifficulty(workout.getDifficulty());
                    workoutDto.setReps(workout.getReps());
                    workoutDto.setSets(workout.getSets());
                    workoutDto.setVideoUrl(workout.getVideoUrl());
                    workoutDto.setAlternateExerciseId(workout.getAlternateExerciseId());
                    workouts.add(workoutDto);
                }
                
            }

            p.setWorkouts(workouts);
            
            userPlanHistory.add(p);

        }

        UserPlanHistoryResponse userPlanHistoryResponse = new UserPlanHistoryResponse();
        userPlanHistoryResponse.setUserPlans(userPlanHistory);

        return userPlanHistoryResponse;

    }

    public CurrentPlanAndProgressResponse currentPlanAndProgress(Long userId) throws Exception{
        
        Optional<User> user = userRepository.findById(userId);

        CurrentPlanAndProgressResponse currentPlanAndProgressResponse = new CurrentPlanAndProgressResponse();

        if(user.isPresent()){
            UserPlan userPlan = userPlanRepository.findByUserAndIsCurrentTrue(user.get());
            if(userPlan == null){
                throw new Exception("User does not have a current plan");
            }

            int totalWorkouts = 0;

            List<WorkoutDto> workouts = new ArrayList<>();

            if(userPlan.getCustomPlanId() != null && !userPlan.getCustomPlanId().isBlank()){
                List<CustomPlanWorkout> customPlanWorkouts = userPlan.getCustomPlanWorkouts();
                for(CustomPlanWorkout cpw : customPlanWorkouts){
                    Workout workout = cpw.getWorkout();
                    if(workout != null){
                        WorkoutDto workoutDto = new WorkoutDto();
                        workoutDto.setId(workout.getId());
                        workoutDto.setName(workout.getName());
                        workoutDto.setDescription(workout.getDescription());
                        workoutDto.setDuration(workout.getDuration());
                        workoutDto.setImageUrl(workout.getImageUrl());
                        workoutDto.setDifficulty(workout.getDifficulty());
                        workoutDto.setReps(workout.getReps());
                        workoutDto.setSets(workout.getSets());
                        workoutDto.setVideoUrl(workout.getVideoUrl());
                        workoutDto.setAlternateExerciseId(workout.getAlternateExerciseId());
                        workouts.add(workoutDto);
                    }
                }
                totalWorkouts = customPlanWorkouts.size();
            } else {
                List<Workout> planWorkouts = userPlan.getDefaultPlan().getWorkouts();
                for (Workout workout : planWorkouts) {
                    WorkoutDto workoutDto = new WorkoutDto();
                    workoutDto.setId(workout.getId());
                    workoutDto.setName(workout.getName());
                    workoutDto.setDescription(workout.getDescription());
                    workoutDto.setDuration(workout.getDuration());
                    workoutDto.setImageUrl(workout.getImageUrl());
                    workoutDto.setDifficulty(workout.getDifficulty());
                    workoutDto.setReps(workout.getReps());
                    workoutDto.setSets(workout.getSets());
                    workoutDto.setVideoUrl(workout.getVideoUrl());
                    workoutDto.setAlternateExerciseId(workout.getAlternateExerciseId());
                    workouts.add(workoutDto);
                }
                totalWorkouts = planWorkouts.size();
            }

            
            currentPlanAndProgressResponse.setPlanName(userPlan.getName());
            currentPlanAndProgressResponse.setPlanDescription(userPlan.getDefaultPlan().getDescription());
            currentPlanAndProgressResponse.setDoctorNotes(userPlan.getDoctorNotes());
            currentPlanAndProgressResponse.setUserPlanId(userPlan.getUserPlanId());

            UserPlanProgress userPlanProgress = userPlanProgressRepository.findByUserPlanAndDate(userPlan, LocalDate.now());
            if(userPlanProgress == null){
                currentPlanAndProgressResponse.setCompleted(0);
                currentPlanAndProgressResponse.setTotal(totalWorkouts);
                currentPlanAndProgressResponse.setProgressPercentage(0);

            } else {
                currentPlanAndProgressResponse.setCompleted(userPlanProgress.getCountCompleted());
                currentPlanAndProgressResponse.setTotal(totalWorkouts);
                double progressPercentage = ((double) userPlanProgress.getCountCompleted() / totalWorkouts) * 100;
                currentPlanAndProgressResponse.setProgressPercentage(progressPercentage);
            }

            currentPlanAndProgressResponse.setWorkouts(workouts);

            currentPlanAndProgressResponse.setTodaysDate(LocalDate.now());
            currentPlanAndProgressResponse.setStatus(Status.SUCCESS);
            return currentPlanAndProgressResponse;

        }else{
            throw new Exception("User is not found");
        }

    }

    public UpdateProgressResponse updateProgress(Integer userPlanId, Long userId, LocalDate today) throws Exception {


        Optional<User> user = userRepository.findById(userId);

        UpdateProgressResponse updateProgressResponse = new UpdateProgressResponse();

        if (user.isPresent()) {
            UserPlan userPlan = userPlanRepository.findById(userPlanId)
                    .orElseThrow(() -> new EntityNotFoundException("UserPlan not found with ID: " + userPlanId));
            if(userPlan.getUser() != user.get()){
                throw new Exception("User is not assigned to this plan");
            }
            UserPlanProgress userPlanProgress = userPlanProgressRepository.findByUserPlanAndDate(userPlan, today);

            int count = 0;

            if(userPlan.getCustomPlanId() != null && !userPlan.getCustomPlanId().isBlank()){
                List<CustomPlanWorkout> customPlanWorkouts = customPlanWorkoutRepository.findByUserPlan_UserPlanId(userPlan.getUserPlanId());
                count = customPlanWorkouts.size();
            } else {
                List<Workout> planWorkouts = userPlan.getDefaultPlan().getWorkouts();
                count = planWorkouts.size();
            }

            if(userPlanProgress == null){
                UserPlanProgress newUserPlanProgress = new UserPlanProgress();
                newUserPlanProgress.setUserPlan(userPlan);
                newUserPlanProgress.setCountCompleted(1);
                newUserPlanProgress.setCountTotal(count);
                newUserPlanProgress.setPercentage(100.0 / count);
                newUserPlanProgress.setDate(today);
                userPlanProgressRepository.save(newUserPlanProgress);
                updateProgressResponse.setStatus(Status.SUCCESS);
                updateProgressResponse.setMessage("Progress updated successfully");
                updateProgressResponse.setCompletedCount(1);
                updateProgressResponse.setTotalCount(count);

            }else{

                int completedCount = userPlanProgress.getCountCompleted();
                if(completedCount < count){
                    userPlanProgress.setCountCompleted(completedCount + 1);
                    userPlanProgress.setCountTotal(count);
                    userPlanProgress.setPercentage(((double) (completedCount + 1) / count) * 100);
                    userPlanProgress.setDate(today);
                    userPlanProgressRepository.save(userPlanProgress);
                    updateProgressResponse.setStatus(Status.SUCCESS);
                    updateProgressResponse.setMessage("Progress updated successfully");
                    updateProgressResponse.setCompletedCount(completedCount + 1);
                    updateProgressResponse.setTotalCount(count);
                } else {
                    throw new Exception("All workouts are already completed");
                }

            }
            
        }else{
            throw new Exception("User is not found");
        }

        return updateProgressResponse;

    }

    public TimeSeriesGraphDto getGraphData(Long userId, LocalDate startDate, LocalDate endDate) throws Exception{

        Optional<User> user = userRepository.findById(userId);

        TimeSeriesGraphDto timeSeriesGraphDto = new TimeSeriesGraphDto();

        if(user.isPresent()){
            UserPlan userPlan = userPlanRepository.findByUserAndIsCurrentTrue(user.get());
            if(userPlan == null){
                throw new Exception("User does not have a current plan");
            }

            List<UserPlanProgress> userPlanProgressList = userPlanProgressRepository.findByUserPlanAndDateBetween(userPlan, startDate, endDate, Sort.by("date").ascending());
            
            List<TimeSeriesGraphDto.XYData> graphDataList = new ArrayList<>();

            for(UserPlanProgress userPlanProgress : userPlanProgressList) {
                TimeSeriesGraphDto.XYData xyData = new TimeSeriesGraphDto.XYData();
                xyData.setDate(userPlanProgress.getDate());
                xyData.setPercentage(userPlanProgress.getPercentage());
                graphDataList.add(xyData);
            }

            timeSeriesGraphDto.setStatus(Status.SUCCESS);
            timeSeriesGraphDto.setData(graphDataList);
            return timeSeriesGraphDto;

        }else{
            throw new Exception("User is not found");
        }

    }
    
}