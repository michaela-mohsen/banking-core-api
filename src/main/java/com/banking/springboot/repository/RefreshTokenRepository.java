package com.banking.springboot.repository;

import com.banking.springboot.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);

    RefreshToken findByUserId(Integer id);
}
