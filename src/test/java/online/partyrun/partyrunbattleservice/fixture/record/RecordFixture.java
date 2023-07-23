package online.partyrun.partyrunbattleservice.fixture.record;

import online.partyrun.partyrunbattleservice.domain.record.entity.*;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;

import java.time.LocalDateTime;
import java.util.List;

public class RecordFixture {
    private static final LocalDateTime startTime = LocalDateTime.now();
    public static final Location LOCATION1 = Location.of(1, 1, 1);
    public static final Location LOCATION2 = Location.of(2, 2, 2);
    public static final GpsData GPSDATA_0 = GpsData.of(1, 1, 1, startTime, startTime);
    public static final GpsData GPSDATA_1 =
            GpsData.of(1, 1, 1, startTime.plusSeconds(1), startTime);
    public static final GpsData GPSDATA_2 =
            GpsData.of(2, 2, 2, startTime.plusSeconds(2), startTime);
    public static final GpsData GPSDATA_3 =
            GpsData.of(3, 3, 3, startTime.plusSeconds(3), startTime);
    public static final GpsDatas GPSDATAS_1 =
            new GpsDatas(List.of(GPSDATA_1, GPSDATA_2, GPSDATA_3));
    public static final Record RECORD_0 = new Record(GPSDATA_0, 0);
    public static final Record RECORD_1 = new Record(GPSDATA_1, 1);
    public static final Record RECORD_2 = new Record(GPSDATA_2, 2);
    public static final Record RECORD_3 = new Record(GPSDATA_3, 3);
    public static final RunnerRecord 박성우_기록 = new RunnerRecord("배틀", "박성우");
    public static final RunnerRecord 박현준_기록 = new RunnerRecord("배틀", "박현준");
    public static final RunnerRecord 노준혁_기록 = new RunnerRecord("배틀", "노준혁");
}
