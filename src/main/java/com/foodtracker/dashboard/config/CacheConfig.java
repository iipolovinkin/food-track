package com.foodtracker.dashboard.config;

import com.foodtracker.dashboard.cache.CacheService;
import com.foodtracker.dashboard.cache.InMemoryCacheServiceImpl;
import com.foodtracker.dashboard.cache.RedisCacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class CacheConfig {

    @Value("${app.cache.type:in-memory}")
    private String cacheType;

    @Bean
    public CacheService cacheService(RedisTemplate<String, Object> redisTemplate) {
        if ("redis".equalsIgnoreCase(cacheType)) {
            log.info("Redis cache enabled");
            return new RedisCacheServiceImpl(redisTemplate);
        } else {
            return new InMemoryCacheServiceImpl();
        }
    }
}