package online.partyrun.partyrunbattleservice.fixture.record;

import online.partyrun.partyrunbattleservice.domain.record.dto.GpsRequest;
import online.partyrun.partyrunbattleservice.domain.record.dto.RecordRequest;

import java.time.LocalDateTime;
import java.util.List;

public class RequestFixture {
    private static final LocalDateTime now = LocalDateTime.now();
    public static final GpsRequest GPS_REQUEST1 = new GpsRequest(1, 1, 1, now);
    public static final GpsRequest GPS_REQUEST2 = new GpsRequest(2, 2, 2, now.plusSeconds(1));
    public static final GpsRequest GPS_REQUEST3 = new GpsRequest(3, 3, 3, now.plusSeconds(2));
    public static final GpsRequest GPS_REQUEST4 = new GpsRequest(1,1,1 , now.plusSeconds(3));
    public static final GpsRequest GPS_REQUEST5 = new GpsRequest(2,2,2, now.plusSeconds(4));
    public static final GpsRequest GPS_REQUEST6 = new GpsRequest(3,3,3, now.plusSeconds(5));
    public static final RecordRequest RECORD_REQUEST1 = new RecordRequest(List.of(GPS_REQUEST1, GPS_REQUEST2, GPS_REQUEST3));
    public static final RecordRequest RECORD_REQUEST2 = new RecordRequest(List.of(GPS_REQUEST4, GPS_REQUEST5, GPS_REQUEST6));
}
