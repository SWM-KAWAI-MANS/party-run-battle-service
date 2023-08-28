package online.partyrun.partyrunbattleservice.domain.battle.infra;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisPublisher {

    Topic topic;
    RedisTemplate<String, BattleWebSocketResponse> redisTemplate;

    public void publish(BattleWebSocketResponse value) {
        redisTemplate.convertAndSend(topic.getTopic(), value);
    }
}
