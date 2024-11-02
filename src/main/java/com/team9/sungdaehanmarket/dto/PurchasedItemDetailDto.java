package com.team9.sungdaehanmarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasedItemDetailDto {
    private Long itemIdx;
    private String title;
    private String itemImage;
    private String description;
    private Long price;
    private String transactionDate;
    private String sellerName;
    private String sellerMajor;
}