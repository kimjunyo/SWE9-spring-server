package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.*;
import com.team9.sungdaehanmarket.security.JwtTokenProvider;
import com.team9.sungdaehanmarket.service.ChatService;
import com.team9.sungdaehanmarket.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;

    // 테스트 완료
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

    // 더미데이터 추가 완료
    @PostMapping
    public ResponseEntity<?> createChatRoom(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ChatRoomCreateRequestDto chatRoomCreateRequestDto) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

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

        if (chatService.createChatRoom(userId, chatRoomCreateRequestDto.username(), chatRoomCreateRequestDto.itemId())) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "A chatroom is saved successfully",
                    null
            );
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().eTag("An user is not found or unexpected error").build();
        }
    }

    // 더미데이터 추가 완료
    @PostMapping("/{chatroomid}/image")
    public ResponseEntity<?> sendAnImage(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("chatroomid") Long chatroomid, @RequestParam("image") MultipartFile image) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

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

        String profileImageUrl;
        try {
            profileImageUrl = s3Service.uploadFile(image);
        } catch (IOException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error uploading profile image",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        if (chatService.storeMessageImage(userId, chatroomid, profileImageUrl)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "An image message is saved successfully",
                    null
            );
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().eTag("A chatroom does not exist").build();
        }
    }

    //더미데이터 추가 완료
    @PostMapping("/{chatroomid}/text")
    public ResponseEntity<?> sendAText(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("chatroomid") Long chatroomid, @RequestBody String text) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

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

        if (chatService.storeMessageText(userId, chatroomid, text)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "An text message is saved successfully",
                    null
            );
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().eTag("A chatroom does not exist").build();
        }
    }

    //테스트 완료
    @DeleteMapping("/{chatroomid}")
    public ResponseEntity<?> deleteChatRoom(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("chatroomid") Long chatroomid) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (chatService.deleteChatRoom(chatroomid)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "A chat room is deleted successfully",
                    null
            );
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().eTag("A chatroom does not exist").build();
        }
    }

    //테스트 완료
    @GetMapping("/{chatroomid}/detail")
    public ResponseEntity<?> getChatRoomDetails(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("chatroomid") Long chatroomid) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

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

        ChatRoomDetailDto chatRoomDetail = chatService.getChatRoomDetail(chatroomid, userId);
        if (chatRoomDetail == null) {
            return ResponseEntity.notFound().eTag("A chatroom does not exist or an item does not exist").build();
        } else {
            ApiResponse<ChatRoomDetailDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Get chat room detail successfully",
                    chatRoomDetail
            );
            return ResponseEntity.ok().body(response);
        }
    }

    //테스트 완료
    @GetMapping("/{chatroomid}")
    public ResponseEntity<?> getMessages(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("chatroomid") Long chatroomid) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

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

        List<MessageResponseDto> messages = chatService.getMessages(chatroomid, userId);
        if (messages == null) {
            return ResponseEntity.notFound().eTag("A chatroom does not exist").build();
        } else {
            ApiResponse<List<MessageResponseDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Get chat room detail successfully",
                    messages
            );
            return ResponseEntity.ok().body(response);
        }
    }
}
