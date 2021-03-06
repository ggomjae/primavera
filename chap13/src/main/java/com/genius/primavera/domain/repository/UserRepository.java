package com.genius.primavera.domain.repository;

import com.genius.primavera.domain.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    <T> List<T> findByNickname(String nickname, Class<T> type);
}
