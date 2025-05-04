package com.system.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(){
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues();
    }
}
