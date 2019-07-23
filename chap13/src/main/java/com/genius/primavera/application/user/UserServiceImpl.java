package com.genius.primavera.application.user;

import com.genius.primavera.domain.model.user.User;
import com.genius.primavera.domain.model.user.UserConnection;
import com.genius.primavera.domain.repository.UserRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User signUp(UserConnection userConnection) {
        return null;
    }

    @Override
    public boolean isExistUser(String email) {
        return false;
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElse(new User());
    }

    @Override
    public User update(User user) {
        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(new User());
    }
}