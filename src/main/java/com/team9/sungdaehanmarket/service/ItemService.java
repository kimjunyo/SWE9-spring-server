package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;  // S3Service 주입

    public ItemService(ItemRepository itemRepository, UserRepository userRepository, S3Service s3Service) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;  // S3Service 주입
    }

    public List<ItemResponseDto> getItems(Long userId) {
        List<Item> items = itemRepository.findAll();

        // Item 엔티티를 ItemResponseDto로 변환
        return items.stream().map(item -> {
            ItemResponseDto dto = new ItemResponseDto();
            dto.setItemIdx(item.getIdx());
            dto.setTitle(item.getTitle());
            dto.setItemImage(item.getPhotos().isEmpty() ? null : item.getPhotos().get(0));
            dto.setDescription(item.getDescription());
            dto.setPrice(item.getPrice());
            dto.setIsFavorite(userRepository.findFavoriteItemsByIdx(userId).contains(item.getIdx()));
            return dto;
        }).collect(Collectors.toList());
    }

    public void registerItem(Long sellerId, String title, Long price, Boolean isOfferAccepted, List<MultipartFile> photos, String category, String description) throws IOException {
        // 사진을 S3에 업로드하고 URL 목록을 생성
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            String url = s3Service.uploadFile(photo);  // s3Service 인스턴스를 사용하여 호출
            photoUrls.add(url);
        }

        // 새로운 Item 생성
        Item newItem = new Item();
        newItem.setSellerId(sellerId);
        newItem.setTitle(title);
        newItem.setPrice(price);
        newItem.setIsOfferAccepted(isOfferAccepted);
        newItem.setPhotos(photoUrls);  // S3에 업로드한 사진 URL 저장
        newItem.setCategory(Item.Category.valueOf(category));
        newItem.setDescription(description);
        newItem.setUploadedAt(LocalDate.now());

        // Item 저장
        itemRepository.save(newItem);
    }
}