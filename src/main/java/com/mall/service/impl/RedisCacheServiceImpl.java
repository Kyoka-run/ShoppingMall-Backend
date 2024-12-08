package com.mall.service.impl;

import com.mall.service.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheServiceImpl implements CacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String USER_PREFIX = "user:";
    private static final String PRODUCT_PREFIX = "product:";

    public RedisCacheServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, long timeoutHours) {
        redisTemplate.opsForValue().set(key, value, timeoutHours, TimeUnit.HOURS);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}