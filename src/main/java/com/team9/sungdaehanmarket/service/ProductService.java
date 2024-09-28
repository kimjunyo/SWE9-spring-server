package com.team9.sungdaehanmarket.service;

import com.team9.sungdaehanmarket.entity.Product;
import com.team9.sungdaehanmarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public String findTitle(Long id) {
        Optional<Product> byId = productRepository.findById(id);
        return byId.map(Product::getName).orElse(null);
    }
}
