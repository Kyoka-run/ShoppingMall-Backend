package com.mall.service.impl;

import com.mall.dto.request.LoginRequest;
import com.mall.dto.request.RegisterRequest;
import com.mall.dto.response.UserResponse;
import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.exception.ResultCode;
import com.mall.model.User;
import com.mall.repository.UserRepository;
import com.mall.service.JwtService;
import com.mall.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // check user exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(ResultCode.DUPLICATE_USERNAME);
        }

        // create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles(Collections.singletonList("ROLE_USER"));

        User savedUser = userRepository.save(user);
        return createUserResponse(savedUser);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        // find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException(ResultCode.USER_NOT_FOUND));

        // auth password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.INVALID_PASSWORD);
        }

        return createUserResponse(user);
    }

    @Override
    public UserResponse findByUsername(String username) {
        // check cache
        String cacheKey = "user:" + username;
        String cachedUser = redisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            return createUserResponse(userRepository.findByUsername(username).get());
        }

        // if cache not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ResultCode.USER_NOT_FOUND));

        // save user to cache
        redisTemplate.opsForValue().set(cacheKey, username, 24, TimeUnit.HOURS);

        return createUserResponse(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ResultCode.USER_NOT_FOUND));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().toArray(new String[0]))
                .build();
    }

    @Override
    public UserResponse createUserResponse(UserDetails userDetails, String token) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(ResultCode.USER_NOT_FOUND));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        response.setToken(token);
        response.setRefreshToken(jwtService.generateRefreshToken(userDetails));
        return response;
    }

    private UserResponse createUserResponse(User user) {
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateAccessToken(userDetails);
        return createUserResponse(userDetails, accessToken);
    }
}

