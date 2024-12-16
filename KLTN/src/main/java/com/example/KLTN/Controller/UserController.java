package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.UserRequestDTO;
import com.example.KLTN.DTOS.Response.UserResponseDTO;
import com.example.KLTN.Enum.Role;
import com.example.KLTN.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")

public class UserController {
    @Lazy
    private UserService userService;

    @GetMapping("/admin/getAll")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("admin/getUserDetails/{id}")
    public ResponseEntity<UserResponseDTO> getUserDetailsForAdmin(@PathVariable Long id){
        return userService.getUserDetailsForAdmin(id);
    }

    @GetMapping("getUserDetails")
    public ResponseEntity<UserResponseDTO> getUserDetails(){
        return userService.getUserDetails();
    }

    @GetMapping("/admin/byRole")
    public ResponseEntity<?> getUsersByRole(@RequestParam Role role){
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("updateUser")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserRequestDTO request){
        return userService.updateUser(request);
    }

    @PutMapping("/admin/updateRole/{id}")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable Long id,@RequestParam Role role){
        return userService.updateRole(id, role);
    }

    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }




}
