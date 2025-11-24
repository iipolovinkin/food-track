package com.foodtracker.dashboard.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        Object cachedValue = redisTemplate.opsForValue().get(key);

        if (cachedValue == null) {
            return Optional.empty();
        }

        // If the cached value is already the correct type, return it directly
        if (type.isInstance(cachedValue)) {
            log.debug("Cache hit for key: {}, value: {}", key, cachedValue);
            return Optional.of(type.cast(cachedValue));
        }

        // If the cached value is a Map (like LinkedHashMap from JSON deserialization), convert it
        if (cachedValue instanceof Map) {
            try {
                T convertedValue = objectMapper.convertValue(cachedValue, type);
                log.debug("Cache hit for key: {}, converted from Map to type: {}", key, type.getSimpleName());
                return Optional.ofNullable(convertedValue);
            } catch (Exception e) {
                log.error("Error converting cached Map to type: {}", type.getSimpleName(), e);
                return Optional.empty();
            }
        }

        log.warn("Unexpected cached value type: {} for expected type: {}",
                cachedValue.getClass().getName(), type.getName());
        return Optional.empty();
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
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}