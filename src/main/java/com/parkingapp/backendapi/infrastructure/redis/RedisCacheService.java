package com.parkingapp.backendapi.infrastructure.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class RedisCacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void cacheData(String key, Object value, long timeoutSeconds) {
    redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds));
  }

  public Object getCachedData(String key) {
    return redisTemplate.opsForValue().get(key);
  }
}
