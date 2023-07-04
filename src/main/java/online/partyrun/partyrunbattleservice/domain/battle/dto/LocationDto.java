package online.partyrun.partyrunbattleservice.domain.battle.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class LocationDto {
    int x;
    int y;
}
