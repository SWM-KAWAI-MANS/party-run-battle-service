package online.partyrun.partyrunbattleservice.domain.battle.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyRunnerNotFoundException;
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
        validateRunningRunner(runnerIds);
        final List<Runner> runners = runnerService.findAllById(runnerIds);

        final Battle battle = battleRepository.save(new Battle(request.getDistance(), runners));
        return battleMapper.toResponse(battle);
    }

    private void validateRunningRunner(List<String> runnerIds) {
        if (battleRepository.existsByRunnersIdInAndRunnersStatusIn(
                runnerIds, List.of(RunnerStatus.READY, RunnerStatus.RUNNING))) {
            throw new RunnerAlreadyRunningInBattleException(runnerIds);
        }
    }

    public BattleResponse getReadyBattle(String runnerId) {
        final Battle battle =
                battleRepository
                        .findByRunnersIdAndRunnersStatus(runnerId, RunnerStatus.READY)
                        .orElseThrow(() -> new ReadyRunnerNotFoundException(runnerId));

        return battleMapper.toResponse(battle);
    }

    public void setRunnerRunning(String battleId, String runnerId) {
        final Battle battle = findBattle(battleId);
        battle.changeRunnerRunningStatus(runnerId);
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

    public BattleStartTimeResponse startBattle(String battleId) {
        final Battle battle = findBattle(battleId);
        final LocalDateTime now = LocalDateTime.now(clock);
        battle.setStartTime(now);

        battleRepository.save(battle);

        return new BattleStartTimeResponse(battle.getStartTime());
    }

    public RunnerDistanceResponse calculateDistance(
            String battleId, String runnerId, RunnerRecordRequest request) {
        final Battle battle = findBattleExceptRunnerRecords(battleId, runnerId);

        final List<GpsData> gpsData = createGpsData(request);
        battle.addRecords(runnerId, gpsData);
        battleRepository.addRunnerRecordsAndUpdateRunnerStatus(
                battleId,
                runnerId,
                battle.getRunnerRecords(runnerId),
                battle.getRunnerStatus(runnerId));

        return new RunnerDistanceResponse(
                runnerId,
                battle.isRunnerFinished(runnerId),
                battle.getRunnerRecentDistance(runnerId));
    }

    private Battle findBattleExceptRunnerRecords(String battleId, String runnerId) {
        return battleRepository
                .findBattleExceptRunnerRecords(battleId, runnerId)
                .orElseThrow(() -> new BattleNotFoundException(battleId, runnerId));
    }

    private List<GpsData> createGpsData(RunnerRecordRequest request) {
        return request.record().stream().map(GpsRequest::toEntity).toList();
    }
}
