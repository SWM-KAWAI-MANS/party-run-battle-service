package online.partyrun.partyrunbattleservice.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;
import online.partyrun.partyrunbattleservice.domain.battle.infra.RedisSubscriber;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleResponse;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory, RedisSubscriber redisSubscriber, Topic topic) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, topic);
        return container;
    }

    @Bean
    public Topic topic() {
        return new ChannelTopic("battle");
    }

    @Bean
    public RedisSerializer<BattleWebSocketResponse> redisSerializer() {
        return new Jackson2JsonRedisSerializer<>(objectMapper(), BattleWebSocketResponse.class);
    }

    private ObjectMapper objectMapper() {
        final JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    public RedisTemplate<String, BattleWebSocketResponse> redisTemplate(RedisConnectionFactory connectionFactory,
                                                                        RedisSerializer<BattleWebSocketResponse> redisSerializer) {
        final RedisTemplate<String, BattleWebSocketResponse> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(redisSerializer);

        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        final RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));

        final HashMap<String, RedisCacheConfiguration> configurations = new HashMap<>();
        configurations.put("single", defaultConfiguration.entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper(), SingleResponse.class)))
        );

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withInitialCacheConfigurations(configurations)
                .build();
    }
}
