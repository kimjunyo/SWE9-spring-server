package com.team9.sungdaehanmarket.repository;

import com.team9.sungdaehanmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u.favoriteItems FROM User u WHERE u.idx = :userId")
    List<Long> findFavoriteItemsByIdx(@Param("userId") Long userId);
    // 이메일이 존재하는지 확인하는 메서드
    boolean existsByEmail(String email);
}
