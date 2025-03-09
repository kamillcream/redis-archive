package com.redis.archive.util;

import com.redis.archive.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DummyDataLoader {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "test2:";

    @Bean
    public ApplicationRunner loadDummyData() {
        return args -> {
            if (Boolean.FALSE.equals(redisTemplate.hasKey(REDIS_KEY_PREFIX + "1"))) {
                redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + "1", "alice@example.com");
                redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + "2", "bob@example.com");
                System.out.println("Redis에 더미 데이터 저장 완료");
            } else {
                System.out.println("Redis에 이미 데이터가 존재합니다.");
            }
        };
    }
}