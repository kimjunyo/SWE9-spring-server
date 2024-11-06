package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ItemDetailResponse;
import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.dto.ApiResponse;
import com.team9.sungdaehanmarket.dto.PurchasedItemDetailDto;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

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

        return items.stream().map(item -> {
            ItemResponseDto dto = new ItemResponseDto();
            dto.setItemIdx(item.getIdx());
            dto.setTitle(item.getTitle());
            dto.setItemImage(item.getPhotos().isEmpty() ? null : item.getPhotos().get(0));
            dto.setDescription(item.getDescription());
            dto.setPrice(item.getPrice());
            dto.setIsFavorite(userRepository.findFavoriteItemsByIdx(userId).contains(item.getIdx()));

            // Enum과 작성자의 학과 정보 추가
            dto.setCategory(item.getCategory().name());
            User seller = userRepository.findById(item.getSellerId()).orElse(null);
            if (seller != null) {
                dto.setAuthorMajor(seller.getMajor());
            }

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
        newItem.setIsSold(false);

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
            return new ItemDetailResponse(HttpStatus.NOT_FOUND.value(), "Item not found", null);
        }

        Item item = itemOpt.get();
        boolean isFavorite = userRepository.findFavoriteItemsByIdx(userId).contains(item.getIdx());

        User seller = userRepository.findById(item.getSellerId()).orElse(null);
        String sellerName = (seller != null) ? seller.getName() : "Unknown";

        ItemDetailResponse.Content content = new ItemDetailResponse.Content(
                item.getPhotos(),
                item.getTitle(),
                item.getUploadedAt().format(DateTimeFormatter.ISO_DATE),
                item.getLikes(),
                item.getDescription(),
                item.getPrice(),
                item.getIsOfferAccepted(),
                isFavorite,
                sellerName  // 판매자 이름 추가
        );

        return new ItemDetailResponse(HttpStatus.OK.value(), "Item detail retrieved successfully", content);
    }

    public List<ItemResponseDto> getLikedItems(Long userId) {
        List<Long> likedItemIds = userRepository.findFavoriteItemsByIdx(userId);

        return itemRepository.findAllById(likedItemIds).stream().map(item -> {
            ItemResponseDto dto = new ItemResponseDto();
            dto.setItemIdx(item.getIdx());
            dto.setTitle(item.getTitle());
            dto.setItemImage(item.getPhotos().isEmpty() ? null : item.getPhotos().get(0));
            dto.setDescription(item.getDescription());
            dto.setPrice(item.getPrice());
            dto.setIsFavorite(true);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ItemResponseDto> getSellingItems(Long sellerId){
        List<Item> unsoldItems = itemRepository.findBySellerIdAndIsSold(sellerId, false);

        return unsoldItems.stream().map(item -> {
            ItemResponseDto dto = new ItemResponseDto();
            dto.setItemIdx(item.getIdx());
            dto.setTitle(item.getTitle());
            dto.setItemImage(item.getPhotos().isEmpty() ? null : item.getPhotos().get(0));
            dto.setDescription(item.getDescription());
            dto.setPrice(item.getPrice());
            dto.setIsFavorite(userRepository.findFavoriteItemsByIdx(sellerId).contains(item.getIdx()));
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ItemResponseDto> getBuyingItems(Long userId){
        List<Item> purchasedItems = itemRepository.findByBuyerIdAndIsSold(userId, true);

        return purchasedItems.stream().map(item -> {
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

    public PurchasedItemDetailDto getItemRatingInfo(Long userId, Long itemIdx) {
        Item item = itemRepository.findByBuyerIdAndIsSold(userId, true).stream()
                .filter(i -> i.getIdx().equals(itemIdx))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found or not purchased by user"));

        User seller = userRepository.findById(item.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        PurchasedItemDetailDto dto = new PurchasedItemDetailDto();
        dto.setItemIdx(item.getIdx());
        dto.setTitle(item.getTitle());
        dto.setItemImage(item.getPhotos().isEmpty() ? null : item.getPhotos().get(0));
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setTransactionDate(item.getTransactionDate() != null ? item.getTransactionDate().toString() : null);
        dto.setSellerName(seller.getName());
        dto.setSellerMajor(seller.getMajor());

        return dto;
    }

    public void saveRating(Long itemIdx, Long userId, float rating) {
        // 아이템이 거래 완료 상태인지 확인하고 판매자 ID 가져오기
        Item item = itemRepository.findById(itemIdx)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("Only the buyer can rate the seller.");
        }

        User seller = userRepository.findById(item.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        seller.setRatingSum(seller.getRatingSum() + rating);
        seller.setRatingCount(seller.getRatingCount() + 1);
        seller.setRating(seller.getRatingSum() / seller.getRatingCount());

        userRepository.save(seller);
    }
}