package online.partyrun.partyrunbattleservice.domain.battle.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerRecordRequest;
import online.partyrun.partyrunbattleservice.domain.battle.infra.RedisPublisher;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleWebsocketController {
    BattleService battleService;
    RedisPublisher messagePublisher;

    @MessageMapping("/battles/{battleId}/record")
    public void calculateDistance(@DestinationVariable String battleId, Authentication auth, @Valid RunnerRecordRequest request) {
        final String runnerId = auth.getName();
        final RunnerDistanceResponse response = battleService.calculateDistance(battleId, runnerId, request);

        messagePublisher.publish(response);
        log.info(response.toString());
    }
}
