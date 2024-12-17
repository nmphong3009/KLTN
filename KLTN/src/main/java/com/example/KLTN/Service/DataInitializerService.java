//package com.example.KLTN.Service;
//
//import com.example.KLTN.Entity.Subject;
//import com.example.KLTN.Entity.User;
//import com.example.KLTN.Entity.Score;
//import com.example.KLTN.Repository.SubjectRepository;
//import com.example.KLTN.Repository.UserRepository;
//import com.example.KLTN.Repository.ScoreRepository;
//import com.github.javafaker.Faker;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Random;
//@RequiredArgsConstructor
//@Service
//public class DataInitializerService {
//
//    @Lazy
//    private final PasswordEncoder passwordEncoder;
//
//    @Lazy
//    private final UserRepository userRepository;
//
//    @Lazy
//    private final SubjectRepository subjectRepository;
//
//    @Lazy
//    private final ScoreRepository scoreRepository;
//
//    private Faker faker = new Faker();
//    private Random random = new Random();
//
//    // Phương thức này sẽ được gọi khi ứng dụng bắt đầu
//    @Transactional
//    public void initData() {
//        createSubjects();
//        createUsers();
//        createScores();
//    }
//
//    // Tạo một số môn học mẫu ngẫu nhiên
//    private void createSubjects() {
//        for (int i = 0; i < 50; i++) {
//            Subject subject = Subject.builder()
//                    .subjectId("SUB" + faker.number().randomNumber(4, true)) // Random subjectId
//                    .subjectName(faker.educator().course()) // Random course name
//                    .credit(faker.number().numberBetween(1, 4)) // Random credits between 3 and 6
//                    .build();
//            subjectRepository.save(subject);
//        }
//    }
//
//    // Tạo một số học sinh mẫu ngẫu nhiên
//    private void createUsers() {
//        for (int i = 0; i < 100; i++) {
//            User user = User.builder()
//                    .studentId("U" + faker.number().randomNumber(4, true)) // Random studentId
//                    .studentName(faker.name().fullName()) // Random name
//                    .email(faker.internet().emailAddress()) // Random email
//                    .password(passwordEncoder.encode("123")) // Random password
//                    .phoneNumber(faker.phoneNumber().cellPhone()) // Random phone number
//                    .registeredDay(LocalDateTime.now())
//                    .enabled(true)
//                    .role(random.nextBoolean() ? com.example.KLTN.Enum.Role.USER : com.example.KLTN.Enum.Role.ADMIN)
//                    .build();
//            userRepository.save(user);
//        }
//    }
//
//    // Tạo một số điểm mẫu ngẫu nhiên cho các học sinh
//    private void createScores() {
//        var users = userRepository.findAll();
//        var subjects = subjectRepository.findAll();
//
//        for (User user : users) {
//            for (Subject subject : subjects) {
//                // Tạo điểm ngẫu nhiên từ 0 đến 10 và làm tròn đến 2 chữ số sau dấu phẩy
//                double grade = Math.round((random.nextDouble() * 10) * 100.0) / 100.0;
//
//                Score score = Score.builder()
//                        .user(user)
//                        .subject(subject)
//                        .grade(grade) // Điểm đã làm tròn
//                        .build();
//                scoreRepository.save(score);
//            }
//        }
//    }
//}
