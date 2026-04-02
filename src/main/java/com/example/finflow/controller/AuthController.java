package com.example.finflow.controller;

import com.example.finflow.dto.AuthRequestDTO;
import com.example.finflow.dto.AuthResponseDTO;
import com.example.finflow.dto.UserRequestDTO;
import com.example.finflow.dto.UserResponseDTO;
import com.example.finflow.entity.User;
import com.example.finflow.mapper.UserMapper;
import com.example.finflow.service.JWTService;
import com.example.finflow.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(JWTService jwtService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO  authRequestDTO) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword()));
        UserDetails  userDetails = (UserDetails) authenticate.getPrincipal();
        String token = jwtService.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO userRequestDTO) {
        User user = userService.createUser(userRequestDTO.getName(), userRequestDTO.getEmail(), userRequestDTO.getPassword());
        UserResponseDTO userResponseDTO = UserMapper.toUserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

}
