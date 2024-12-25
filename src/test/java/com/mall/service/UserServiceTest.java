package com.mall.service;

import com.mall.dto.AuthRequest;
import com.mall.dto.AuthResponse;
import com.mall.dto.PasswordResetRequest;
import com.mall.dto.RegisterRequest;
import com.mall.exception.UnauthorizedException;
import com.mall.model.User;
import com.mall.repository.UserRepository;
import com.mall.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CacheService cacheService;

    private UserService userService;
    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService, authenticationManager, cacheService);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password");
        testUser.setEmail("test@example.com");
        testUser.setRoles(Arrays.asList("ROLE_USER"));

        testToken = "test.jwt.token";
    }

    @Test
    void authenticate_Success() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(String.class))).thenReturn(testToken);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password123"));

        AuthResponse response = userService.authenticate(request);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testToken, response.getAccessToken());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(String.class))).thenReturn(testToken);

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testToken, response.getAccessToken());
    }

    @Test
    void register_UserExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThrows(UnauthorizedException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    void findByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> {
            userService.findByUsername("nonexistent");
        });
    }

    @Test
    void logout_Success() {
        when(jwtService.getCurrentToken()).thenReturn(testToken);

        userService.logout("testuser");

        verify(cacheService).set(eq("token:blacklist:" + testToken), eq("blacklisted"), eq(24L));
    }

    @Test
    void resetPassword_Success() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setOldPassword("oldpass");
        request.setNewPassword("newpass");

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldpass", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_newpass");

        userService.resetPassword(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_WrongOldPassword() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setOldPassword("wrongpass");
        request.setNewPassword("newpass");

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", testUser.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            userService.resetPassword(request);
        });
    }
}