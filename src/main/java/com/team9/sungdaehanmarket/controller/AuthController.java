package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import com.team9.sungdaehanmarket.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("id");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getIdx(), user.getUsername());

            Map<String, String> content = new HashMap<>();
            content.put("token", token);

            ApiResponse<Map<String, String>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Login successful",
                    content
            );

            return ResponseEntity.ok().body(response);
        } else {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid username or password",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}