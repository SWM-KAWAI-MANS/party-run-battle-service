package online.partyrun.partyrunbattleservice.domain.mypage.dto;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.dto.RunningTimeResponse;
import online.partyrun.partyrunbattleservice.domain.single.entity.RunningTime;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record MyPageRunningResponse(String id,
                                    LocalDateTime startTime,
                                    RunningTimeResponse runningTime,
                                    double distance) {

    public static MyPageRunningResponse of(Battle battle, String memberId) {
        final RunnerRecord runnerRecentRecord = battle.getRunnerRecentRecord(memberId);
        final LocalDateTime startTime = battle.getStartTime();

        if (Objects.isNull(runnerRecentRecord)) {
            return new MyPageRunningResponse(
                    battle.getId(),
                    startTime,
                    new RunningTimeResponse(0, 0, 0),
                    0
            );
        }

        final LocalDateTime endTime = runnerRecentRecord.getTime();
        final Duration duration = Duration.between(startTime, endTime);

        return new MyPageRunningResponse(
                battle.getId(),
                startTime,
                new RunningTimeResponse(duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()),
                runnerRecentRecord.getDistance()
        );
    }

    public static MyPageRunningResponse from(Single single) {
        final RunningTime runningTime = single.getRunningTime();

        final List<RunnerRecord> runnerRecords = single.getRunnerRecords();

        return new MyPageRunningResponse(
                single.getId(),
                runnerRecords.get(0).getTime(),
                new RunningTimeResponse(runningTime.getHours(), runningTime.getMinutes(), runningTime.getSeconds()),
                runnerRecords.get(runnerRecords.size() - 1).getDistance()
        );
    }
}
