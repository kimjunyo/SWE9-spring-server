package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.dto.ItemResponseDto;
import com.team9.sungdaehanmarket.entity.Item;
import com.team9.sungdaehanmarket.repository.ItemRepository;
import com.team9.sungdaehanmarket.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
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
}