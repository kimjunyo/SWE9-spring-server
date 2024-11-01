package com.team9.sungdaehanmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;  // 사용자 고유 ID

    @Column(nullable = false, unique = true)
    private String username;  // 아이디

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(nullable = false)
    private String name;  // 이름

    @Column(nullable = false, unique = true)
    private String email;  // 이메일

    private Boolean isSchoolVerified;  // 학교 인증 여부

    @ElementCollection
    private List<Long> purchaseHistory;  // 구매 내역 (Item ID 리스트)

    @ElementCollection
    private List<Long> saleHistory;  // 판매 내역 (Item ID 리스트)

    @ElementCollection
    private Set<Long> favoriteItems = new HashSet<>();  // 관심 물품 목록 (Item ID Set)

    @ElementCollection
    private List<Long> myItems;  // 내 상품 목록 (Item ID 리스트)

    private String profileImage;  // 프로필 이미지 (S3 URL)

    private String major;  // 학과

    private Float rating;  // 평점
}