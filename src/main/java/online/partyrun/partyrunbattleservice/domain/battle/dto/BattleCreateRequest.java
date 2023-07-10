package online.partyrun.partyrunbattleservice.domain.battle.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BattleCreateRequest {

    int distance;
    @NotNull List<String> runnerIds;
}
