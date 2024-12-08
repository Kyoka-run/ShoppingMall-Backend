package com.mall.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String token; // JWT token
    private String refreshToken;
}