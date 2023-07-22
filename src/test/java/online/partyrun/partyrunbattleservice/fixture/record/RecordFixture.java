package online.partyrun.partyrunbattleservice.fixture.record;

import online.partyrun.partyrunbattleservice.domain.record.entity.GpsData;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;

import java.time.LocalDateTime;

public class RecordFixture {
    private static final LocalDateTime startTime = LocalDateTime.now();
    private static final GpsData GPSDATA_1 = GpsData.of(1,1,1, startTime.plusSeconds(1), startTime);
    private static final GpsData GPSDATA_2 = GpsData.of(2,2,2, startTime.plusSeconds(2), startTime);
    private static final GpsData GPSDATA_3 = GpsData.of(3,3,3, startTime.plusSeconds(3), startTime);
    public static final Record RECORD_1 = new Record(GPSDATA_1, 1000);
    public static final Record RECORD_2 = new Record(GPSDATA_2, 1000);
    public static final Record RECORD_3 = new Record(GPSDATA_3, 1000);
    public static final RunnerRecord 박성우_기록 = new RunnerRecord("배틀", "박성우");
    public static final RunnerRecord 박현준_기록 = new RunnerRecord("배틀", "박현준");
    public static final RunnerRecord 노준혁_기록 = new RunnerRecord("배틀", "노준혁");


}
