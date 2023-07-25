package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerRecord {
    GpsData gpsData;
    double distance;
}
