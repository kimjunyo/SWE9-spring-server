package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.dto.*;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import com.team9.sungdaehanmarket.service.ItemService;
import com.team9.sungdaehanmarket.security.JwtTokenProvider;
import com.team9.sungdaehanmarket.service.S3Service;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private S3Service s3Service;

    private final JwtTokenProvider jwtTokenProvider;

    public MyPageController(ItemService itemService, JwtTokenProvider jwtTokenProvider) {
        this.itemService = itemService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/likes")
    public ResponseEntity<?> getLikes(@RequestHeader("Authorization") String authorizationHeader) {
        // Bearer 접두사 제거
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token after stripping Bearer: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        List<ItemResponseDto> likedItems = itemService.getLikedItems(userId);

        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Liked items retrieved successfully",
                likedItems
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/selling")
    public ResponseEntity<?> getSellingItems(@RequestHeader("Authorization") String authorizationHeader) {
        // Bearer 접두사 제거
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token after stripping Bearer: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        // 사용자의 판매 중인 아이템 목록 조회
        List<ItemResponseDto> sellingItems = itemService.getSellingItems(userId);

        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Selling items retrieved successfully",
                sellingItems
        );

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/selling/{itemIdx}")
    public ResponseEntity<?> updateSellingItem(
            @PathVariable("itemIdx") Long itemIdx,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("title") String title,
            @RequestParam("price") Long price,
            @RequestParam("isOfferAccepted") Boolean isOfferAccepted,
            @RequestParam("photos") List<MultipartFile> photos,
            @RequestParam("category") String category,
            @RequestParam("description") String description) {

        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token after stripping Bearer: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        Optional<Item> itemOpt = itemRepository.findByIdxAndSellerId(itemIdx, userId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Item not found or not owned by user", null));
        }

        Item item = itemOpt.get();

        if (title != null) item.setTitle(title);
        if (price != null) item.setPrice(price);
        if (isOfferAccepted != null) item.setIsOfferAccepted(isOfferAccepted);
        if (category != null) item.setCategory(Item.Category.valueOf(category));
        if (description != null) item.setDescription(description);

        if (photos != null && !photos.isEmpty()) {
            List<String> photoUrls = new ArrayList<>();
            try {
                for (MultipartFile photo : photos) {
                    String url = s3Service.uploadFile(photo);
                    photoUrls.add(url);
                }
                item.setPhotos(photoUrls);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error uploading photos", null));
            }
        }

        itemRepository.save(item);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Item updated successfully", null));
    }

    @DeleteMapping("/selling/{itemIdx}")
    public ResponseEntity<?> deleteSellingItem(
            @PathVariable("itemIdx") Long itemIdx,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token after stripping Bearer: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        Optional<Item> itemOpt = itemRepository.findByIdxAndSellerId(itemIdx, userId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Item not found or not owned by user", null));
        }

        itemRepository.delete(itemOpt.get());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Item deleted successfully", null));
    }

    @GetMapping("/buying")
    public ResponseEntity<?> getBuyingItems(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        if (!jwtTokenProvider.validateToken(token)) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Long userId = Long.parseLong(authentication.getName());

        List<ItemResponseDto> buyingItems = itemService.getBuyingItems(userId);

        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Buying items retrieved successfully",
                buyingItems
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating")
    public ResponseEntity<?> getItemRating(
            @RequestParam("itemIdx") Long itemIdx,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token", null));
        }

        Long userId = Long.parseLong(jwtTokenProvider.getAuthentication(token).getName());

        try {
            PurchasedItemDetailDto content = itemService.getItemRatingInfo(userId, itemIdx);

            ApiResponse<PurchasedItemDetailDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Item rating information retrieved successfully.",
                    content
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/rating")
    public ResponseEntity<?> rateUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> requestBody) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token", null));
        }

        Long buyerId = Long.parseLong(jwtTokenProvider.getAuthentication(token).getName());

        Long itemIdx = ((Number) requestBody.get("itemIdx")).longValue();
        Float rating = ((Number) requestBody.get("rating")).floatValue();

        try {
            itemService.saveRating(itemIdx, buyerId, rating);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Rating saved successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }
}

