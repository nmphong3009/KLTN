package com.example.KLTN.Component;

import com.example.KLTN.Entity.User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("your_secret_key_your_secret_key_your_secret_key".getBytes());
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)  // Sử dụng secret key đã mã hóa
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("studentId", user.getStudentId());
        claims.put("role", user.getRole()); // Nếu có role
        claims.put("name", user.getStudentName());
        return createToken(claims, user.getStudentId()); // Sử dụng idCard hoặc tên người dùng làm subject
    }
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Token hết hạn sau 10 giờ
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)  // Sử dụng secret key đã mã hóa
                .compact();
    }
    public Boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getStudentId()) && !isTokenExpired(token) ); // Kiểm tra idCard
    }
}
