package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartTimeResponse;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

@Slf4j
@Async
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleEventHandler {
    SimpMessagingTemplate messagingTemplate;
    BattleService battleService;

    @EventListener
    public void setBattleRunning(BattleRunningEvent event) {
        final BattleStartTimeResponse response = battleService.setBattleRunning(event.battleId());
        messagingTemplate.convertAndSend("/topic/battle/" + event.battleId(), response);
    }
}
