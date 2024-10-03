package com.team9.sungdaehanmarket.repository;

import com.team9.sungdaehanmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // 이메일이 존재하는지 확인하는 메서드
    boolean existsByEmail(String email);
}
