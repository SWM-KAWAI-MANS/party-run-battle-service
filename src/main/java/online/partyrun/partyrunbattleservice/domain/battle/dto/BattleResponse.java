package online.partyrun.partyrunbattleservice.domain.battle.dto;

import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerResponse;

import java.time.LocalDateTime;
import java.util.List;

public record BattleResponse(
        double targetDistance, LocalDateTime startTime, List<RunnerResponse> runners) {}
