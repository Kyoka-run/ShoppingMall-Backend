package com.mall.service;

import com.mall.service.impl.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private CacheService cacheService;

    @Mock
    private HttpServletRequest request;

    private JwtService jwtService;
    private final String SECRET_KEY = "dGhpc2lzYXRlc3RrZXlmb3JqdW5pdHRlc3Rpbmdqd3RzZXJ2aWNl";
    private final Long EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(cacheService);
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", EXPIRATION);
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_Success() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void isTokenValid_Success() {
        String token = jwtService.generateToken("testuser");
        when(cacheService.get("token:blacklist:" + token)).thenReturn(null);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_BlacklistedToken() {
        String token = jwtService.generateToken("testuser");
        when(cacheService.get("token:blacklist:" + token)).thenReturn("blacklisted");
        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_InvalidToken() {
        String invalidToken = "invalid.jwt.token";
        assertFalse(jwtService.isTokenValid(invalidToken));
    }

    @Test
    void getCurrentToken_Success() {
        String token = "test.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        assertEquals(token, jwtService.getCurrentToken());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getCurrentToken_NoToken() {
        when(request.getHeader("Authorization")).thenReturn(null);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        assertNull(jwtService.getCurrentToken());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void isTokenBlacklisted_True() {
        String token = "test.token";
        when(cacheService.get("token:blacklist:" + token)).thenReturn("blacklisted");
        assertTrue(jwtService.isTokenBlacklisted(token));
    }

    @Test
    void isTokenBlacklisted_False() {
        String token = "test.token";
        when(cacheService.get("token:blacklist:" + token)).thenReturn(null);
        assertFalse(jwtService.isTokenBlacklisted(token));
    }
}