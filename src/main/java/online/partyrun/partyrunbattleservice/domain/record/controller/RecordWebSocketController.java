package online.partyrun.partyrunbattleservice.domain.record.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.record.dto.RecordRequest;
import online.partyrun.partyrunbattleservice.domain.record.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.record.service.RunnerRecordService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordWebSocketController {

    RunnerRecordService runnerRecordService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/battle/{battleId}/record")
    public void calculateDistance(@DestinationVariable String battleId, Authentication auth, @Valid RecordRequest request) {
        final String runnerId = auth.getName();
        RunnerDistanceResponse response = runnerRecordService.calculateDistance(battleId, runnerId, request);

        messagingTemplate.convertAndSend("/topic/battle/" + battleId, response);
    }
}
