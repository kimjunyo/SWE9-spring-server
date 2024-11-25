package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    @DisplayName(value = "채팅방 생성 성공 테스트")
    public void chatRoomCreateSuccessTest() {
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
    public void chatRoomCreateItemFailureTest() {
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
    public void chatRoomCreateUserFailureTest() {
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
}
