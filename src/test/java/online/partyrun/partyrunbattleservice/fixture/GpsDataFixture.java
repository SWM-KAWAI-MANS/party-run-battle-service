package online.partyrun.partyrunbattleservice.fixture;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;

import static online.partyrun.partyrunbattleservice.fixture.LocalDateTimeFixture.now;

public class GpsDataFixture {
    public static final GpsData GPSDATA0 = GpsData.of(0, 0, 0, now.plusSeconds(1));
    public static final GpsData GPSDATA1 = GpsData.of(0.00001, 0.00001, 0, now.plusSeconds(2));
    public static final GpsData GPSDATA2 = GpsData.of(0.00002, 0.00002, 0, now.plusSeconds(3));
    public static final GpsData GPSDATA3 = GpsData.of(0.00003, 0.00003, 0, now.plusSeconds(4));
    public static final GpsData GPSDATA4 = GpsData.of(0.00004, 0.00004, 0, now.plusSeconds(5));
}
