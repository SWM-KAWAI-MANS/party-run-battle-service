package online.partyrun.partyrunbattleservice.domain.runner.dto;

import java.time.LocalDateTime;

public record FinishedRunnerResponse(String id, int rank, LocalDateTime endTime) {}
