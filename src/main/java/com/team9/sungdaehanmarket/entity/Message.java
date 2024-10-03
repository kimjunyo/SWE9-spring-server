package com.team9.sungdaehanmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;  // 메시지 고유 ID

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;  // 채팅방

    private Long senderId;  // 보낸 사람 ID

    private Boolean isSellerMessage;  // 판매자 메시지 여부

    private String content;  // 메시지 내용

    private LocalDateTime sentAt = LocalDateTime.now();  // 메시지 전송 시간

    private Boolean isRead = false;  // 메시지 읽음 여부
}