package com.team9.sungdaehanmarket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MessageDto {

    // Getters
    private final String content;
    private final LocalDateTime sentAt;
}
