package com.example.KLTN.Service;

import com.example.KLTN.Component.JwtTokenProvider;
import com.example.KLTN.DTOS.Request.*;
import com.example.KLTN.DTOS.Response.UserResponseDTO;
import com.example.KLTN.Entity.Major;
import com.example.KLTN.Entity.User;
import com.example.KLTN.Enum.Role;
import com.example.KLTN.Repository.MajorRepository;
import com.example.KLTN.Repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final UserService userService;
    private final MajorRepository majorRepository;

    public AuthenticationService(UserRepository userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, EmailService emailService, MajorRepository majorRepository) {
        this.userRepository = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.userService = userService;
        this.majorRepository = majorRepository;
    }
    public ResponseEntity<UserResponseDTO> registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByStudentId(registerRequest.getStudentId()).isPresent()) {
            throw new RuntimeException("idCard already exists!");
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords don't match");
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("email already exists!");
        }
        Major major = majorRepository.findById(registerRequest.getMajorId())
                .orElseThrow(() -> new RuntimeException("Major not found  " ));
        User user = new User();
        user.setStudentId(registerRequest.getStudentId());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER); // Mặc định role là USER
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        user.setEmail(registerRequest.getEmail());
        user.setStudentName(registerRequest.getStudentName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setMajor(major);
        sendVerificationEmail(user);
        userRepository.save(user);
        return new ResponseEntity<>(UserResponseDTO.builder()
                .email(user.getEmail())
                .build(),HttpStatus.OK);
    }

    public ResponseEntity<?> changePass(ChangePassDTO changePassDTO) {
        User user = userService.getCurrentUser();
        if (!passwordEncoder.matches(changePassDTO.getOldPass(), user.getPassword())){
            return new ResponseEntity<>("OldPass don't match",HttpStatus.BAD_REQUEST);
        }
        if (!changePassDTO.getPassword().equals(changePassDTO.getConfirmPassword())) {
            return new ResponseEntity<>("Passwords don't match",HttpStatus.UNPROCESSABLE_ENTITY);
        }
        user.setPassword(passwordEncoder.encode(changePassDTO.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Change pass successful");
    }


    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with idCard: " + request.getStudentId()));
        if  (!user.isEnabled()){
            throw new RuntimeException("Account not verified. Please verify your account");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getStudentId(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed");
        }
        String token = jwtTokenProvider.generateToken(user);
        String role = user.getRole().toString();
        return ResponseEntity.ok(Map.of("message", "Login successful", "token", token, "role", role));
    }

    public ResponseEntity<?> verifyUser(VerifyUserDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email not found with email: " + request.getEmail()));
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Verification code has expires");
        }
        if (user.getVerificationCode().equals(request.getVerificationCode())){
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            userRepository.save(user);
        }
        return ResponseEntity.ok("User authentication successful");
    }

    public ResponseEntity<?> resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found with email: " + email));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
        sendVerificationEmail(user);
        userRepository.save(user);
        return ResponseEntity.ok("Verification code sent");
    }

    public ResponseEntity<?> forgotPassWord(ForgotPassRequest request){
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with idCard: " + request.getStudentId()));
        if (user.getEmail().equals(request.getEmail())){
            String newPassword = generateVerificationCode();
            sendPassEmail(user, newPassword);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Mk da duoc gui den Mail cua ban");
        }
        return ResponseEntity.badRequest().body("Email khong hop le");
    }

    private void sendPassEmail(User user, String newPass) { //TODO: Update with company logo
        String subject = "Send New PassWord";
        String newPassWord = "New PassWord " + newPass;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + newPassWord + "</p>"
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

