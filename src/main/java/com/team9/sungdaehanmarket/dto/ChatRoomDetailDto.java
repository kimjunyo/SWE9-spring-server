package com.team9.sungdaehanmarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ChatRoomDetailDto {
    private final String name;
    private final float rating;
    private final String itemImage;
    private final String title;
    private final int price;
    private final String profileImage;
}
