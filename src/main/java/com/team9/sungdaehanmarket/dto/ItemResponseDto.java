package com.team9.sungdaehanmarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseDto {
    private Long itemIdx;
    private String title;
    private String itemImage;
    private String description;
    private Long price;
    private Boolean isFavorite;
    private String category;
    private String authorMajor;
}