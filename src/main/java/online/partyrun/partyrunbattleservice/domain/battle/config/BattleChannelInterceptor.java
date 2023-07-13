package online.partyrun.partyrunbattleservice.domain.battle.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidSubscribeRequestException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;

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

    static final String BATTLE_TOPIC_PREFIX = "/topic/battle/";
    BattleRepository battleRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (isSubscribeToBattleTopic(accessor)) {
            validateSubscription(accessor);
        }

        return message;
    }

    private boolean isSubscribeToBattleTopic(StompHeaderAccessor accessor) {
        final String destination = accessor.getDestination();
        return StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                && Objects.nonNull(destination)
                && destination.contains(BATTLE_TOPIC_PREFIX);
    }

    private void validateSubscription(StompHeaderAccessor accessor) {
        final String runnerId = getRunnerId(accessor);
        String battleId = extractBattleId(accessor);

        if (!battleRepository.existsByIdAndRunnersIdAndStatus(
                battleId, runnerId, BattleStatus.READY)) {

            throw new InvalidSubscribeRequestException(battleId, runnerId);
        }
    }

    private String getRunnerId(StompHeaderAccessor accessor) {
        final Principal auth = Objects.requireNonNull(accessor.getUser());
        return Objects.requireNonNull(auth.getName());
    }

    private String extractBattleId(StompHeaderAccessor accessor) {
        final String destination = Objects.requireNonNull(accessor.getDestination());
        final String[] parts = destination.split(BATTLE_TOPIC_PREFIX);
        return parts[parts.length - 1];
    }
}
