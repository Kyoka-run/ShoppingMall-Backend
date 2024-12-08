package com.mall.service;

import com.mall.dto.request.LoginRequest;
import com.mall.dto.request.RegisterRequest;
import com.mall.dto.response.UserResponse;
import com.mall.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse register(RegisterRequest request);
    UserResponse login(LoginRequest request);
    UserResponse findByUsername(String username);
    UserResponse createUserResponse(UserDetails userDetails, String token);
}
