package com.team9.sungdaehanmarket.dto;

import lombok.*;

@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomsResponseDto {
    private Long chatroomIdx;
    private String profileImage;
    private String major;
    private String id;
    private String lastMessageTime;
    private String lastMessage;
}