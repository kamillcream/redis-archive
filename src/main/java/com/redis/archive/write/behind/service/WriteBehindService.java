package com.redis.archive.write.behind.service;

import com.redis.archive.entity.Test;
import com.redis.archive.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WriteBehindService {
    private final RedisTemplate<String, String> redisTemplate;
    private final TestRepository testRepository;

    private static final String CACHE_PREFIX = "test2:";

    public void saveTest(String testId, String name){
        redisTemplate.opsForValue().set(CACHE_PREFIX + testId, name);
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void syncToDB(){
        Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
        if (keys != null){
            for (String key : keys){
                String testId = key.replace(CACHE_PREFIX, "");
                String name = redisTemplate.opsForValue().get(key);
                System.out.println(testId);
                testRepository.save(new Test(testId, name));
                redisTemplate.delete(key);
            }
        }
    }

    @Bean
    @Async
    public ApplicationRunner runWriteBehind(){
        return args -> {
            while(true){
                int keyCount = 0;
                ScanOptions options = ScanOptions.scanOptions().match(CACHE_PREFIX + "*").count(100).build();
                Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

                while (cursor.hasNext()) {
                    keyCount++;
                    cursor.next();
                }

                if (keyCount >= 2){
                    syncToDB();
                }

                System.out.println("캐시의 크기가 " + keyCount + " 입니다.");

                Thread.sleep(5000);
            }
        };
    }
}
