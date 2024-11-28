package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DisplayName(value = "아이템 목록 가져오기 test")
    public void addItem() {
        //Given
        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");
        buyer.setRating(1.0f);
        buyer.setMajor("수학과");

        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");
        seller.setMajor("보건학과");

        userRepository.save(buyer);
        userRepository.save(seller);

        //아이템 사진 X, 판매자가 없는 제품
        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setTitle("전공 서적");
        item.setPrice(10000L);
        item.setSellerId(2000L);
        item.setPhotos(List.of());

        //아이템 사진 O, 판매된 제품
        Item item3 = new Item();
        item3.setCategory(Item.Category.EXERCISE_EQUIPMENT);
        item3.setTitle("운동기구");
        item3.setPrice(20000L);
        item3.setPhotos(List.of("item3"));
        item3.setSellerId(seller.getIdx());
        item3.setBuyerId(buyer.getIdx());

        itemRepository.save(item);
        itemRepository.save(item3);

        Item byId = itemRepository.findById(item.getIdx()).get();
        //When
        List<ItemResponseDto> items = itemService.getItems(buyer.getIdx());
        //Then
        assertThat(items.get(items.size()-2).getItemImage()).isNull();
        assertThat(items.get(items.size()-2).getAuthorMajor()).isNull();
        assertThat(items.get(items.size()-1).getAuthorMajor()).isNotNull();
        assertThat(items.get(items.size()-1).getItemImage()).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName(value = "아이템 등록 test")
    public void registerItem() {

    }
}
