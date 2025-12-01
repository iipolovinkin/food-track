package com.foodtracker.dashboard.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisCacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new RedisCacheServiceImpl(redisTemplate);
    }

    @Test
    void get_WithValidKeyAndCorrectType_ShouldReturnCachedValue() {
        // Given
        String key = "testKey";
        String value = "testValue";
        when(valueOperations.get(key)).thenReturn(value);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        Optional<String> result = cacheService.get(key, String.class);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(value);
        verify(valueOperations).get(key);
    }

    @Test
    void get_WithNonExistentKey_ShouldReturnEmptyOptional() {
        // Given
        String key = "nonExistentKey";
        when(valueOperations.get(key)).thenReturn(null);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        Optional<String> result = cacheService.get(key, String.class);

        // Then
        assertThat(result).isEmpty();
        verify(valueOperations).get(key);
    }

    @Test
    void get_WithMapValue_ShouldConvertToRequestedType() {
        // Given
        String key = "mapKey";
        Map<String, Object> mapValue = new HashMap<>();
        mapValue.put("name", "John Doe");
        mapValue.put("age", 30);
        Person expectedPerson = new Person("John Doe", 30);

        when(valueOperations.get(key)).thenReturn(mapValue);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        Optional<Person> result = cacheService.get(key, Person.class);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(expectedPerson.getName());
        assertThat(result.get().getAge()).isEqualTo(expectedPerson.getAge());
        verify(valueOperations).get(key);
    }

    @Test
    void get_WithUnconvertibleMapValue_ShouldReturnEmptyOptional() {
        // Given
        String key = "invalidMapKey";
        Map<String, Object> mapValue = new HashMap<>();
        mapValue.put("invalidField", new Object()); // This will cause conversion to fail

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(mapValue);

        // When
        Optional<Person> result = cacheService.get(key, Person.class);

        // Then
        assertThat(result).isEmpty();
        verify(valueOperations).get(key);
    }

    @Test
    void put_WithValueAndExpiry_ShouldSetInRedisWithExpiry() {
        // Given
        String key = "testKey";
        String value = "testValue";
        int expirySeconds = 60;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        cacheService.put(key, value, expirySeconds);

        // Then
        verify(valueOperations).set(eq(key), eq(value), eq((long) expirySeconds), eq(TimeUnit.SECONDS));
    }

    @Test
    void evict_WithExistingKey_ShouldDeleteFromRedis() {
        // Given
        String key = "keyToEvict";

        // When
        cacheService.evict(key);

        // Then
        verify(redisTemplate).delete(key);
    }

    @Test
    void evict_WithNonExistentKey_ShouldNotThrowException() {
        // Given
        String key = "nonExistentKey";

        // When & Then (should not throw any exception)
        assertThatCode(() -> cacheService.evict(key)).doesNotThrowAnyException();
        verify(redisTemplate).delete(key);
    }

    @Test
    void clear_WithMultipleEntries_ShouldFlushAllRedisData() {
        // Given
        var connectionFactory = mock(org.springframework.data.redis.connection.RedisConnectionFactory.class);
        var connection = mock(org.springframework.data.redis.connection.RedisConnection.class);
        var serverCommands = mock(org.springframework.data.redis.connection.RedisServerCommands.class);

        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.serverCommands()).thenReturn(serverCommands);

        // When
        cacheService.clear();

        // Then
        verify(serverCommands).flushAll();
    }

    @Test
    void get_WithDifferentTypes_ShouldHandleCorrectly() {
        // Given
        String stringKey = "stringKey";
        String intKey = "intKey";
        String stringValue = "testString";
        Integer intValue = 42;

        when(valueOperations.get(stringKey)).thenReturn(stringValue);
        when(valueOperations.get(intKey)).thenReturn(intValue);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        Optional<String> stringResult = cacheService.get(stringKey, String.class);
        Optional<Integer> intResult = cacheService.get(intKey, Integer.class);

        // Then
        assertThat(stringResult).isPresent().get().isEqualTo(stringValue);
        assertThat(intResult).isPresent().get().isEqualTo(intValue);
        verify(valueOperations).get(stringKey);
        verify(valueOperations).get(intKey);
    }

    @Test
    void get_WithUnexpectedCachedValueType_ShouldReturnEmptyOptional() {
        // Given
        String key = "unexpectedTypeKey";
        Object unexpectedValue = new Object(); // This is not a Map or the expected type
        when(valueOperations.get(key)).thenReturn(unexpectedValue);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        Optional<String> result = cacheService.get(key, String.class);

        // Then
        assertThat(result).isEmpty();
        verify(valueOperations).get(key);
    }

    // Helper class for testing Map conversion
    @Getter
    @AllArgsConstructor
    static class Person {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

    }
}