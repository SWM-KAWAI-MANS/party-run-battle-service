package online.partyrun.partyrunbattleservice.domain.record.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.record.service.RunnerRecordService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordEventHandler {

    RunnerRecordService runnerRecordService;

    @EventListener
    public void setBattleRunning(BattleRunningEvent event) {
        runnerRecordService.createBattleRecord(event.battleId());
    }
}
