package com.sean.ninnong.auth.repository;

import com.sean.ninnong.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
