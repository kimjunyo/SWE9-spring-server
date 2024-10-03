package com.team9.sungdaehanmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;  // 물품 고유 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;  // 카테고리 선택 (Enum)

    @ElementCollection
    @CollectionTable(name = "item_photos", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "photo_url")
    private List<String> photos;  // 사진 (최대 10장, S3 URL로 저장)

    @Column(nullable = false)
    private String title;  // 물품 제목

    @Column(nullable = false)
    private Long price;  // 가격

    private Boolean isOfferAccepted;  // 제안 받기 여부

    private LocalDate uploadedAt;  // 올린 날짜

    private int likes;  // 관심 숫자

    @Column(columnDefinition = "TEXT")
    private String description;  // 상세 설명

    private Long sellerId;  // 판매자 ID

    private Boolean isSold;  // 판매 완료 여부

    private Long buyerId;  // 구매자 ID

    private LocalDate transactionDate;  // 거래 날짜

    public enum Category {
        TEXTBOOK,
        EXERCISE_EQUIPMENT,
        ELECTRONICS,
        OTHER
    }
}