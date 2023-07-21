package online.partyrun.partyrunbattleservice.domain.record.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.record.repository.RunnerRecordDao;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerRecordService {

    RunnerRecordDao runnerRecordDao;

    public void createBattleRecord(String battleId) {
        final List<String> runnerIds = runnerRecordDao.getRunnerIds(battleId);
        final List<RunnerRecord> records =
                runnerIds.stream().map(runnerId -> new RunnerRecord(battleId, runnerId)).toList();

        runnerRecordDao.saveAll(records);
    }
}
