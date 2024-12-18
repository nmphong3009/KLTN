//package com.example.KLTN.Service;
//
//import com.example.KLTN.Entity.Semester;
//import com.example.KLTN.Entity.Subject;
//import com.example.KLTN.Entity.User;
//import com.example.KLTN.Entity.Score;
//import com.example.KLTN.Repository.SemesterRepository;
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
//import java.util.ArrayList;
//import java.util.List;
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
//    @Lazy
//    private final SemesterRepository semesterRepository;
//
//    private Faker faker = new Faker();
//    private Random random = new Random();
//
//    // Phương thức này sẽ được gọi khi ứng dụng bắt đầu
//    @Transactional
//    public void initData() {
//        createSubjects();
//        createUsers();
//        createSemesters();
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
//        for (int i = 1; i <= 50; i++) { // Tạo 50 user
//            User user = User.builder()
//                    .studentId("user" + i) // studentId bắt đầu từ user1 đến user50
//                    .studentName(faker.name().fullName()) // Tạo tên ngẫu nhiên
//                    .email(faker.internet().emailAddress()) // Tạo email ngẫu nhiên
//                    .password(passwordEncoder.encode("123")) // Mã hóa password với "123"
//                    .phoneNumber(faker.phoneNumber().cellPhone()) // Tạo số điện thoại ngẫu nhiên
//                    .registeredDay(LocalDateTime.now()) // Ngày đăng ký là hiện tại
//                    .enabled(true) // Người dùng đã được kích hoạt
//                    .role(com.example.KLTN.Enum.Role.USER) // Gán role là USER
//                    .build();
//            userRepository.save(user); // Lưu vào cơ sở dữ liệu
//        }
//    }
//
//    private void createSemesters() {
//        for (int i = 1; i <= 4; i++) {
//            Semester semester = Semester.builder()
//                    .semesterName("Semester " + i)
//                    .build();
//            semesterRepository.save(semester);
//        }
//    }
//
//    private void createScores() {
//        List<User> users = userRepository.findAll();
//        List<Subject> subjects = subjectRepository.findAll();
//        List<Semester> semesters = semesterRepository.findAll();
//
//        for (User user : users) {
//            // Lưu trữ các môn học mà học sinh đã đăng ký
//            List<Subject> subjectsTaken = new ArrayList<>();
//
//            // Đảm bảo ít nhất 5 môn cho mỗi học kỳ
//            for (Semester semester : semesters) {
//                // Lưu các môn học có thể chọn (loại bỏ các môn đã học)
//                List<Subject> availableSubjects = new ArrayList<>(subjects);
//                availableSubjects.removeAll(subjectsTaken);  // Loại bỏ các môn học đã học trước đó
//
//                // Nếu có ít nhất 5 môn học còn lại
//                if (availableSubjects.size() >= 5) {
//                    // Chọn 5 môn học ngẫu nhiên từ danh sách availableSubjects
//                    List<Subject> selectedSubjects = new ArrayList<>();
//                    for (int i = 0; i < 5; i++) {
//                        Subject subject = availableSubjects.get(random.nextInt(availableSubjects.size()));
//                        selectedSubjects.add(subject);
//                        availableSubjects.remove(subject);  // Loại bỏ môn học đã chọn
//                    }
//
//                    // Tạo điểm cho mỗi môn học trong kỳ học này
//                    for (Subject subject : selectedSubjects) {
//                        double grade = Math.round((random.nextDouble() * 10) * 100.0) / 100.0;
//
//                        // Tạo điểm cho học sinh, môn học và kỳ học
//                        Score score = Score.builder()
//                                .user(user)
//                                .subject(subject)
//                                .semester(semester)
//                                .grade(grade)
//                                .build();
//                        scoreRepository.save(score);
//
//                        // Đánh dấu môn học này là đã học
//                        subjectsTaken.add(subject);
//                    }
//                }
//            }
//        }
//    }
//
//}
