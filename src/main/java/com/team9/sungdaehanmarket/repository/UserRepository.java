package com.team9.sungdaehanmarket.repository;

import com.team9.sungdaehanmarket.entity.User;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Username으로 사용자를 찾는 메서드
    Optional<User> findByUsername(String username);

    // 특정 사용자(userId)의 favoriteItems 목록을 조회하는 메서드
    @Query("SELECT u.favoriteItems FROM User u WHERE u.idx = :userId")
    List<Long> findFavoriteItemsByIdx(@Param("userId") Long userId);

    // 이메일 중복 여부를 확인하는 메서드
    boolean existsByEmail(String email);

    // 특정 사용자(userId)의 프로필 정보만 조회하는 메서드
    @Query("SELECT u.profileImage as profileImage, u.name as name, u.major as major, u.rating as rating FROM User u WHERE u.idx = :userId")
    Optional<Tuple> findUserProfileById(@Param("userId") Long userId);
}