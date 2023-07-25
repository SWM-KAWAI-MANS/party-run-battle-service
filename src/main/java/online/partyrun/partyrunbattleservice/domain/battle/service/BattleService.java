package online.partyrun.partyrunbattleservice.domain.battle.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyBattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleDao;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.service.RunnerService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleService {

    BattleMapper battleMapper;
    BattleRepository battleRepository;
    BattleDao battleDao;
    RunnerService runnerService;
    ApplicationEventPublisher eventPublisher;
    Clock clock;

    public BattleResponse createBattle(BattleCreateRequest request) {
        final List<String> runnerIds = request.getRunnerIds();
        final List<Runner> runners = runnerService.findAllById(runnerIds);

        validateRunnerInBattle(runners);

        final Battle battle = battleRepository.save(new Battle(request.getDistance(), runners));
        return battleMapper.toResponse(battle);
    }

    private void validateRunnerInBattle(List<Runner> runners) {
        final List<Battle> runningBattle =
                battleRepository.findByStatusInAndRunnersIn(
                        List.of(BattleStatus.READY, BattleStatus.RUNNING), runners);
        if (!runningBattle.isEmpty()) {
            throw new RunnerAlreadyRunningInBattleException(runners, runningBattle);
        }
    }

    public BattleResponse getReadyBattle(String runnerId) {
        final Battle battle =
                battleRepository
                        .findByStatusAndRunnersId(BattleStatus.READY, runnerId)
                        .orElseThrow(() -> new ReadyBattleNotFoundException(runnerId));

        return battleMapper.toResponse(battle);
    }

    public void setRunnerRunning(String battleId, String runnerId) {
        final Battle battle = findBattle(battleId);
        battle.changeRunnerStatus(runnerId, RunnerStatus.RUNNING);
        final Battle updatedBattle =
                battleDao.updateRunnerStatus(battleId, runnerId, battle.getRunnerStatus(runnerId));

        publishBattleRunningEvent(updatedBattle);
    }

    private Battle findBattle(String battleId) {
        return battleRepository
                .findById(battleId)
                .orElseThrow(() -> new BattleNotFoundException(battleId));
    }

    private void publishBattleRunningEvent(Battle battle) {
        if (battle.isAllRunnersRunningStatus()) {
            eventPublisher.publishEvent(new BattleRunningEvent(battle.getId()));
        }
    }

    public BattleStartTimeResponse setBattleRunning(String battleId) {
        final Battle battle = findBattle(battleId);
        battle.changeBattleStatus(BattleStatus.RUNNING);

        final LocalDateTime now = LocalDateTime.now(clock);
        final LocalDateTime startTime = now.plusSeconds(5);
        battle.setStartTime(now, startTime);
        battleRepository.save(battle);

        return new BattleStartTimeResponse(startTime);
    }

    public RunnerDistanceResponse calculateDistance(
            String battleId, String runnerId, RecordRequest request) {
        final Battle battle = findBattleExceptRunnerRecords(battleId, runnerId);

        final List<GpsData> gpsData = createNewGpsData(request);
        battle.createNewRecords(gpsData);
        battleDao.pushNewRecords(battle);

        // TODO: 2023/07/21 현재는 종료 로직이 들어가지 않았으므로 무조건 isFinished에 false 적용
        return new RunnerDistanceResponse(runnerId, false, battle.getRunnerDistance(runnerId));
    }

    private Battle findBattleExceptRunnerRecords(String battleId, String runnerId) {
        return battleRepository
                .findBattleExceptRunnerRecords(battleId, runnerId)
                .orElseThrow(() -> new BattleNotFoundException(battleId, runnerId));
    }

    private List<GpsData> createNewGpsData(RecordRequest request) {
        return request.getRecord().stream().map(GpsRequest::toEntity).toList();
    }
}
