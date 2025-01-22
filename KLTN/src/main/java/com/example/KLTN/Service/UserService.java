package com.example.KLTN.Service;
import com.example.KLTN.DTOS.Request.UserRequestDTO;
import com.example.KLTN.DTOS.Response.UserResponseDTO;
import com.example.KLTN.Entity.Faculty;
import com.example.KLTN.Entity.Major;
import com.example.KLTN.Entity.Score;
import com.example.KLTN.Entity.User;
import com.example.KLTN.Enum.Role;
import com.example.KLTN.Repository.FacultyRepository;
import com.example.KLTN.Repository.MajorRepository;
import com.example.KLTN.Repository.ScoreRepository;
import com.example.KLTN.Repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
@Service
public class UserService implements UserDetailsService {
    @Lazy
    private final UserRepository userRepository;

    @Lazy
    private final MajorRepository majorRepository;

    @Lazy
    private final ScoreRepository scoreRepository;

    @Lazy
    private final EmailService emailService;


    public UserService(UserRepository userRepository, MajorRepository majorRepository, ScoreRepository scoreRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.majorRepository = majorRepository;
        this.scoreRepository = scoreRepository;
        this.emailService = emailService;
    }

    public User findByStudentId(String studentId) {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with studentId: " + studentId));
    }
    public boolean isAdmin(){
        User currentUser = getCurrentUser();
        return currentUser.getRole() == Role.ADMIN;
    }

    public boolean isEditor(){
        User currentUser = getCurrentUser();
        return currentUser.getRole() == Role.EDITOR;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

    public List<UserResponseDTO> getAllUsers(){
        if (!isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        List<User> userList = userRepository.findAll();
        return userList.stream().map(
                user -> UserResponseDTO.builder()
                        .studentId(user.getStudentId())
                        .role(user.getRole())
                        .studentName(user.getStudentName())
                        .phoneNumber(user.getPhoneNumber())
                        .email(user.getEmail())
                        .id(user.getId())
                        .majorName(user.getMajor().getMajorName())
                        .facultyName(user.getMajor().getFaculty().getFacultyName())
                        .build()
        ).toList();
    }

    // Lấy thông tin các cá nhân User
    public ResponseEntity<UserResponseDTO> getUserDetailsForAdmin(Long id){
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " ));
        if (!isAdmin()){
            throw new RuntimeException("Only admin can access this resource.");
        }
        return new ResponseEntity<>(UserResponseDTO.builder()
                .studentId(existingUser.getStudentId())
                .studentName(existingUser.getStudentName())
                .phoneNumber(existingUser.getPhoneNumber())
                .id(existingUser.getId())
                .role(existingUser.getRole())
                .email(existingUser.getEmail())
                .majorName(existingUser.getMajor().getMajorName())
                .facultyName(existingUser.getMajor().getFaculty().getFacultyName())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<UserResponseDTO> getUserDetails(){
        return new ResponseEntity<>(UserResponseDTO.builder()
                .studentId(getCurrentUser().getStudentId())
                .role(getCurrentUser().getRole())
                .studentName(getCurrentUser().getStudentName())
                .phoneNumber(getCurrentUser().getPhoneNumber())
                .email(getCurrentUser().getEmail())
                .majorName(getCurrentUser().getMajor().getMajorName())
                .facultyName(getCurrentUser().getMajor().getFaculty().getFacultyName())
                .build(), HttpStatus.OK);
    }

    public List<UserResponseDTO> getUsersByRole(Role role){
        if (!isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        List<User> usersList = userRepository.findByRole(role);
        if (usersList == null){
            throw new RuntimeException("Chưa có người dùng thuộc role này!");
        }
        return  usersList.stream().map(
                user -> UserResponseDTO.builder()
                        .studentName(user.getStudentName())
                        .email(user.getEmail())
                        .studentId(user.getStudentId())
                        .phoneNumber(user.getPhoneNumber())
                        .id(user.getId())
                        .role(user.getRole())
                        .majorName(user.getMajor().getMajorName())
                        .facultyName(user.getMajor().getFaculty().getFacultyName())
                        .build()
        ).collect(Collectors.toList());
    }

    public ResponseEntity<UserResponseDTO> updateUser(UserRequestDTO request){
        User existingUser = getCurrentUser();
        existingUser.setStudentName(request.getStudentName());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(existingUser);
        return new ResponseEntity<>(UserResponseDTO.builder()
                .studentId(existingUser.getStudentId())
                .id(existingUser.getId())
                .phoneNumber(existingUser.getPhoneNumber())
                .email(existingUser.getEmail())
                .role(existingUser.getRole())
                .studentName(existingUser.getStudentName())
                .majorName(existingUser.getMajor().getMajorName())
                .facultyName(existingUser.getMajor().getFaculty().getFacultyName())
                .build(),HttpStatus.OK);
    }

    public ResponseEntity<UserResponseDTO> updateRole(Long id, Role role){
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found  " ));
        if (!isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        existingUser.setRole(role);
        userRepository.save(existingUser);
        return new ResponseEntity<>(UserResponseDTO.builder()
                .studentId(existingUser.getStudentId())
                .id(existingUser.getId())
                .phoneNumber(existingUser.getPhoneNumber())
                .email(existingUser.getEmail())
                .role(existingUser.getRole())
                .studentName(existingUser.getStudentName())
                .majorName(existingUser.getMajor().getMajorName())
                .facultyName(existingUser.getMajor().getFaculty().getFacultyName())
                .build(),HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUser(Long id){
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found  " ));
        if (!isAdmin()&&!id.equals(getCurrentUser().getId())){
            throw new RuntimeException("Only admin users can access this resource.");
        }
        userRepository.delete(existingUser);
        return ResponseEntity.ok("Delete user successfully !");
    }

    public ResponseEntity<?> updateMajor(Long majorId){
        User user = getCurrentUser();
        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> new RuntimeException("Major not found " ));
        user.setMajor(major);
        userRepository.save(user);
        List<Score> scoreList = scoreRepository.findByUser(user);
        scoreRepository.deleteAll(scoreList);
        return ResponseEntity.ok("Update Major user successfully !");
    }

    public ResponseEntity<?> ChangeMailRequest(){
        User user = getCurrentUser();
        user.setVerificationCode(generateVerificationCode());
        userRepository.save(user);
        sendVerificationEmail(user);
        return ResponseEntity.ok("Code da gui den mail cua ban");
    }


    public ResponseEntity<?> changeMailVerify(String verifyCode){
        User user = getCurrentUser();
        if (verifyCode.equals(user.getVerificationCode())){
            user.setVerificationCode(null);
            userRepository.save(user);
            return ResponseEntity.ok("VerifyCode successful");
        }
        throw new RuntimeException("VerifyCode is not found");
    }

    public ResponseEntity<?> changeMail(String email){
        User user = getCurrentUser();
        if (userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email da ton tai");
        }
        user.setEmail(email);
        userRepository.save(user);
        return ResponseEntity.ok("Change Mail successful");
    }
    private void sendVerificationEmail(User user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    public String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
