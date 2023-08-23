package online.partyrun.partyrunbattleservice.domain.runner.dto;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;

import java.time.LocalDateTime;

public record RunnerRecordResponse(double longitude, double latitude, double altitude, LocalDateTime time, double distance) {

    public RunnerRecordResponse(RunnerRecord runnerRecord) {
        this(runnerRecord.getGpsData().getLocation().getPoint().getX(),
                runnerRecord.getGpsData().getLocation().getPoint().getY(),
                runnerRecord.getGpsData().getLocation().getAltitude(),
                runnerRecord.getGpsData().getTime(),
                runnerRecord.getDistance()
        );
    }
}
