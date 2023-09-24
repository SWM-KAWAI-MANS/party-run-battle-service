package online.partyrun.partyrunbattleservice.domain.battle.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleChannelInterceptor implements ChannelInterceptor {

    private static final String BATTLE_TOPIC_PREFIX = "/topic/battles/";

    BattleService battleService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (isSubscribeToBattleTopic(accessor)) {
            final String battleId = extractBattleId(accessor);
            final String runnerId = getRunnerId(accessor);
            battleService.setRunnerRunning(battleId, runnerId);
        }

        return message;
    }

    private boolean isSubscribeToBattleTopic(StompHeaderAccessor accessor) {
        final String destination = accessor.getDestination();
        return StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                && Objects.nonNull(destination)
                && destination.contains(BATTLE_TOPIC_PREFIX);
    }


    private String extractBattleId(StompHeaderAccessor accessor) {
        final String destination = Objects.requireNonNull(accessor.getDestination());
        final String[] parts = destination.split(BATTLE_TOPIC_PREFIX);
        return parts[parts.length - 1];
    }

    private String getRunnerId(StompHeaderAccessor accessor) {
        final Principal auth = Objects.requireNonNull(accessor.getUser());
        return Objects.requireNonNull(auth.getName());
    }
}
