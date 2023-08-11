package online.partyrun.partyrunbattleservice.domain.runner.dto;

import java.time.LocalDateTime;

public record RunnerResponse(String id, int rank, LocalDateTime endTime) {}
