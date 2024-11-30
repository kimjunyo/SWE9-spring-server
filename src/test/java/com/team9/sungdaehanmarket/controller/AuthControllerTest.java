package com.team9.sungdaehanmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    @Transactional
    public void testLogin_Success() throws Exception {
        // Given
        User user = new User();
        user.setUsername("kimjunyo");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail("kimjunyo@email.com");
        user.setName("kimjunyo");
        userRepository.save(user);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("id", "kimjunyo");
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
    @Transactional
    public void testLogin_Failure_InvalidPassword() throws Exception {
        // Given
        User user = new User();
        user.setUsername("kimjunyo");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail("kimjunyo@email.com");
        user.setName("kimjunyo");
        userRepository.save(user);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("id", "kimjunyo");
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
    @Transactional
    public void testLogin_Failure_UserNotFound() throws Exception {
        // Given
        User user = new User();
        user.setUsername("kimjunyo");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail("kimjunyo@email.com");
        user.setName("kimjunyo");
        userRepository.save(user);

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

