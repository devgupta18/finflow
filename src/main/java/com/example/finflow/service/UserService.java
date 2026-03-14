package com.example.finflow.service;

import com.example.finflow.entity.User;
import com.example.finflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        return  userRepository.save(user);
    }
}
