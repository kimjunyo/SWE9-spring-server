package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import com.team9.sungdaehanmarket.security.JwtTokenProvider;
import com.team9.sungdaehanmarket.service.S3Service;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private S3Service s3Service;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> userRequest) {
        // 요청 데이터 추출
        String username = (String) userRequest.get("id");
        String password = (String) userRequest.get("password");
        String name = (String) userRequest.get("name");
        String major = (String) userRequest.get("major");
        String email = (String) userRequest.get("email");
        Boolean isValid = (Boolean) userRequest.get("isValid");

        // 유저가 이미 존재하는지 확인 (username)
        if (userRepository.findByUsername(username).isPresent()) {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    HttpStatus.CONFLICT.value(),
                    "Username already exists",
                    null
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // 이메일이 이미 존재하는지 확인 (email)
        if (userRepository.existsByEmail(email)) {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    HttpStatus.CONFLICT.value(),
                    "Email already exists",
                    null
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 새로운 유저 생성 및 저장
        User newUser = new User(username, encodedPassword, name, email, isValid, major);

        userRepository.save(newUser);

        ApiResponse<String> successResponse = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token", null));
        }

        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(token));
        Optional<Tuple> userProfile = userRepository.findUserProfileById(userId);

        if (userProfile.isPresent()) {
            Tuple profileData = userProfile.get();

            String profileImage = profileData.get("profileImage", String.class);
            String name = profileData.get("name", String.class);
            String major = profileData.get("major", String.class);
            Float rating = profileData.get("rating", Float.class);

            Map<String, Object> profile = Map.of(
                    "profileImage", profileImage,
                    "name", name,
                    "major", major,
                    "rating", rating
            );

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "User profile retrieved successfully",
                    profile
            );

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }
    }

    // PATCH /profile - 사용자 프로필 수정
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "major", required = false) String major) {

        // Bearer 접두사 제거
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        // JWT 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // JWT 토큰에서 사용자 ID 추출
        Long userId = Long.parseLong(jwtTokenProvider.getAuthentication(token).getName());
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 프로필 이미지 업데이트 (profileImage 파라미터가 존재하는 경우에만 처리)
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    String profileImageUrl = s3Service.uploadFile(profileImage);
                    user.setProfileImage(profileImageUrl);
                } catch (IOException e) {
                    ApiResponse<String> errorResponse = new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error uploading profile image",
                            null
                    );
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                }
            }

            // 이름 업데이트 (name 파라미터가 존재하는 경우에만 처리)
            if (name != null) {
                user.setName(name);
            }

            // 전공 업데이트 (major 파라미터가 존재하는 경우에만 처리)
            if (major != null) {
                user.setMajor(major);
            }

            // 변경 사항 저장
            userRepository.save(user);

            ApiResponse<Void> successResponse = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Profile updated successfully",
                    null
            );

            return ResponseEntity.ok(successResponse);
        } else {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "User not found",
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}