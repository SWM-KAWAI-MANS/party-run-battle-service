package online.partyrun.partyrunbattleservice.domain.single.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleIdResponse;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleRunnerRecordsRequest;
import online.partyrun.partyrunbattleservice.domain.single.entity.RunningTime;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;
import online.partyrun.partyrunbattleservice.domain.single.repository.SingleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SingleService {
    SingleRepository singleRepository;

    public SingleIdResponse create(String runnerId, SingleRunnerRecordsRequest request) {
        final RunningTime runningTime = request.getRunningTime();
        final List<RunnerRecord> records = request.runnerRecords();
        final Single newSingleRecord = singleRepository.save(new Single(runnerId, runningTime, records));
        return new SingleIdResponse(newSingleRecord.getId());
    }
}
