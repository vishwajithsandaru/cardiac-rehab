package com.dexter.fyp.backend.service;

import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.entity.MedicalRecords;
import com.dexter.fyp.backend.entity.Plan;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.entity.Workout;
import com.dexter.fyp.backend.repository.BookingRepository;
import com.dexter.fyp.backend.repository.DoctorRepository;
import com.dexter.fyp.backend.repository.FeedbackRepository;
import com.dexter.fyp.backend.repository.MedicalRecordsRepository;
import com.dexter.fyp.backend.repository.PlanRepository;
import com.dexter.fyp.backend.repository.UserRepository;
import com.dexter.fyp.backend.repository.WorkoutRepository;
import com.dexter.fyp.backend.entity.Booking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DataPopulationService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PlanRepository planRepository;
    private final WorkoutRepository workoutRepository;
    private final BookingRepository bookingRepository;
    private final FeedbackRepository feedbackRepository;
    private final MedicalRecordsRepository medicalRecordsRepository;

    private final UserService userService;
    // PlanService and BookingService are not directly used here after refactoring User creation
    // private final PlanService planService;
    // private final BookingService bookingService;

    public DataPopulationService(UserRepository userRepository, DoctorRepository doctorRepository,
                                 PlanRepository planRepository, WorkoutRepository workoutRepository,
                                 BookingRepository bookingRepository, FeedbackRepository feedbackRepository,
                                 MedicalRecordsRepository medicalRecordsRepository,
                                 UserService userService /*, PlanService planService, BookingService bookingService */) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.planRepository = planRepository;
        this.workoutRepository = workoutRepository;
        this.bookingRepository = bookingRepository;
        this.feedbackRepository = feedbackRepository;
        this.medicalRecordsRepository = medicalRecordsRepository;
        this.userService = userService;
        // this.planService = planService; // Not strictly needed if UserService handles plan assignment
        // this.bookingService = bookingService; // Not strictly needed if not creating bookings here directly
    }

    // @Transactional
    // public void populateData() {
    //     // Clear existing data first
    //     clearAllData();

    //     // 1. Create Workouts with scheduled dates
    //     List<Workout> createdWorkouts = createSampleWorkouts();

    //     // 3. Create Medical Records
    //     List<MedicalRecords> createdMedicalRecords = createSampleMedicalRecords();
    //     // Save Medical Records explicitly BEFORE creating users that reference them
    //     medicalRecordsRepository.saveAll(createdMedicalRecords);

    //     // 4. Create Users and assign Plans (UserService handles assignment)
    //     createSampleUsers(createdMedicalRecords); // userService.createUser is called within this method


    //     // 6. Create Bookings
    //     createSampleBookings(userRepository.findAll(), doctorRepository.findAll());

    //     // 7. Create Feedback
    //     createSampleFeedback(); // Feedback doesn't seem linked to users in the current entity
    // }

    @Transactional
    public void clearAllData() {
        // Clear data from tables, respecting foreign key constraints
        bookingRepository.deleteAllInBatch();
        feedbackRepository.deleteAllInBatch(); // Clear feedback first
        // Remove user associations before deleting users if needed (e.g., clearing planHistory join table)
        // Or rely on Cascade settings. Clear users which might cascade to MedicalRecords.
        userRepository.deleteAllInBatch();
        doctorRepository.deleteAllInBatch(); // Clear doctors (and their availability via cascade)
        planRepository.deleteAllInBatch(); // Clear plans (and plan_workout join table via cascade)
        workoutRepository.deleteAllInBatch(); // Clear remaining workouts
        medicalRecordsRepository.deleteAllInBatch(); // Clear any remaining medical records explicitly
    }


    // Helper to find workout by name from the created list
    private Workout findWorkoutByName(List<Workout> workouts, String name) {
        return workouts.stream()
                .filter(workout -> workout.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Workout not found during plan assignment: " + name));
    }

    private List<MedicalRecords> createSampleMedicalRecords() {
        List<MedicalRecords> medicalRecords = new ArrayList<>();

        // Low Risk - Ensure all relevant fields have values
        medicalRecords.add(new MedicalRecords(
                null, 55, "Class I", 120, 80, 150.0, 98, "Never Smoked", 5.5, 8.0,
                "None", null, "None", "Active", new ArrayList<>(Collections.singletonList("Occasional Palpitations"))
        ));

        // Moderate Risk - Matching the JSON example
        medicalRecords.add(new MedicalRecords(
                null, 45, "Class II", 145, 95, 180.0, 93, "Former Smoker", 7.5, 6.0,
                "Myocardial Infarction", LocalDate.now().minusYears(2), "Occasional", "Moderate",
                new ArrayList<>(Collections.singletonList("Class II Heart Failure"))
        ));

        // High Risk - Ensure all relevant fields have values
        medicalRecords.add(new MedicalRecords(
                null, 35, "Class III", 165, 105, 220.0, 88, "Current Smoker", 9.0, 4.0,
                "CABG", LocalDate.now().minusMonths(6), "Heavy", "Light",
                new ArrayList<>(Arrays.asList("Angina", "Class IV Heart Failure"))
        ));

        // Minimal/Default - Provide some defaults for fields that might otherwise be null
        medicalRecords.add(new MedicalRecords(
                null, 60, "Class I", 110, 70, 120.0, 99, "Never Smoked", 5.0, 9.0,
                "None", null, "None", "Sedentary", new ArrayList<>()
        ));

        // Save medical records BEFORE returning them
        return medicalRecordsRepository.saveAll(medicalRecords);
    }


    private void createSampleBookings(List<User> users, List<Doctor> doctors) {
        if (users.isEmpty() || doctors.isEmpty()) {
            System.out.println("Not enough users or doctors to create sample bookings.");
            return;
        }

        // Use the already fetched/created users and doctors
        User user1 = users.stream().filter(u -> "alice.smith@example.com".equals(u.getEmail())).findFirst().orElse(null);
        User user2 = users.stream().filter(u -> "bob.johnson@example.com".equals(u.getEmail())).findFirst().orElse(null);
        Doctor doctor1 = doctors.stream().filter(d -> "emma.white@example.com".equals(d.getEmail())).findFirst().orElse(null);
        Doctor doctor2 = doctors.stream().filter(d -> "chris.blue@example.com".equals(d.getEmail())).findFirst().orElse(null);

        if (user1 == null || user2 == null || doctor1 == null || doctor2 == null) {
            System.out.println("Could not find specific sample users or doctors to create bookings.");
            return;
        }

        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Future Confirmed, Unpaid
        bookings.add(new Booking(null, user1, doctor1, new Booking.TimeSlot(now.plusDays(5).withHour(10).withMinute(0), now.plusDays(5).withHour(10).withMinute(30)), "Follow-up appointment", 50.0f, 1, false));
        // Future Pending, Unpaid
        bookings.add(new Booking(null, user2, doctor2, new Booking.TimeSlot(now.plusDays(7).withHour(14).withMinute(0), now.plusDays(7).withHour(14).withMinute(45)), "Initial consultation", 75.0f, 0, false));
        // Past Completed, Paid
        bookings.add(new Booking(null, user1, doctor2, new Booking.TimeSlot(now.minusDays(2).withHour(11).withMinute(0), now.minusDays(2).withHour(11).withMinute(30)), "Previous check-up", 60.0f, 1, true)); // Assuming status 1 means completed if in past
        // Future Cancelled
        bookings.add(new Booking(null, user2, doctor1, new Booking.TimeSlot(now.plusDays(3).withHour(16).withMinute(0), now.plusDays(3).withMinute(30)), "Consultation", 50.0f, -1, false)); // Status -1 for cancelled

        bookingRepository.saveAll(bookings);
    }

    

    public void populateWorkoutsAndPlans(){
        
        Workout gentleMarch = new Workout(
                1L,
                "Gentle March",
                "To promote circulation, improve cardiovascular endurance, and increase lower-body mobility in a low-impact, safe manner suitable for individuals recovering from cardiac events.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex+1.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/1.Cardiac+Rehab+at+Home+-+Level+1+Programme+Gentle+March+warm+up+1.mp4", null, 0,
                null, null, 3, 20
        );

        Workout toeTaps = new Workout(
                2L,
                "Toe Taps",
                "To promote circulation in the lower legs, improve ankle mobility, and support gentle cardiovascular activity in a safe, seated position suitable for individuals in cardiac rehabilitation.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex+2.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/2.Cardiac+Rehab+at+Home+-+Level+1+Programme+toe+tap+warm+up+2.mp4", null, 0,
                null, null, 3, 20
        );

        Workout heelDigs = new Workout(
                3L,
                "Heel Digs",
                "To improve lower-body circulation, ankle mobility, and coordination with low-impact, safe movement.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex3.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/3.Cardiac+Rehab+at+Home+-+Level+1+Programme+Heel+digs+warm+up+3.mp4", null, 0,
                null, null, 3, 20
        );

        Workout takingTheLegToTheSide = new Workout(
                4L,
                "Taking the Leg to the Side",
                "To strengthen the muscles of the hips and outer thighs, improve balance, and support mobility—all in a low-impact, heart-safe way appropriate for cardiac rehabilitation.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex+4.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/4.Cardiac+Rehab+at+Home+-+Level+1+Programme+taking+the+leg+to+the+side+exercise+1.mp4", null, 0,
                null, null, 3, 15
        );

        Workout hamstringCurls = new Workout(
                5L,
                "Hamstring Curls",
                "To gently strengthen the hamstrings (back of the thigh), promote circulation, and improve coordination and balance in a low-impact format appropriate for cardiac rehab.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex+5.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/5.Cardiac+Rehab+at+Home+-+Level+1+Programme+Hand+raise+exercise+2.mp4", null, 0,
                null, null, 3, 15
        );

        Workout kneeRaises = new Workout(
                6L,
                "Knee Raises",
                "To gently strengthen the hip flexors and thighs, improve balance and coordination, and provide light cardiovascular stimulation in a heart-safe way.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex+6.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/6.cardiac+rehab+hamstring+curls+exercise+3+level+1.mp4", null, 0,
                null, null, 3, 10
        );

        Workout handRaises = new Workout(
                7L,
                "Hand Raises",
                "Hand Raises are designed to help improve upper body strength, mobility, and circulation, particularly beneficial for individuals in cardiac rehabilitation. This exercise engages the shoulder, arm, and core muscles, while also promoting joint flexibility.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex7.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/7.Cardiac+rehab+knee+raises+exercise+4+level+1.mp4", null, 0,
                null, null, 3, 15
        );

        

        Workout heelTaps = new Workout(
                8L,
                "Heal Taps",
                "To promote circulation, improve cardiovascular endurance, and increase lower-body mobility in a low-impact, safe manner suitable for individuals recovering from cardiac events.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex+1.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/1.Cardiac+Rehab+at+Home+-+Level+1+Programme+Gentle+March+warm+up+1.mp4", null, 0,
                null, null, 3, 15
        );

        Workout loosenUpTheSpine = new Workout(
                9L,
                "Loosen Up the Spine",
                "Heel Taps are a simple yet effective exercise designed to strengthen the lower body, particularly the quadriceps, hamstrings, and calves, while promoting balance and coordination. This exercise is especially beneficial for individuals in cardiac rehabilitation as it encourages leg movement without excessive strain.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/level+1+ex9.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+1/9.cardiac+rehab+heel+tap+to+the+front+warm+down+2+level+1.mp4", null, 0,
                null, null, 3, 20
        );

        Workout shoulderRoll = new Workout(
                10L,
                "Shoulder Roll",
                "Shoulder Rolls are a simple yet effective exercise designed to improve shoulder mobility, reduce tension in the neck and upper back, and enhance circulation in the upper body. This exercise is particularly beneficial for individuals in cardiac rehabilitation as it encourages gentle movement, promotes relaxation, and helps alleviate stiffness and tightness in the shoulders.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex2.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/2.cardiac+rehab+shoulder+roll+warm+up+2+level+2.mp4", null, 0,
                null, null, 3, 20
        );

        
        Workout uprightRows = new Workout(
                11L,
                "Upright Rows",
                "Upright Rows are a strengthening exercise designed to target the shoulders, trapezius, and upper back muscles. This exercise is particularly beneficial for individuals in cardiac rehabilitation, as it helps improve upper body strength, posture, and muscular endurance. It is a low-impact movement that helps enhance muscle tone without putting too much stress on the cardiovascular system.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex+4.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/4.cardiac+rehab+upright+row+exercise+1+level+2.mp4", null, 0,
                null, null, 3, 15
        );

        Workout sideSteps = new Workout(
                12L,
                "Side Steps",
                "Side Steps are a simple yet effective exercise designed to improve leg strength, balance, and coordination. This exercise is especially beneficial for individuals in cardiac rehabilitation, as it enhances lower body mobility and helps promote circulation in the legs while engaging the hip, thigh, and calf muscles.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex5.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/5.cardiac+rehab+side+step+exercise+2+level+2.mp4", null, 0,
                null, null, 3, 20
        );

        Workout backwardLunges = new Workout(
                13L,
                "Backward Lungus",
                "Backward Lunges are a powerful lower body exercise that focuses on improving leg strength, balance, and coordination. This exercise is beneficial for individuals in cardiac rehabilitation, as it enhances functional mobility, strengthens the glutes, quads, and hamstrings, and encourages improved posture and stability.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex+7.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/7.cardica+rehab+backward+lung+exercise+4+level+2.mp4", null, 0,
                null, null, 3, 20
        );

        Workout lateralRaises = new Workout(
                14L,
                "Lateral Raises",
                "Lateral Raises are a simple yet effective exercise designed to strengthen the shoulders and improve upper body stability and muscle endurance. This exercise is beneficial for individuals in cardiac rehabilitation, as it helps increase muscle tone and postural support, all while being gentle on the heart and cardiovascular system.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex+7.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/7.cardica+rehab+backward+lung+exercise+4+level+2.mp4", null, 0,
                null, null, 3, 20
        );

        Workout bicepCurls = new Workout(
                15L,
                "Bicep Curls",
                "Bicep Curls are a classic upper body exercise designed to strengthen the biceps (the muscles in the upper arm). This exercise is especially beneficial for individuals in cardiac rehabilitation, as it helps improve muscle endurance, strength, and circulation in the arms, all while being a low-impact movement that is easy to control.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex+7.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/8.cardiac+rehab+bicep+curl+exercise+5+level+2.mp4", null, 0,
                null, null, 3, 15
        );

        Workout toeTapsWithBicepCurls = new Workout(
                16L,
                "Toe Taps with Bicep Curls",
                "Toe Taps with Bicep Curls combine a lower body movement (toe taps) with an upper body exercise (bicep curls), helping to improve both muscular strength and coordination. This combined movement benefits individuals in cardiac rehabilitation by promoting circulation, improving balance, and engaging multiple muscle groups without excessive strain on the heart.",
                "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/level+2+ex2.jpg", "https://smartcardiacfypdex.s3.eu-north-1.amazonaws.com/9.cardiac+rehab+toe+tap+with+the+bicep+curl+warm+down+1+level+2.mp4", null, 0,
                null, null, 3, 20
        );

        Plan level1 =  new Plan(
                null,
                "Level 1",
                "A gentle and low-impact rehabilitation program to help you safely begin recovery",
                "Developed in collaboration with professional cardiac rehabilitation specialists, this beginner-level program provides a safe and structured recovery path. It focuses on low-impact exercises to enhance flexibility, heart health, and endurance. Ideal for patients in the early stages of rehabilitation.",
                15,
                List.of(gentleMarch, toeTaps, heelDigs, takingTheLegToTheSide, hamstringCurls, handRaises, toeTaps, heelTaps)
        );

        Plan level2 = new Plan(
                null,
                "Level 2",
                "A balanced program with slightly more activity to rebuild strength and stamina",
                "This intermediate-level plan was carefully designed by medical experts to support patients progressing from Level 1. It includes moderately intense exercises to build strength, stamina, and cardiovascular fitness, while still prioritizing safety and recovery pace.",
                15,
                List.of(loosenUpTheSpine, shoulderRoll, heelDigs, uprightRows, sideSteps, backwardLunges, lateralRaises, bicepCurls, toeTapsWithBicepCurls, kneeRaises)
        );

        Plan level3 = new Plan(
                null,
                "Level 3",
                "A higher-intensity plan for full recovery and heart health maintenance",
                "Formulated under the guidance of licensed physiotherapists and cardiac experts, this advanced program focuses on improving endurance and strength. It prepares low-risk patients for a return to daily physical activity with confidence, ensuring long-term heart health and prevention.",
                20,
                List.of(gentleMarch, toeTaps, heelDigs, uprightRows, sideSteps, backwardLunges, heelTaps)
        );

        planRepository.saveAll(List.of(level1, level2, level3));

    }

}