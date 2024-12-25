package com.mall.service.impl;

import com.mall.dto.AuthRequest;
import com.mall.dto.AuthResponse;
import com.mall.dto.PasswordResetRequest;
import com.mall.dto.RegisterRequest;
import com.mall.exception.UnauthorizedException;
import com.mall.model.User;
import com.mall.repository.UserRepository;
import com.mall.service.CacheService;
import com.mall.service.JwtService;
import com.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CacheService cacheService;
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final long TOKEN_BLACKLIST_HOURS = 24;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = findByUsername(request.getUsername());
        String token = jwtService.generateToken(user.getUsername());

        return buildAuthResponse(user, token);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UnauthorizedException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles(Arrays.asList("ROLE_USER"));

        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername());

        return buildAuthResponse(user, token);
    }

    @Override
    public void logout(String username) {
        String currentToken = jwtService.getCurrentToken();
        if (currentToken != null) {
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + currentToken;
            cacheService.set(blacklistKey, "blacklisted", TOKEN_BLACKLIST_HOURS);
        }
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findByUsername(username);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .accessToken(token)
                .build();
    }
}