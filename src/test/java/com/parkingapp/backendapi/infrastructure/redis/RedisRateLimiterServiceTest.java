package com.parkingapp.backendapi.infrastructure.redis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisRateLimiterServiceTest {

  @Mock private RedisTemplate<String, Object> redisTemplate;

  // Mock the specific part of RedisTemplate used for key-value ops
  @Mock private ValueOperations<String, Object> valueOperations;

  // 3. Inject the mocks into the service being tested
  @InjectMocks private RedisRateLimiterService redisRateLimiterService;

  private static final String TEST_KEY = "test:key";
  private static final long TIMEOUT_SECONDS = 30L;

  @BeforeEach
  void setup() {
    // Ensure that redisTemplate.opsForValue() always returns our mock valueOperations
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  // --- Test Cases ---

  @Test
  void incrementAndExpire_firstCall_setsExpirationAndReturnsOne() {
    when(valueOperations.increment(TEST_KEY)).thenReturn(1L);

    long count = redisRateLimiterService.incrementAndExpire(TEST_KEY, TIMEOUT_SECONDS);

    assertEquals(1L, count, "Count should be 1 on the first call");

    verify(redisTemplate, times(1)).expire(eq(TEST_KEY), eq(Duration.ofSeconds(TIMEOUT_SECONDS)));
  }

  @Test
  void incrementAndExpire_returnsZeroIfIncrementReturnsNull() {
    when(valueOperations.increment(TEST_KEY)).thenReturn(null);

    long count = redisRateLimiterService.incrementAndExpire(TEST_KEY, TIMEOUT_SECONDS);

    assertEquals(0L, count, "Count should be 0 if Redis returns null");
    verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
  }
}
