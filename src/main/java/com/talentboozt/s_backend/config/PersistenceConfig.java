package com.talentboozt.s_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableMongoRepositories(basePackages = {
                "com.talentboozt.s_backend.domains.*.repository.mongodb",
                "com.talentboozt.s_backend.shared.*.repository.mongodb"
})
@EnableRedisRepositories(basePackages = {
                "com.talentboozt.s_backend.domains.*.repository.redis",
                "com.talentboozt.s_backend.shared.*.repository.redis"
}, redisTemplateRef = "redisRepoTemplate")
public class PersistenceConfig {

        @Bean
        public RedisTemplate<Object, Object> redisRepoTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<Object, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);
                template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
                template.setKeySerializer(RedisSerializer.string());
                template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                return template;
        }
}
