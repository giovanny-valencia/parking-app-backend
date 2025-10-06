package com.parkingapp.backendapi.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // Available at runtime for the Interceptor to read
public @interface RateLimit {
  int limit() default 10; // Default max requests

  long timeWindowSeconds() default 60; // Default window (60 seconds)

  String keyPrefix() default "ip"; // Key type (e.g., "ip", "userId")
}
