package com.team9.sungdaehanmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User user = new User();

    @BeforeEach
    public void setUp() {
        // 테스트용 사용자 추가
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail("test@email.com");
        user.setName("testUser");
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        // 테스트 후 사용자 데이터 삭제
        userRepository.delete(user);
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("id", "testUser");
        loginRequest.put("password", "testPassword");

        // When
        // Then
        String body = mapper.writeValueAsString(loginRequest);

        mvc.perform(post("/api/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                //MockMvc를 통해 /hello 주소로 GET 요청
                //mvc.perform()의 결과를 검증
                .andExpect(status().isOk()); //200 상태
    }

    @Test
    public void testLogin_Failure_InvalidPassword() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("id", "testUser");
        loginRequest.put("password", "wrongPassword");

        // When
        String body = mapper.writeValueAsString(loginRequest);

        mvc.perform(post("/api/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                //MockMvc를 통해 /hello 주소로 GET 요청
                //mvc.perform()의 결과를 검증
                .andExpect(status().isUnauthorized()); //200 상태
    }

    @Test
    public void testLogin_Failure_UserNotFound() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("id", "nonExistentUser");
        loginRequest.put("password", "testPassword");

        // When
        String body = mapper.writeValueAsString(loginRequest);

        mvc.perform(post("/api/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                //MockMvc를 통해 /hello 주소로 GET 요청
                //mvc.perform()의 결과를 검증
                .andExpect(status().isUnauthorized()); //200 상태
    }
}

