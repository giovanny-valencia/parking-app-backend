package com.parkingapp.backendapi.infrastructure.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class RedisRateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Executes the atomic rate limiting logic (INCR + conditional EXPIRE).
     *
     * @param key  value stored on redis
     * @param timeoutSeconds time to live
     * @return new count value.
     */
    public long incrementAndExpire(String key, long timeoutSeconds){

        // increment counter
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1){
            redisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds));
        }

        return count != null ? count : 0;
    }
}
