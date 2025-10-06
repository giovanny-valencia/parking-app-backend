package com.parkingapp.backendapi.infrastructure.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();

    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());

    // If RedisTemplate was designed for full object caching
    // template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    //    Jackson2JsonRedisSerializer<Object> serializer = new
    // Jackson2JsonRedisSerializer<>(Object.class);
    //    template.setDefaultSerializer(serializer);
    //    template.setKeySerializer(new StringRedisSerializer());
    //    template.setHashKeySerializer(new StringRedisSerializer());
    //    template.setValueSerializer(serializer);

    template.setHashKeySerializer(new StringRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }
}
