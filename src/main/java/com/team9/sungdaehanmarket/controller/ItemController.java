package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.dto.LikeItemRequest;
import com.team9.sungdaehanmarket.service.ItemService;
import com.team9.sungdaehanmarket.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping
    public ResponseEntity<?> registerItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("title") String title,
            @RequestParam("price") Long price,
            @RequestParam("isOfferAccepted") Boolean isOfferAccepted,
            @RequestParam("photos") List<MultipartFile> photos,
            @RequestParam("category") String category,
            @RequestParam("description") String description
    ) {
        // Bearer 접두사 제거
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        // 토큰에서 유저 정보 추출
        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long sellerId = Long.parseLong(authentication.getName());

        try {
            itemService.registerItem(sellerId, title, price, isOfferAccepted, photos, category, description);
            ApiResponse<Void> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Item registered successfully",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IOException e) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error uploading photos",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/likes")
    public ResponseEntity<?> likeItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody LikeItemRequest request) {

        // Bearer 접두사 제거
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        // JWT 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 사용자 ID 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        // 좋아요 상태 변경 시도
        boolean isLiked = itemService.likeItem(userId, request.getItemIdx());

        // 성공 여부에 따른 응답 반환
        if (isLiked) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Item liked/unliked successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to like/unlike item", null));
        }
    }
}