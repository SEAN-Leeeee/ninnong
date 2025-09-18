package com.sean.ninnong.user.repository;

import com.sean.ninnong.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Transactional(readOnly = true)
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findUserInfoById(Long userId);

    @Transactional(readOnly = true)
    @Query("SELECT u FROM User u WHERE u.id IN :userIdList")
    List<User> findUserInfoById(List<Long> userIdList);
}
