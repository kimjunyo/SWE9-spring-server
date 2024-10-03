package com.team9.sungdaehanmarket.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 서명용 SecretKey를 Base64로 인코딩
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("adjfl-j2034-kldj-fls02-4123-14jdkl-fjag09342".getBytes());

    private final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30 * 6;  // 6개월 (밀리초 단위)

    // JWT 토큰 생성
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(Long.toString(userId))  // 유저 ID를 subject로
                .claim("username", username)  // 유저 이름을 claim으로 추가
                .setIssuedAt(new Date())  // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 유효기간 설정
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)  // 서명 알고리즘과 비밀 키 설정
                .compact();
    }

    // 토큰에서 유저 ID 추출
    public Long extractUserId(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // 토큰 유효성 검증
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            System.out.println("유효하지 않은 토큰입니다.");
        }
        return false;
    }
}