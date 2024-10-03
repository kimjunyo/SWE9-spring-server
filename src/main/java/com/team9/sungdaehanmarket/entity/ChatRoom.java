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
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;  // 채팅방 고유 ID

    @Column(name = "item_id", nullable = false)
    private Long itemId;  // 물품 ID

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;  // 첫 번째 사용자 ID

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;  // 두 번째 사용자 ID

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 채팅방 생성 시간
}