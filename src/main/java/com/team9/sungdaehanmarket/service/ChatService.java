package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ChatRoomDetailDto;
import com.team9.sungdaehanmarket.dto.ChatRoomsResponseDto;
import com.team9.sungdaehanmarket.dto.MessageDto;
import com.team9.sungdaehanmarket.dto.MessageResponseDto;
import com.team9.sungdaehanmarket.entity.ChatRoom;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.entity.Message;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.ChatRoomRepository;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.MessageRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ItemRepository itemRepository;

    public List<ChatRoomsResponseDto> getChatRooms(Long userId) {
        List<ChatRoomsResponseDto> chatRoomsList = new ArrayList<>();

        List<ChatRoom> user1IdList = chatRoomRepository.findAllByUser1IdAndUser2IdNotContaining(userId, userId);

        for (ChatRoom chatRoom : user1IdList) {
            Tuple userProfileById = userRepository.findUserProfileById(chatRoom.getUser2Id()).get();

            String profileImage = userProfileById.get("profileImage", String.class);
            String username = userProfileById.get("username", String.class);
            String major = userProfileById.get("major", String.class);

            Optional<MessageDto> latestMessage = messageRepository.findLatestMessageContentAndSentAtByChatRoomId(chatRoom.getIdx());
            if (latestMessage.isPresent()) {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(username)
                        .profileImage(profileImage)
                        .lastMessageTime(latestMessage.get().getSentAt().toString())
                        .lastMessage(latestMessage.get().getContent())
                        .build();
                chatRoomsList.add(dto);
            } else {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(username)
                        .profileImage(profileImage)
                        .build();
                chatRoomsList.add(dto);
            }
        }

        List<ChatRoom> user2IdList = chatRoomRepository.findAllByUser1IdNotContainingAndUser2Id(userId, userId);

        for (ChatRoom chatRoom : user2IdList) {
            Tuple userProfileById = userRepository.findUserProfileById(chatRoom.getUser1Id()).get();

            String profileImage = userProfileById.get("profileImage", String.class);
            String username = userProfileById.get("username", String.class);
            String major = userProfileById.get("major", String.class);

            Optional<MessageDto> latestMessage = messageRepository.findLatestMessageContentAndSentAtByChatRoomId(chatRoom.getIdx());
            if (latestMessage.isPresent()) {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(username)
                        .profileImage(profileImage)
                        .lastMessageTime(latestMessage.get().getSentAt().toString())
                        .lastMessage(latestMessage.get().getContent())
                        .build();
                chatRoomsList.add(dto);
            } else {
                ChatRoomsResponseDto dto = ChatRoomsResponseDto.builder()
                        .chatroomIdx(chatRoom.getIdx())
                        .major(major)
                        .id(username)
                        .profileImage(profileImage)
                        .build();
                chatRoomsList.add(dto);
            }
        }

        return chatRoomsList;
    }

    public Boolean createChatRoom(Long user1Id, String username, Long itemId) {
        Optional<User> byUsername = userRepository.findByUsername(username);

        if (byUsername.isPresent()) {
            try {
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setItemId(itemId);
                chatRoom.setUser1Id(user1Id);
                chatRoom.setUser2Id(byUsername.get().getIdx());
                chatRoomRepository.save(chatRoom);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }


    }

    public Boolean storeMessageImage(Long chatRoomId, Long userId, String imageUrl) {
        if (chatRoomRepository.findById(chatRoomId).isPresent()) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
            messageRepository.save(new Message(chatRoom, userId, false, imageUrl, LocalDateTime.now(), false));
            return true;
        } else {
            return false;
        }
    }

    public Boolean storeMessageText(Long chatRoomId, Long userId, String text) {
        if (chatRoomRepository.findById(chatRoomId).isPresent()) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
            messageRepository.save(new Message(chatRoom, userId, true, text, LocalDateTime.now(), false));
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteChatRoom(Long chatRoomId) {
        if (chatRoomRepository.findById(chatRoomId).isPresent()) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
            chatRoomRepository.delete(chatRoom);
            return true;
        } else {
            return false;
        }
    }

    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId, Long userId) {
        if (chatRoomRepository.findById(chatRoomId).isPresent()) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
            Long opponentId;
            if (chatRoom.getUser1Id().equals(userId)) {
                opponentId = chatRoom.getUser2Id();
            } else {
                opponentId = chatRoom.getUser1Id();
            }

            Tuple userProfileById = userRepository.findUserProfileById(opponentId).get();

            String profileImage = userProfileById.get("profileImage", String.class);
            String name = userProfileById.get("name", String.class);
            String rating = userProfileById.get("rating", String.class);

            Optional<Item> byId = itemRepository.findById(chatRoom.getItemId());

            return byId.map(item -> ChatRoomDetailDto.builder()
                    .title(item.getTitle())
                    .price(item.getPrice().intValue())
                    .rating(Float.parseFloat(rating))
                    .itemImage(item.getPhotos().get(0))
                    .profileImage(profileImage)
                    .name(name)
                    .build()).orElse(null);

        } else {
            return null;
        }
    }

    public List<MessageResponseDto> getMessages(Long chatRoomId, Long userId) {
        if (chatRoomRepository.findById(chatRoomId).isPresent()) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
            List<MessageResponseDto> messageList = new ArrayList<>();

            List<Message> allByChatRoom = messageRepository.findAllByChatRoom(chatRoom);
            for (Message message : allByChatRoom) {
                if (Objects.equals(message.getSenderId(), userId)) {
                    messageList.add(MessageResponseDto.builder()
                            .isPhoto(message.getIsSellerMessage())
                            .isSender(true)
                            .data(message.getContent())
                            .build());
                } else {
                    messageList.add(MessageResponseDto.builder()
                            .isPhoto(message.getIsSellerMessage())
                            .isSender(false)
                            .data(message.getContent())
                            .build());
                }
            }
            return messageList;

        } else {
            return null;
        }
    }
}
