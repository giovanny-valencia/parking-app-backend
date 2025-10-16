package com.parkingapp.backendapi.infrastructure.redis;

import com.parkingapp.backendapi.common.annotations.RateLimit;
import com.parkingapp.backendapi.common.exception.custom.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

  private final RedisRateLimiterService redisRateLimiterService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // If handler is not a controller method, allow to pass
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }

    // Check for custom annotation on method
    RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

    // If the method is NOT annotated, we allow it to proceed with NO limit enforced.
    if (rateLimit == null) {
      return true;
    }

    // --- Rate limit enforcement

    String clientIdentifier;
    String keyPrefix = rateLimit.keyPrefix();

    if ("userId".equalsIgnoreCase(keyPrefix)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null || !authentication.isAuthenticated()) {
        clientIdentifier = request.getRemoteAddr();
      } else {
        clientIdentifier = authentication.getName();
      }
    } else {
      clientIdentifier = request.getRemoteAddr();
    }

    // Values from annotation
    int limit = rateLimit.limit();
    long timeWindow = rateLimit.timeWindowSeconds();

    // Key construction
    String methodKey = handlerMethod.getMethod().getName();
    // Key format: rate:userId:<identifier>:<method_name>
    String rateLimitKey = "rate:" + keyPrefix + ":" + clientIdentifier + ":" + methodKey;

    // Execute atomic Redis check
    long currentCount = redisRateLimiterService.incrementAndExpire(rateLimitKey, timeWindow);

    if (currentCount > limit) {
      throw new RateLimitExceededException(
          "Too many requests"); // message handled in global handler
    }

    return true;
  }
}
