package online.partyrun.partyrunbattleservice.domain.record.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import online.partyrun.partyrunbattleservice.domain.record.dto.RecordRequest;
import online.partyrun.partyrunbattleservice.domain.record.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.record.entity.GpsData;
import online.partyrun.partyrunbattleservice.domain.record.entity.GpsDatas;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.record.repository.RunnerRecordDao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerRecordService {

    BattleService battleService;
    RunnerRecordDao runnerRecordDao;

    public void createBattleRecord(String battleId) {
        final List<String> runnerIds = runnerRecordDao.getRunnerIds(battleId);
        final List<RunnerRecord> records =
                runnerIds.stream().map(runnerId -> new RunnerRecord(battleId, runnerId)).toList();

        runnerRecordDao.saveAll(records);
    }

    public RunnerDistanceResponse calculateDistance(String battleId, String runnerId, RecordRequest request) {
        final Battle battle = battleService.findRunningRunner(battleId, runnerId);

        final GpsDatas gpsDatas = createNewGpsDatas(request, battle.getStartTime());
        final List<Record> newRecords = createNewRecords(battleId, runnerId, gpsDatas);
        runnerRecordDao.pushNewRecords(battleId, runnerId, newRecords);

        // TODO: 2023/07/21 현재는 종료 로직이 들어가지 않았으므로 무조건 isFinished에 false 적용
        return new RunnerDistanceResponse(runnerId, false, newRecords.get(newRecords.size() - 1).getDistance());
    }

    private GpsDatas createNewGpsDatas(RecordRequest request, LocalDateTime minTime) {
        final List<GpsData> gpsData =
                request.getRecord().stream()
                        .map(r -> r.toEntity(minTime))
                        .toList();

        return new GpsDatas(gpsData);
    }

    private List<Record> createNewRecords(String battleId, String runnerId, GpsDatas gpsDatas) {
        return runnerRecordDao
                .findLatestRecord(battleId, runnerId)
                .map(gpsDatas::createRecords)
                .orElseGet(gpsDatas::createNewRecords);
    }
}
