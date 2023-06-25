package online.partyrun.partyrunbattleservice.domain.battle.dto;

import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerResponse;

import java.util.List;

public record BattleResponse(String id, List<RunnerResponse> runners) {
}
