package com.example.jiufu.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

@Configuration
public class MyRedisConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.opsForValue();
        template.setConnectionFactory(redisConnectionFactory);

        logger.info("hello world");

        return template;
    }
}
