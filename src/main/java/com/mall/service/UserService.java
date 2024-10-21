package com.mall.service;

import com.mall.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User registerUser(User user);
    User findByUsername(String username);
}
