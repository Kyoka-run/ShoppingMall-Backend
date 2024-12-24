package com.mall.service;

import com.mall.dto.AuthRequest;
import com.mall.dto.AuthResponse;
import com.mall.dto.RegisterRequest;
import com.mall.model.User;

public interface UserService {
    AuthResponse authenticate(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    User findByUsername(String username);
}