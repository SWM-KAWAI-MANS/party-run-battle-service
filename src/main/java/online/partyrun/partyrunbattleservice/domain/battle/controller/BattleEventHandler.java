package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartTimeResponse;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Async
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleEventHandler {
    static Map<RunnerRunningEvent, Integer> RUNNER_RUNNING_EVENTS = new ConcurrentHashMap<>();
    SimpMessagingTemplate messagingTemplate;
    BattleService battleService;

    @EventListener
    public void setBattleRunning(RunnerRunningEvent event) {
        final int eventCount = RUNNER_RUNNING_EVENTS.merge(event, 1, Integer::sum);

        if (event.isTargetCount(eventCount)) {
            final BattleStartTimeResponse response = battleService.setBattleRunning(event.battleId());
            messagingTemplate.convertAndSend("/topic/battle/" + event.battleId(), response);
        }
    }
}
