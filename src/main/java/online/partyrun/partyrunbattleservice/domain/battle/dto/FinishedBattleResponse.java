package online.partyrun.partyrunbattleservice.domain.battle.dto;

import online.partyrun.partyrunbattleservice.domain.runner.dto.FinishedRunnerResponse;

import java.time.LocalDateTime;
import java.util.List;

public record FinishedBattleResponse(
        double targetDistance, LocalDateTime startTime, List<FinishedRunnerResponse> runners) {}
