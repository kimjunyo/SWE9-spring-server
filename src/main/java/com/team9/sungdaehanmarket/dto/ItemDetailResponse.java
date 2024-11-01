package com.team9.sungdaehanmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDetailResponse {
    private int status;
    private String message;
    private Content content;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Content {
        private List<String> photos;
        private String title;
        private String date;
        private int likes;
        private String description;
        private Long price;
        private Boolean isOfferAccepted;
        private Boolean isFavorite;
    }
}