package com.innowise.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final CacheProperties cacheProperties;

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.
                        fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> ttlConfiguration = new HashMap<>();

        ttlConfiguration.put("user", config.entryTtl(cacheProperties.getTtl().get("user")));
        ttlConfiguration.put("card", config.entryTtl(cacheProperties.getTtl().get("card")));
        ttlConfiguration.put("cards", config.entryTtl(cacheProperties.getTtl().get("cards")));

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(ttlConfiguration)
                .build();
    }
}
