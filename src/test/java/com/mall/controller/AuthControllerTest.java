package com.mall.controller;

import com.mall.dto.AuthRequest;
import com.mall.dto.AuthResponse;
import com.mall.dto.PasswordResetRequest;
import com.mall.dto.RegisterRequest;
import com.mall.exception.UnauthorizedException;
import com.mall.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void login_Success() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        AuthResponse response = AuthResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .roles(Arrays.asList("ROLE_USER"))
                .accessToken("test.jwt.token")
                .build();

        when(userService.authenticate(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.accessToken").value("test.jwt.token"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("wronguser");
        request.setPassword("wrongpass");

        when(userService.authenticate(any(AuthRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        AuthResponse response = AuthResponse.builder()
                .id(1L)
                .username("newuser")
                .email("new@example.com")
                .roles(Arrays.asList("ROLE_USER"))
                .accessToken("test.jwt.token")
                .build();

        when(userService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void register_UserExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password");
        request.setEmail("existing@example.com");

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new UnauthorizedException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_InvalidData() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("pwd");
        request.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void logout_Success() throws Exception {
        doNothing().when(userService).logout(anyString());

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());

        verify(userService).logout(anyString());
    }

    @Test
    @WithMockUser
    void resetPassword_Success() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setOldPassword("oldpass");
        request.setNewPassword("newpass");

        doNothing().when(userService).resetPassword(any(PasswordResetRequest.class));

        mockMvc.perform(post("/api/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).resetPassword(any(PasswordResetRequest.class));
    }

    @Test
    @WithMockUser
    void resetPassword_WrongOldPassword() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setOldPassword("wrongpass");
        request.setNewPassword("newpass");

        doThrow(new UnauthorizedException("Invalid old password"))
                .when(userService).resetPassword(any(PasswordResetRequest.class));

        mockMvc.perform(post("/api/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}