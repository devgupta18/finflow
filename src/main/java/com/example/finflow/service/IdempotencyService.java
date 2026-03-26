package com.example.finflow.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import static java.util.concurrent.TimeUnit.HOURS;

@Service
public class IdempotencyService {
    private final RedisTemplate<String, String> redisTemplate;

    public IdempotencyService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isIdempotent(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("idempotency:" + key));
    }

    public void storeKey(String key) {
        redisTemplate.opsForValue().set("idempotency:" +  key, "processed", 24, HOURS);
    }
}
