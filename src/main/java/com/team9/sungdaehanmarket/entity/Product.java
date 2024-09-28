package com.team9.sungdaehanmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @JoinColumn(nullable = false, name = "owner_num")
    @OneToOne
    private User owner;

    @JoinColumn(nullable = false, name = "buyer_num")
    @OneToOne
    private User buyer;
}
