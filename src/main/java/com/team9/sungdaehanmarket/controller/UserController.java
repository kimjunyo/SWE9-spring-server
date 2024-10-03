package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setName(name);
        newUser.setMajor(major);
        newUser.setEmail(email);
        newUser.setIsSchoolVerified(isValid);

        userRepository.save(newUser);

        ApiResponse<String> successResponse = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }
}