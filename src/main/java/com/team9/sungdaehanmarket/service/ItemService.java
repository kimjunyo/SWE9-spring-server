package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ItemDetailResponse;
import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.entity.User;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public boolean likeItem(Long userId, Long itemIdx) {
        User user = userRepository.findById(userId).orElse(null);
        Item item = itemRepository.findById(itemIdx).orElse(null);

        if (user != null && item != null) {
            if (user.getFavoriteItems().contains(itemIdx)) {
                // 좋아요 취소
                user.getFavoriteItems().remove(itemIdx);
                item.setLikes(item.getLikes() - 1);
                System.out.println("좋아요 취소 - itemIdx: " + itemIdx);
            } else {
                // 좋아요 추가
                user.getFavoriteItems().add(itemIdx);
                item.setLikes(item.getLikes() + 1);
                System.out.println("좋아요 추가 - itemIdx: " + itemIdx);
            }

            // 변경사항을 강제로 DB에 반영
            userRepository.saveAndFlush(user); // 변경사항 즉시 반영
            itemRepository.saveAndFlush(item); // 변경사항 즉시 반영

            return true;
        }
        System.out.println("User 또는 Item을 찾을 수 없음 - userId: " + userId + ", itemIdx: " + itemIdx);
        return false;
    }

    public ItemDetailResponse getItemDetail(Long itemIdx, Long userId) {
        Optional<Item> itemOpt = itemRepository.findById(itemIdx);

        if (itemOpt.isEmpty()) {
            // 아이템이 없을 경우, 상태 코드와 메시지를 포함한 응답을 반환
            return new ItemDetailResponse(HttpStatus.NOT_FOUND.value(), "Item not found", null);
        }

        Item item = itemOpt.get();

        // 유저가 해당 아이템을 좋아요했는지 여부 확인 (ItemRepository 메서드 사용)
        boolean isFavorite = itemRepository.isItemFavoritedByUser(userId, item.getIdx());

        // Item 정보를 기반으로 ItemDetailResponse.Content 생성
        ItemDetailResponse.Content content = new ItemDetailResponse.Content(
                item.getPhotos(),
                item.getTitle(),
                item.getUploadedAt().format(DateTimeFormatter.ISO_DATE),
                item.getLikes(),
                item.getDescription(),
                item.getPrice(),
                item.getIsOfferAccepted(),
                isFavorite
        );

        // ItemDetailResponse 생성 후 반환
        return new ItemDetailResponse(HttpStatus.OK.value(), "Item detail retrieved successfully", content);
    }
}