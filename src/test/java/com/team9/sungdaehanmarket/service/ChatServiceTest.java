package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ChatRoomDetailDto;
import com.team9.sungdaehanmarket.dto.ChatRoomsResponseDto;
import com.team9.sungdaehanmarket.entity.ChatRoom;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.entity.Message;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.ChatRoomRepository;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.MessageRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ChatServiceTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before All");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After All");
    }

    @Autowired
    private ChatService chatService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatroomRepository;

    @Autowired
    private MessageRepository messageRepository;

    //채팅방 가져오기 api 테스트 --------------------------------------------------------------------------------
    @Test
    @DisplayName(value = "메세지 기록 없는 채팅방 가져오기 성공 테스트")
    @Transactional
    public void getChatRoomSuccessTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        List<ChatRoomsResponseDto> chatRooms = chatService.getChatRooms(buyer.getIdx());
        //Then
        assertThat(chatRooms.get(0).getId()).isEqualTo(seller.getUsername());
    }

    @Test
    @DisplayName(value = "메세지 기록 있는 채팅방 가져오기 성공 테스트")
    @Transactional
    public void getChatRoomAndMessagesSuccessTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setIsSellerMessage(true);
        message.setSenderId(seller.getIdx());
        message.setContent("사랑해");

        messageRepository.save(message);
        //When
        List<ChatRoomsResponseDto> chatRooms = chatService.getChatRooms(buyer.getIdx());
        //Then
        assertThat(chatRooms.get(0).getLastMessage()).isEqualTo(message.getContent());
    }

    //채팅방 생성 테스트 --------------------------------------------------------------------------------

    @Test
    @DisplayName(value = "채팅방 생성 성공 테스트")
    public void createChatRoomSuccessTest() {
        //Given
        User buyer = new User();
        buyer.setIdx(998L);
        buyer.setEmail("test3@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("test3");

        User seller = new User();
        seller.setIdx(999L);
        seller.setEmail("test4@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("test4");

        Item item = new Item();
        item.setIdx(1000L);
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        //When
        Boolean chatRoom1 = chatService.createChatRoom(buyer.getIdx(), item.getIdx());
        //Then
        assertThat(chatRoom1).isFalse();
    }

    @Test
    @DisplayName(value = "채팅방 생성 아이템 idx 실패 테스트")
    public void createChatRoomItemFailureTest() {
        //Given
        User buyer = new User();
        buyer.setIdx(998L);
        buyer.setEmail("test3@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("test3");

        User seller = new User();
        seller.setIdx(999L);
        seller.setEmail("test4@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("test4");

        Item item = new Item();
        item.setIdx(1000L);
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        //When
        Boolean chatRoom1 = chatService.createChatRoom(buyer.getIdx(), 2000L);
        //Then
        assertThat(chatRoom1).isFalse();
    }

    @Test
    @DisplayName(value = "채팅방 생성 유저 idx 실패 테스트")
    public void createChatRoomUserFailureTest() {
        //Given
        User buyer = new User();
        buyer.setIdx(998L);
        buyer.setEmail("test3@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("test3");

        User seller = new User();
        seller.setIdx(999L);
        seller.setEmail("test4@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("test4");

        Item item = new Item();
        item.setIdx(1000L);
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(2000L);
        item.setTitle("test.title");
        item.setPrice(1000L);

        //When
        Boolean chatRoom1 = chatService.createChatRoom(buyer.getIdx(), item.getIdx());
        //Then
        assertThat(chatRoom1).isFalse();
    }

    //메세지 저장 api test --------------------------------------------------------------------------------
    @Test
    @DisplayName(value = "메세지 저장 성공 api test")
    @Transactional
    public void storeMessagesSuccessTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        Boolean b = chatService.storeMessageText(buyer.getIdx(), chatRoom.getIdx(), "사랑해");
        //Then
        assertThat(b).isTrue();
    }

    @Test
    @DisplayName(value = "메세지 저장 chatroom id 없어서 실패 api test")
    @Transactional
    public void storeMessageFailTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        Boolean b = chatService.storeMessageText(buyer.getIdx(), 200L, "사랑해");
        //Then
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName(value = "채팅방 삭제 성공 test")
    @Transactional
    public void deleteChatRoomSuccessTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        Boolean b = chatService.deleteChatRoom(chatRoom.getIdx());
        //Then
        assertThat(b).isTrue();
    }

    @Test
    @DisplayName(value = "채팅방 삭제 채팅방 존재하지 않아 실패 test")
    @Transactional
    public void deleteChatRoomFailTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        Boolean b = chatService.deleteChatRoom(200L);
        //Then
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName(value = "채팅방 정보 가져오기 성공 test")
    @Transactional
    public void getChatRoomDetailsSuccessTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");
        buyer.setRating(1.0f);

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);
        item.setPhotos(List.of("서적"));

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        ChatRoomDetailDto chatRoomDetail = chatService.getChatRoomDetail(chatRoom.getIdx(), buyer.getIdx());
        ChatRoomDetailDto chatRoomDetail2 = chatService.getChatRoomDetail(chatRoom.getIdx(), seller.getIdx());
        //Then
        assertThat(chatRoomDetail.getName()).isEqualTo("test2");
        assertThat(chatRoomDetail2.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName(value = "채팅방 정보 가져오기 실패 test")
    @Transactional
    public void getChatRoomDetailsFailTest() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");
        buyer.setRating(1.0f);

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");

        userRepository.save(buyer);
        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setSellerId(seller.getIdx());
        item.setTitle("test.title");
        item.setPrice(1000L);
        item.setPhotos(List.of("서적"));

        itemRepository.save(item);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser1Id(buyer.getIdx());
        chatRoom.setUser2Id(seller.getIdx());
        chatRoom.setItemId(item.getIdx());
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatroomRepository.save(chatRoom);
        //When
        ChatRoomDetailDto chatRoomDetail = chatService.getChatRoomDetail(200L, buyer.getIdx());
        //Then
        assertThat(chatRoomDetail).isNull();
    }


}
