package com.example.KLTN.Entity;

import com.example.KLTN.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "account_user")
public class User extends AbstractEntity<Long> implements UserDetails {
    @Column
    private String studentId;
    @Column
    private String studentName;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String phoneNumber;
    @Column
    private LocalDateTime registeredDay;
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    @Column
    private LocalDateTime verificationCodeExpiresAt;

    @Column
    private String verificationCode;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về danh sách quyền (authority) của người dùng
        return List.of(() -> role.name());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return studentName; // Hoặc trường nào đại diện cho tên người dùng
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tùy thuộc vào yêu cầu của bạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tùy thuộc vào yêu cầu của bạn
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Tùy thuộc vào yêu cầu của bạn
    }

    @Override
    public boolean isEnabled() {
        return enabled; // Tùy thuộc vào yêu cầu của bạn
    }

}
