package com.example.KLTN.Service;
import com.example.KLTN.DTOS.Request.UserRequestDTO;
import com.example.KLTN.DTOS.Response.UserResponseDTO;
import com.example.KLTN.Entity.User;
import com.example.KLTN.Enum.Role;
import com.example.KLTN.Repository.UserRepository;
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
import java.util.stream.Collectors;
@Service
public class UserService implements UserDetailsService {
    @Lazy
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<UserResponseDTO> getUserDetails(){
        return new ResponseEntity<>(UserResponseDTO.builder()
                .studentId(getCurrentUser().getStudentId())
                .role(getCurrentUser().getRole())
                .studentName(getCurrentUser().getStudentName())
                .phoneNumber(getCurrentUser().getPhoneNumber())
                .email(getCurrentUser().getEmail())
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
                        .build()
        ).collect(Collectors.toList());
    }

    public ResponseEntity<UserResponseDTO> updateUser(UserRequestDTO request){
        User existingUser = userRepository.findById(request.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found  " ));
        if (!isAdmin()&&!request.getId().equals(getCurrentUser().getId())){
            throw new RuntimeException("Only admin users can access this resource.");
        }
        existingUser.setStudentId(request.getStudentId());
        existingUser.setStudentName(request.getStudentName());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        existingUser.setEmail(request.getEmail());
        userRepository.save(existingUser);
        return new ResponseEntity<>(UserResponseDTO.builder()
                .studentId(existingUser.getStudentId())
                .id(existingUser.getId())
                .phoneNumber(existingUser.getPhoneNumber())
                .email(existingUser.getEmail())
                .role(existingUser.getRole())
                .studentName(existingUser.getStudentName())
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



}
