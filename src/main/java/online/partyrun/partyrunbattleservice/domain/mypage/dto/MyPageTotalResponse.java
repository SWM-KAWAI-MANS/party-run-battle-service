package online.partyrun.partyrunbattleservice.domain.mypage.dto;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRecentRunnerRecordException;
import online.partyrun.partyrunbattleservice.domain.single.dto.RunningTimeResponse;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public record MyPageTotalResponse(double totalDistance, double averagePace, RunningTimeResponse totalRunningTime) {

    public static MyPageTotalResponse of(String memberId, List<Battle> battles, List<Single> singles) {
        if (battles.isEmpty() && singles.isEmpty()) {
            return new MyPageTotalResponse(0, 0, new RunningTimeResponse(0, 0, 0));
        }

        final double totalBattleDistance = battles.stream()
                .mapToDouble(
                        battle -> {
                            try {
                                return battle.getRunnerRecentDistance(memberId);
                            } catch (InvalidRecentRunnerRecordException exception) {
                                return 0;
                            }
                        }
                )
                .sum();

        final long totalBattleRunningTime = battles.stream()
                .mapToLong(
                        battle -> {
                            final RunnerRecord runnerRecentRecord = battle.getRunnerRecentRecord(memberId);
                            if (Objects.isNull(runnerRecentRecord)) {
                                return 0;
                            }
                            return Duration.between(battle.getCreatedAt(), runnerRecentRecord.getTime()).toSeconds();
                        }
                )
                .sum();

        final double totalSingleDistance = singles.stream()
                .mapToDouble(single -> {
                    final List<RunnerRecord> runnerRecords = single.getRunnerRecords();
                    return runnerRecords.get(runnerRecords.size() - 1).getDistance();
                })
                .sum();

        final int totalSingleRunningTime = singles.stream()
                .mapToInt(single -> single.getRunningTime().getTotalSeconds())
                .sum();


        final double totalDistance = totalBattleDistance + totalSingleDistance;
        final long totalRunningTime = totalBattleRunningTime + totalSingleRunningTime;

        int totalHour = (int) totalRunningTime / 3600;
        int totalMinute = (int) (totalRunningTime % 3600) / 60;
        int totalSecond = (int) totalRunningTime % 60;

        return new MyPageTotalResponse(totalDistance, totalDistance / totalRunningTime, new RunningTimeResponse(totalHour, totalMinute, totalSecond));
    }
}
