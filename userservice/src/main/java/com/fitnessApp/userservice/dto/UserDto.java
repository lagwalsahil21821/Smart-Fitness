package com.fitnessApp.userservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
