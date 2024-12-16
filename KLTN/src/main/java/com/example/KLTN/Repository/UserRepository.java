package com.example.KLTN.Repository;


import com.example.KLTN.Entity.User;
import com.example.KLTN.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentId(String studentId);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}

