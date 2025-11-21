package com.foodtracker.dashboard.cache;

import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, Class<T> type);
    <T> void put(String key, T value, int expirySeconds);
    void evict(String key);
    void clear();
}