package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // BCryptPasswordEncoder 추가
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
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 409);  // Conflict 상태 코드
            errorResponse.put("message", "Username already exists");

            return ResponseEntity.status(409).body(errorResponse);
        }

        // 이메일이 이미 존재하는지 확인 (email)
        if (userRepository.existsByEmail(email)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 409);  // Conflict 상태 코드
            errorResponse.put("message", "Email already exists");

            return ResponseEntity.status(409).body(errorResponse);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 새로운 유저 생성 및 저장
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);  // 암호화된 비밀번호 저장
        newUser.setName(name);
        newUser.setMajor(major);
        newUser.setEmail(email);
        newUser.setIsSchoolVerified(isValid);

        userRepository.save(newUser);

        // 성공 응답
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("status", 201);  // Created 상태 코드
        successResponse.put("message", "User registered successfully");

        return ResponseEntity.status(201).body(successResponse);
    }
}