package com.mall.service;

public interface CacheService {
    void set(String key, String value, long timeoutHours);
    String get(String key);
    void delete(String key);
    boolean hasKey(String key);
}