package com.team9.sungdaehanmarket.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import com.team9.sungdaehanmarket.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void testRegister_Success() throws Exception {
        // 요청 데이터
        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("id", "testUser");
        userRequest.put("password", "testPassword");
        userRequest.put("name", "Test Name");
        userRequest.put("major", "Computer Science");
        userRequest.put("email", "test@example.com");
        userRequest.put("isValid", true);

        // JSON 요청 바디 생성
        String jsonRequest = new ObjectMapper().writeValueAsString(userRequest);

        // 테스트 실행
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testRegister_UsernameAlreadyExists() throws Exception {
        // 요청 데이터
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail("existing@example.com");
        user.setName("kimjunyo");
        userRepository.save(user);

        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("id", "existingUser");
        userRequest.put("password", "testPassword");
        userRequest.put("name", "Test Name");
        userRequest.put("major", "Computer Science");
        userRequest.put("email", "test@example.com");
        userRequest.put("isValid", true);

        // JSON 요청 바디 생성
        String jsonRequest = new ObjectMapper().writeValueAsString(userRequest);

        // 테스트 실행
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    public void testRegister_EmailAlreadyExists() throws Exception {
        // 요청 데이터
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail("existing@example.com");
        user.setName("kimjunyo");
        userRepository.save(user);

        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("id", "testUser");
        userRequest.put("password", "testPassword");
        userRequest.put("name", "Test Name");
        userRequest.put("major", "Computer Science");
        userRequest.put("email", "existing@example.com");
        userRequest.put("isValid", true);

        // JSON 요청 바디 생성
        String jsonRequest = new ObjectMapper().writeValueAsString(userRequest);

        // 테스트 실행
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetMyPage_Success() throws Exception {
        // Mock 데이터 설정
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwidXNlcm5hbWUiOiJqdW5uODExIiwiaWF0IjoxNzMyOTc2NzUwLCJleHAiOjE3NDg1Mjg3NTB9.QFBQRYzrUfxNy6pQEqV2lG1hGknYMSalBIwA3BeA9CU";
        // 요청 실행
        mockMvc.perform(get("/api/mypage")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMyPage_InvalidToken() throws Exception {
        // Mock 데이터 설정
        String token = "invalidToken";
        // 요청 실행
        mockMvc.perform(get("/api/mypage")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetMyPage_UserNotFound() throws Exception {
        // Mock 데이터 설정
        String token = "validToken";
        // 요청 실행
        mockMvc.perform(get("/api/mypage")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    public void testUpdateProfile_Success() throws Exception {
        // Mock 데이터 설정
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwidXNlcm5hbWUiOiJqdW5uODExIiwiaWF0IjoxNzMyOTc2NzUwLCJleHAiOjE3NDg1Mjg3NTB9.QFBQRYzrUfxNy6pQEqV2lG1hGknYMSalBIwA3BeA9CU";

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage", "profile.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );

        // 요청 실행
        mockMvc.perform(multipart("/api/profile")
                        .file(profileImage)
                        .param("name", "New Name")
                        .param("major", "New Major")
                        .header("Authorization", "Bearer " + token)
                        .with(request -> {
                            request.setMethod("PATCH"); // PATCH 메서드로 설정
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testUpdateProfile_InvalidToken() throws Exception {
        // Mock 데이터 설정
        String token = "invalidToken";

        // 요청 실행
        mockMvc.perform(multipart("/api/profile")
                        .param("name", "New Name")
                        .header("Authorization", "Bearer " + token)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().is4xxClientError());
    }
}

