package com.team9.sungdaehanmarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class MessageResponseDto {
    private final Boolean isPhoto;
    private final Boolean isSender;
    private final String data;
}
