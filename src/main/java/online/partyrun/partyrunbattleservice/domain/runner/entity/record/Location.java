package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    GeoJsonPoint point;
    double altitude;
}
