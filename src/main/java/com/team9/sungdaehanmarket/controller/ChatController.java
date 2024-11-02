package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.dto.ChatRoomsResponseDto;
import com.team9.sungdaehanmarket.entity.ChatRoom;
import com.team9.sungdaehanmarket.security.JwtTokenProvider;
import com.team9.sungdaehanmarket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<?> getChatRoomList(@RequestHeader("Authorization") String authorizationHeader) {
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

        List<ChatRoomsResponseDto> chatRooms = chatService.getChatRooms(userId);

        ApiResponse<List<ChatRoomsResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "chatrooms are gotten succesfully",
                chatRooms
        );
        return ResponseEntity.ok().body(response);
    }

    // 필요할 거 같은데..
    @PostMapping
    private ResponseEntity<?> createChatRoom(ChatRoom chatRoom) {
        return null;
    }
}
