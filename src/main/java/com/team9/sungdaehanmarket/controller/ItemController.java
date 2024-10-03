package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.service.ItemService;
import com.team9.sungdaehanmarket.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final JwtTokenProvider jwtTokenProvider;

    public ItemController(ItemService itemService, JwtTokenProvider jwtTokenProvider) {
        this.itemService = itemService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<?> getItems(@RequestHeader("Authorization") String authorizationHeader) {
        // Bearer 접두사 제거
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token after stripping Bearer: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        List<ItemResponseDto> items = itemService.getItems(userId);

        // 리스트 응답
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Items retrieved successfully",
                items
        );

        return ResponseEntity.ok().body(response);
    }
}