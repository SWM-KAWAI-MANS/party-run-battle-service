package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.LocationDto;
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
    SimpMessagingTemplate messagingTemplate;
    BattleService battleService;

    @MessageMapping("/battle/{battleId}")
    public void messageMapping(@DestinationVariable String battleId, LocationDto request) {
        this.messagingTemplate.convertAndSend("/topic/battle/" + battleId, request);
    }

    @MessageMapping("/battle/{battleId}/ready")
    public void setRunnerRunning(@DestinationVariable String battleId, Authentication auth) {
        final String memberId = auth.getName();
        battleService.setRunnerRunning(battleId, memberId);
    }
}
