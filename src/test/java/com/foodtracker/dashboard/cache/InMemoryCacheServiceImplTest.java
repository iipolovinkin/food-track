package com.foodtracker.dashboard.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.awaitility.Awaitility.await;

@ExtendWith(MockitoExtension.class)
class InMemoryCacheServiceImplTest {

    private InMemoryCacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new InMemoryCacheServiceImpl();
    }

    @Test
    void putAndGet_WithValidKeyAndType_ShouldReturnCachedValue() {
        // Given
        String key = "testKey";
        String value = "testValue";
        int expirySeconds = 30;

        // When
        cacheService.put(key, value, expirySeconds);
        Optional<String> result = cacheService.get(key, String.class);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(value);
    }

    @Test
    void get_WithNonExistentKey_ShouldReturnEmptyOptional() {
        // Given
        String key = "nonExistentKey";

        // When
        Optional<String> result = cacheService.get(key, String.class);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void get_WithExpiredKey_ShouldReturnEmptyOptionalAndRemoveEntry() {
        // Given
        String key = "expiringKey";
        String value = "expiringValue";
        int expirySeconds = 1; // 1 second expiry

        // When
        cacheService.put(key, value, expirySeconds);

        // Wait for the cache entry to expire
        await().atMost(java.time.Duration.ofSeconds(2))
                .until(() -> cacheService.get(key, String.class).isEmpty());

        // Then
        Optional<String> resultAfterExpiry = cacheService.get(key, String.class);
        assertThat(resultAfterExpiry).isEmpty();
    }

    @Test
    void evict_WithExistingKey_ShouldRemoveEntry() {
        // Given
        String key = "keyToEvict";
        String value = "valueToEvict";
        cacheService.put(key, value, 30);

        // When
        cacheService.evict(key);

        // Then
        Optional<String> result = cacheService.get(key, String.class);
        assertThat(result).isEmpty();
    }

    @Test
    void evict_WithNonExistentKey_ShouldNotThrowException() {
        // Given
        String key = "nonExistentKey";

        // When & Then (should not throw any exception)
        assertThatCode(() -> cacheService.evict(key)).doesNotThrowAnyException();
    }

    @Test
    void clear_WithMultipleEntries_ShouldRemoveAllEntries() {
        // Given
        String key1 = "key1";
        String key2 = "key2";
        String value1 = "value1";
        String value2 = "value2";
        
        cacheService.put(key1, value1, 30);
        cacheService.put(key2, value2, 30);

        // Verify entries exist before clearing
        assertThat(cacheService.get(key1, String.class)).isPresent();
        assertThat(cacheService.get(key2, String.class)).isPresent();

        // When
        cacheService.clear();

        // Then
        assertThat(cacheService.get(key1, String.class)).isEmpty();
        assertThat(cacheService.get(key2, String.class)).isEmpty();
    }

    @Test
    void put_WithDifferentTypes_ShouldHandleCorrectly() {
        // Given
        String stringKey = "stringKey";
        String intKey = "intKey";
        String stringValue = "testString";
        Integer intValue = 42;

        // When
        cacheService.put(stringKey, stringValue, 30);
        cacheService.put(intKey, intValue, 30);

        // Then
        Optional<String> stringResult = cacheService.get(stringKey, String.class);
        Optional<Integer> intResult = cacheService.get(intKey, Integer.class);
        
        assertThat(stringResult).isPresent().get().isEqualTo(stringValue);
        assertThat(intResult).isPresent().get().isEqualTo(intValue);
    }

    @Test
    void put_WithZeroExpiry_ShouldStillCacheValue() {
        // Given
        String key = "zeroExpiryKey";
        String value = "zeroExpiryValue";

        // When
        cacheService.put(key, value, 0); // 0 seconds expiry (should expire immediately)

        // Then
        // The value might still be available if we check immediately after putting it
        Optional<String> result = cacheService.get(key, String.class);
        assertThat(result).isPresent(); // Should be present as no time has passed yet
    }

    @Test
    void concurrentAccess_ShouldBeThreadSafe() {
        // Given
        String key = "concurrentKey";
        String value = "concurrentValue";
        cacheService.put(key, value, 30);

        // When
        // Testing that multiple threads can access the cache safely
        Runnable getTask = () -> {
            for (int i = 0; i < 100; i++) {
                cacheService.get(key, String.class);
            }
        };
        
        Runnable putTask = () -> {
            for (int i = 0; i < 10; i++) {
                cacheService.put("concurrentKey" + i, "value" + i, 30);
            }
        };

        Thread thread1 = new Thread(getTask);
        Thread thread2 = new Thread(putTask);
        Thread thread3 = new Thread(getTask);

        // Then (should not throw any ConcurrentModificationException or similar)
        thread1.start();
        thread2.start();
        thread3.start();
        
        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test thread interrupted", e);
        }
        
        // After concurrent operations, the original value should still be accessible
        Optional<String> result = cacheService.get(key, String.class);
        assertThat(result).isPresent().get().isEqualTo(value);
    }
}