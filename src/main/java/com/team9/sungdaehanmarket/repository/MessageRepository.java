package com.team9.sungdaehanmarket.repository;

import com.team9.sungdaehanmarket.dto.MessageDto;
import com.team9.sungdaehanmarket.entity.ChatRoom;
import com.team9.sungdaehanmarket.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT new com.team9.sungdaehanmarket.dto.MessageDto(m.content, m.sentAt) FROM Message m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.sentAt DESC")
    Optional<MessageDto> findLatestMessageContentAndSentAtByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    List<Message> findAllByChatRoom(ChatRoom chatRoom);

}
