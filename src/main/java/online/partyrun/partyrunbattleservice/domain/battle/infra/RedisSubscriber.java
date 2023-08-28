package online.partyrun.partyrunbattleservice.domain.battle.infra;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisSubscriber implements MessageListener {

    RedisSerializer<BattleWebSocketResponse> redisSerializer;
    SimpMessageSendingOperations messageSendingOperations;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        final BattleWebSocketResponse response = redisSerializer.deserialize(message.getBody());
        messageSendingOperations.convertAndSend("/topic/battles/" + response.getBattleId(), response);
    }
}
