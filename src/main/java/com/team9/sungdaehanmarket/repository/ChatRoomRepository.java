package com.team9.sungdaehanmarket.repository;

import com.team9.sungdaehanmarket.entity.ChatRoom;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @NotNull
    @Query("SELECT c FROM ChatRoom c WHERE c.user1Id = :user1Id and c.user2Id != :user2Id")
    List<ChatRoom> findAllByUser1IdAndUser2IdNotContaining(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @NotNull
    @Query("SELECT c FROM ChatRoom c WHERE c.user1Id != :user1Id and c.user2Id = :user2Id")
    List<ChatRoom> findAllByUser1IdNotContainingAndUser2Id(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
