package com.fitnessApp.userservice.service;

import org.springframework.stereotype.Service;

import com.fitnessApp.userservice.dto.RegisterRequestDto;
import com.fitnessApp.userservice.dto.UserDto;
import com.fitnessApp.userservice.mapper.UserMapper;
import com.fitnessApp.userservice.model.User;
import com.fitnessApp.userservice.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;

    public UserDto getUserProfile(String userId) {
        User user = repository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found!"));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto register(RegisterRequestDto request) {
        if(repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        User user = UserMapper.mapToUser(request);
        User savedUser = repository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }
}
