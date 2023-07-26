package online.partyrun.partyrunbattleservice.domain.battle.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunnerRecordRequest(@NotNull List<GpsRequest> record) {
}
