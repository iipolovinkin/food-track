package com.foodtracker.dashboard.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class InMemoryCacheServiceImpl implements CacheService {

    private static class CacheEntry<T> {
        private final T value;
        private final long expiryTime;

        public CacheEntry(T value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public T getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final Map<String, CacheEntry<Object>> cache = new ConcurrentHashMap<>();

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        CacheEntry<Object> entry = cache.get(key);
        if (entry != null) {
            if (entry.isExpired()) {
                cache.remove(key); // Remove expired entry
                return Optional.empty();
            }
            return Optional.ofNullable((T) entry.getValue());
        }
        return Optional.empty();
    }

    @Override
    public <T> void put(String key, T value, int expirySeconds) {
        long expiryTime = System.currentTimeMillis() + (expirySeconds * 1000L);
        cache.put(key, new CacheEntry<>(value, expiryTime));
    }

    @Override
    public void evict(String key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }
}