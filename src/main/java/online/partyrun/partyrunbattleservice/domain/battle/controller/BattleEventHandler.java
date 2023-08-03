package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerFinishedResponse;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerFinishedEvent;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

@Async
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleEventHandler {
    SimpMessagingTemplate messagingTemplate;
    BattleService battleService;

    @EventListener
    public void setBattleRunning(BattleRunningEvent event) {
        final BattleWebSocketResponse response = battleService.start(event.battleId());
        messagingTemplate.convertAndSend("/topic/battles/" + event.battleId(), response);
    }

    @EventListener
    public void publishRunnerFinished(RunnerFinishedEvent event) {
        final RunnerFinishedResponse response = new RunnerFinishedResponse(event.runnerId());
        messagingTemplate.convertAndSend("/topic/battles/" + event.battleId(), response);
    }
}
