package com.mall.service;

import com.mall.service.impl.RedisCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new RedisCacheServiceImpl(redisTemplate);
    }

    @Test
    void setTest() {
        String key = "test:key";
        String value = "test-value";
        long timeout = 24;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cacheService.set(key, value, timeout);

        verify(valueOperations).set(eq(key), eq(value), eq(timeout), eq(TimeUnit.HOURS));
    }

    @Test
    void getTest() {
        String key = "test:key";
        String value = "test-value";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(value);

        String result = cacheService.get(key);

        assertEquals(value, result);
        verify(valueOperations).get(key);
    }

    @Test
    void deleteTest() {
        String key = "test:key";
        when(redisTemplate.delete(key)).thenReturn(true);

        cacheService.delete(key);

        verify(redisTemplate).delete(key);
    }

    @Test
    void hasKeyTest() {
        String key = "test:key";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean result = cacheService.hasKey(key);

        assertTrue(result);
        verify(redisTemplate).hasKey(key);
    }
}