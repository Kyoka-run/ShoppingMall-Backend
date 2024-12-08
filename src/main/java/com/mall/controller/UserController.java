package com.mall.controller;

import com.mall.dto.request.LoginRequest;
import com.mall.dto.request.RegisterRequest;
import com.mall.dto.response.UserResponse;
import com.mall.exception.ResponseResult;
import com.mall.exception.UnauthorizedException;
import com.mall.service.JwtService;
import com.mall.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseResult<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseResult.success(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseResult<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseResult.success(userService.login(request));
    }

    @GetMapping("/user/{username}")
    public ResponseResult<UserResponse> getUserInfo(@PathVariable String username) {
        return ResponseResult.success(userService.findByUsername(username));
    }

    @PostMapping("/logout")
    public ResponseResult<Void> logout(@RequestHeader("Authorization") String token) {
        jwtService.invalidateToken(token);
        return ResponseResult.success(null);
    }

    @PostMapping("/refresh")
    public ResponseResult<UserResponse> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        String accessToken = jwtService.generateAccessToken(userDetails);
        return ResponseResult.success(userService.createUserResponse(userDetails, accessToken));
    }
}
