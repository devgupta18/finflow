package com.example.finflow.controller;

import com.example.finflow.dto.UserRequestDTO;
import com.example.finflow.dto.UserResponseDTO;
import com.example.finflow.dto.UserUpdateRequestDTO;
import com.example.finflow.entity.User;
import com.example.finflow.mapper.UserMapper;
import com.example.finflow.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        User user = userService.createUser(userRequestDTO.getName(), userRequestDTO.getEmail(), userRequestDTO.getPassword());
        UserResponseDTO userResponseDTO = UserMapper.toUserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        User user = userService.updateUser(id, userUpdateRequestDTO.getName(), userUpdateRequestDTO.getEmail(), userUpdateRequestDTO.getPassword());
        UserResponseDTO userResponseDTO = UserMapper.toUserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }

}