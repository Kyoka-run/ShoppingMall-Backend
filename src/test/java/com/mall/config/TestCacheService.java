package com.mall.config;

import com.mall.service.CacheService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Profile("test")
@Primary
public class TestCacheService implements CacheService {
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public void set(String key, String value, long timeoutHours) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
    }

    @Override
    public boolean hasKey(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void setHash(String key, String hashKey, String value, long timeoutHours) {
        cache.put(key + ":" + hashKey, value);
    }

    @Override
    public String getHash(String key, String hashKey) {
        return cache.get(key + ":" + hashKey);
    }
}

