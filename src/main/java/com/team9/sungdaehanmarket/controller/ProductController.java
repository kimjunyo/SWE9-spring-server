package com.team9.sungdaehanmarket.controller;

import com.team9.sungdaehanmarket.api.ResponseAPI;
import com.team9.sungdaehanmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/product")
@Log
public class ProductController {
    private final ProductService productService;

    @GetMapping("/title/{id}")
    public ResponseAPI showTitle(@PathVariable Long id) {
        String title = productService.findTitle(id);
        if (title == null) {
            return new ResponseAPI(404, "there is no product with id " + id);
        }
        return new ResponseAPI(200, title, "success");
    }

    // Controller Test 2
}
