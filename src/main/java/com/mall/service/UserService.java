package com.mall.service;

import com.mall.dto.AuthRequest;
import com.mall.dto.AuthResponse;
import com.mall.dto.PasswordResetRequest;
import com.mall.dto.RegisterRequest;
import com.mall.model.User;

public interface UserService {
    AuthResponse authenticate(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    void logout(String username);
    void resetPassword(PasswordResetRequest request);
    User findByUsername(String username);
}