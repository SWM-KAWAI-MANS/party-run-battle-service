package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartedResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerFinishedResponse;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerFinishedEvent;
import online.partyrun.partyrunbattleservice.domain.battle.infra.RedisPublisher;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

@Async
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleEventHandler {
    BattleService battleService;
    RedisPublisher messagePublisher;

    @EventListener
    public void setBattleRunning(BattleRunningEvent event) {
        final BattleStartedResponse response = battleService.start(event.battleId());
        messagePublisher.publish(response);
    }

    @EventListener
    public void publishRunnerFinished(RunnerFinishedEvent event) {
        final RunnerFinishedResponse response = new RunnerFinishedResponse(event.battleId(), event.runnerId());
        messagePublisher.publish(response);
    }
}
