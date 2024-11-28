package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ItemDetailResponse;
import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
        assertThat(items.get(items.size() - 2).getItemImage()).isNull();
        assertThat(items.get(items.size() - 2).getAuthorMajor()).isNull();
        assertThat(items.get(items.size() - 1).getAuthorMajor()).isNotNull();
        assertThat(items.get(items.size() - 1).getItemImage()).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName(value = "아이템 등록 test")
    public void registerItem() throws IOException {
        //Given
        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");
        seller.setMajor("보건학과");

        userRepository.save(seller);

        MockMultipartFile file = new MockMultipartFile(
                "홍길동전 썸네일 이미지",
                "thumbnail.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );
        //When
        //Then
        assertThatCode(() -> itemService.registerItem(seller.getIdx(), "생물 서적", 20000L, true, List.of(file), Item.Category.TEXTBOOK.toString(), "안녕 클레오파트라")).
                doesNotThrowAnyException();
    }

    @Test
    @Transactional
    @DisplayName(value = "아이템 좋아요 test")
    public void likeItem(){
        //Given
        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");
        seller.setMajor("보건학과");

        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setTitle("전공 서적");
        item.setPrice(10000L);
        item.setSellerId(2000L);
        item.setPhotos(List.of());
        item.setLikes(3);

        itemRepository.save(item);

        Set<Long> favoriteItems = new HashSet<>();
        Set<Long> favoriteItems2 = new HashSet<>();
        favoriteItems.add(item.getIdx());

        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");
        buyer.setRating(1.0f);
        buyer.setMajor("수학과");
        buyer.setFavoriteItems(favoriteItems);

        User buyer2= new User();
        buyer2.setEmail("kimjunyo2@gmail.com");
        buyer2.setPassword("test");
        buyer2.setName("test");
        buyer2.setUsername("kimjunyo2");
        buyer2.setRating(1.0f);
        buyer2.setMajor("수학과");
        buyer2.setFavoriteItems(favoriteItems2);

        userRepository.save(buyer);
        userRepository.save(buyer2);
        //When
        boolean b = itemService.likeItem(buyer.getIdx(), item.getIdx());
        boolean c = itemService.likeItem(buyer2.getIdx(), item.getIdx());
        //Then
        assertThat(b).isTrue();
        assertThat(c).isTrue();
        assertThat(item.getLikes()).isEqualTo(3);
        assertThat(itemService.likeItem(2000L, item.getIdx())).isEqualTo(false);
    }

    @Test
    @Transactional
    @DisplayName(value = "아이템 상세정보 test")
    public void getItemDetail(){
        //Given
        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");
        seller.setMajor("보건학과");

        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setTitle("전공 서적");
        item.setPrice(10000L);
        item.setSellerId(seller.getIdx());
        item.setPhotos(List.of());
        item.setLikes(3);
        item.setUploadedAt(LocalDate.now());

        Item item2 = new Item();
        item2.setCategory(Item.Category.TEXTBOOK);
        item2.setTitle("전공 서적");
        item2.setPrice(10000L);
        item2.setSellerId(2000L);
        item2.setPhotos(List.of());
        item2.setLikes(3);
        item2.setUploadedAt(LocalDate.now());

        itemRepository.save(item);
        itemRepository.save(item2);

        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");
        buyer.setRating(1.0f);
        buyer.setMajor("수학과");

        userRepository.save(buyer);
        //When
        ItemDetailResponse itemDetail = itemService.getItemDetail(item.getIdx(), buyer.getIdx());
        ItemDetailResponse itemDetail2 = itemService.getItemDetail(item2.getIdx(), buyer.getIdx());
        ItemDetailResponse itemDetail3 = itemService.getItemDetail(2000L, buyer.getIdx());
        //Then
        assertThat(itemDetail.getContent().getSellerName()).isEqualTo(seller.getName());
        assertThat(itemDetail2.getContent().getSellerName()).isEqualTo("Unknown");
        assertThat(itemDetail3.getContent()).isNull();
    }

    @Test
    @Transactional
    @DisplayName(value = "좋아요 누른 아이템 가져오기 test")
    public void getLikeItems(){
        //Given
        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");
        seller.setMajor("보건학과");

        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setTitle("전공 서적");
        item.setPrice(10000L);
        item.setSellerId(seller.getIdx());
        item.setPhotos(List.of());
        item.setLikes(3);
        item.setUploadedAt(LocalDate.now());

        itemRepository.save(item);

        Set<Long> favoriteItems = new HashSet<>();
        favoriteItems.add(item.getIdx());

        User buyer = new User();
        buyer.setEmail("kimjunyo@gmail.com");
        buyer.setPassword("test");
        buyer.setName("test");
        buyer.setUsername("kimjunyo");
        buyer.setRating(1.0f);
        buyer.setMajor("수학과");
        buyer.setFavoriteItems(favoriteItems);

        userRepository.save(buyer);
        //When
        List<ItemResponseDto> likedItems = itemService.getLikedItems(buyer.getIdx());
        //Then
        assertThat(likedItems.get(0)).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName(value = "판매 중인 아이템 가져오기 test")
    public void getSellingItems(){
        //Given
        User seller = new User();
        seller.setEmail("bomin@gmail.com");
        seller.setPassword("test2");
        seller.setName("test2");
        seller.setUsername("bomin");
        seller.setRating(1.0f);
        seller.setProfileImage("예쁜사진");
        seller.setMajor("보건학과");

        userRepository.save(seller);

        Item item = new Item();
        item.setCategory(Item.Category.TEXTBOOK);
        item.setTitle("전공 서적");
        item.setPrice(10000L);
        item.setSellerId(seller.getIdx());
        item.setPhotos(List.of());
        item.setLikes(3);
        item.setUploadedAt(LocalDate.now());
        item.setIsSold(false);

        itemRepository.save(item);
        //When
        List<ItemResponseDto> sellingItems = itemService.getSellingItems(seller.getIdx());
        //Then
        assertThat(sellingItems.get(0)).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName(value = "구매한 아이템 가져오기 test")
    public void getBuyingItems(){
        //Given
        //When
        //Then
    }

    @Test
    @Transactional
    @DisplayName(value = "아이템 구매후기 가져오기 test")
    public void getItemRatingInfo(){
        //Given
        //When
        //Then
    }

    @Test
    @Transactional
    @DisplayName(value = "아이템 구매후기 저장 test")
    public void saveRating(){
        //Given
        //When
        //Then
    }
}
