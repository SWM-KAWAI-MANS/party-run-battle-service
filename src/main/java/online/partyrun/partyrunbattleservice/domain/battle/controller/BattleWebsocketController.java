package online.partyrun.partyrunbattleservice.domain.battle.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerRecordRequest;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleWebsocketController {
    BattleService battleService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/battle/{battleId}/ready")
    public void setRunnerRunning(@DestinationVariable String battleId, Authentication auth) {
        final String memberId = auth.getName();
        battleService.setRunnerRunning(battleId, memberId);
    }

    @MessageMapping("/battle/{battleId}/record")
    public void calculateDistance(
            @DestinationVariable String battleId,
            Authentication auth,
            @Valid RunnerRecordRequest request) {
        final String runnerId = auth.getName();
        final RunnerDistanceResponse response =
                battleService.calculateDistance(battleId, runnerId, request);

        messagingTemplate.convertAndSend("/topic/battle/" + battleId, response);
    }
}
