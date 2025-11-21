package com.foodtracker.dashboard.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        T cachedValue = (T) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(cachedValue);
    }

    @Override
    public <T> void put(String key, T value, int expirySeconds) {
        redisTemplate.opsForValue().set(key, value, expirySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clear() {
        // This would clear all keys in Redis - be careful in production
        // For a more targeted approach, we could implement pattern-based deletion
        log.warn("Clearing entire Redis cache - use with caution in production");
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}