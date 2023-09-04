package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartedResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerFinishedResponse;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerFinishedEvent;
import online.partyrun.partyrunbattleservice.domain.battle.infra.RedisPublisher;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.*;

@Slf4j
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
        log.info(response.toString());
    }

    @EventListener
    public void publishRunnerFinished(RunnerFinishedEvent event) {
        final RunnerFinishedResponse response = new RunnerFinishedResponse(event.battleId(), event.runnerId());
        messagePublisher.publish(response);
        log.info(response.toString());
    }

    @EventListener
    public void handleWebSocketSessionConnectEvent(SessionConnectEvent event) {
        writeLog(event.getUser().getName(), "Connect");
    }

    @EventListener
    public void handleWebSocketSessionConnectedEvent(SessionConnectedEvent event) {
        writeLog(event.getUser().getName(), "Connected");
    }

    @EventListener
    public void handleWebSocketSessionSubscribeEvent(SessionSubscribeEvent event) {
        writeLog(event.getUser().getName(), "Subscribe");
    }

    @EventListener
    public void handleWebSocketSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        writeLog(event.getUser().getName(), "Unsubscribe");
    }

    @EventListener
    public void handleWebSocketSessionDisconnectEvent(SessionDisconnectEvent event) {
        writeLog(event.getUser().getName(), "Disconnected");
    }

    private void writeLog(String memberId, String status) {
        log.info(String.format("멤버 : %s, 웹소켓 상태 : %s", memberId, status));
    }
}
