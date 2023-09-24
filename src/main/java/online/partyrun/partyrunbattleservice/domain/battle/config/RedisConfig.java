package online.partyrun.partyrunbattleservice.domain.battle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;
import online.partyrun.partyrunbattleservice.domain.battle.infra.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.aggregation.SelectionOperators;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        final JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        return new Jackson2JsonRedisSerializer<>(objectMapper, BattleWebSocketResponse.class);
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
}
