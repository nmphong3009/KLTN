package com.example.KLTN.Controller;

import com.example.KLTN.Component.JwtTokenProvider;
import com.example.KLTN.DTOS.Request.ChangePassDTO;
import com.example.KLTN.DTOS.Request.LoginRequest;
import com.example.KLTN.DTOS.Request.RegisterRequest;
import com.example.KLTN.DTOS.Request.VerifyUserDTO;
import com.example.KLTN.Service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")

public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("changePass")
    public ResponseEntity<?> changePass(@RequestBody ChangePassDTO changePassDTO) {
        return authenticationService.changePass(changePassDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.registerUser(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDTO verifyUserDto) {
        return ResponseEntity.ok(authenticationService.verifyUser(verifyUserDto));
    }
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        return ResponseEntity.ok(authenticationService.resendVerificationCode(email));
    }



}
