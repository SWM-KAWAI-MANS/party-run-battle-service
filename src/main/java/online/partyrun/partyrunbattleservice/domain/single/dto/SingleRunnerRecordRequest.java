package online.partyrun.partyrunbattleservice.domain.single.dto;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;

import java.time.LocalDateTime;

public record SingleRunnerRecordRequest(double longitude, double latitude, double altitude, LocalDateTime time, double distance) {
    public RunnerRecord toRunnerRecord() {
        final GpsData gpsData = GpsData.of(longitude, latitude, altitude, time);
        return new RunnerRecord(gpsData, distance);
    }
}
