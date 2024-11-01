package com.team9.sungdaehanmarket.repository;

import com.team9.sungdaehanmarket.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.favoriteItems f WHERE u.idx = :userId AND f = :itemId")
    boolean isItemFavoritedByUser(@Param("userId") Long userId, @Param("itemId") Long itemId);

    List<Item> findByCategory(Item.Category category);

    List<Item> findByIsSold(boolean isSold);

    List<Item> findBySellerId(Long sellerId);
}