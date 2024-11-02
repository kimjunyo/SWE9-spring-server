package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ChatRoomsResponseDto;
import com.team9.sungdaehanmarket.dto.MessageDto;
import com.team9.sungdaehanmarket.entity.ChatRoom;
import com.team9.sungdaehanmarket.repository.ChatRoomRepository;
import com.team9.sungdaehanmarket.repository.MessageRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public List<ChatRoomsResponseDto> getChatRooms(Long userId) {
        List<ChatRoomsResponseDto> chatRoomsList = new ArrayList<>();

        List<ChatRoom> user1IdList = chatRoomRepository.findAllByUser1IdAndUser2IdNotContaining(userId, userId);

        for (ChatRoom chatRoom : user1IdList) {
            Tuple userProfileById = userRepository.findUserProfileById(chatRoom.getUser2Id()).get();

            String profileImage = userProfileById.get("profileImage", String.class);
            String name = userProfileById.get("name", String.class);
            String major = userProfileById.get("major", String.class);

            Optional<MessageDto> latestMessage = messageRepository.findLatestMessageContentAndSentAtByChatRoomId(chatRoom.getIdx());
            if (latestMessage.isPresent()) {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(name)
                        .profileImage(profileImage)
                        .lastMessageTime(latestMessage.get().getSentAt().toString())
                        .lastMessage(latestMessage.get().getContent())
                        .build();
                chatRoomsList.add(dto);
            } else {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(name)
                        .profileImage(profileImage)
                        .build();
                chatRoomsList.add(dto);
            }
        }

        List<ChatRoom> user2IdList = chatRoomRepository.findAllByUser1IdNotContainingAndUser2Id(userId, userId);

        for (ChatRoom chatRoom : user2IdList) {
            Tuple userProfileById = userRepository.findUserProfileById(chatRoom.getUser1Id()).get();

            String profileImage = userProfileById.get("profileImage", String.class);
            String name = userProfileById.get("name", String.class);
            String major = userProfileById.get("major", String.class);

            Optional<MessageDto> latestMessage = messageRepository.findLatestMessageContentAndSentAtByChatRoomId(chatRoom.getIdx());
            if (latestMessage.isPresent()) {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(name)
                        .profileImage(profileImage)
                        .lastMessageTime(latestMessage.get().getSentAt().toString())
                        .lastMessage(latestMessage.get().getContent())
                        .build();
                chatRoomsList.add(dto);
            } else {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(name)
                        .profileImage(profileImage)
                        .build();
                chatRoomsList.add(dto);
            }
        }

        return chatRoomsList;
    }
}
