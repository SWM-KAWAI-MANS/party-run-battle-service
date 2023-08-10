package online.partyrun.partyrunbattleservice.domain.battle.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerFinishedEvent;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyRunnerNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleDao;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.dto.FinishedRunnerResponse;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.service.RunnerService;
import online.partyrun.partyrunbattleservice.global.annotation.DistributedLock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleService {

    BattleRepository battleRepository;
    BattleDao battleDao;
    RunnerService runnerService;
    ApplicationEventPublisher eventPublisher;
    Clock clock;

    public BattleResponse createBattle(BattleCreateRequest request) {
        final List<String> runnerIds = request.getRunnerIds();
        validateRunningRunner(runnerIds);
        final List<Runner> runners = runnerService.findAllById(runnerIds);

        final Battle battle =
                battleRepository.save(
                        new Battle(request.getDistance(), runners, LocalDateTime.now(clock)));
        return new BattleResponse(battle.getId());
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

        return new BattleResponse(battle.getId());
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

    public BattleStartedResponse start(String battleId) {
        final Battle battle = findBattle(battleId);
        final LocalDateTime now = LocalDateTime.now(clock);
        battle.setStartTime(now);

        battleRepository.save(battle);

        return new BattleStartedResponse(battle.getStartTime());
    }

    @DistributedLock(key = "#battleId.concat('-').concat(#runnerId)")
    public RunnerDistanceResponse calculateDistance(
            String battleId, String runnerId, RunnerRecordRequest request) {
        final Battle battle = findBattleExceptRunnerRecords(battleId, runnerId);

        final List<GpsData> gpsData = createGpsData(request);
        battle.addRecords(runnerId, gpsData);
        battleRepository.addRunnerRecordsAndUpdateRunnerStatus(
                battleId,
                runnerId,
                battle.getRunnerRecords(runnerId),
                battle.getRunnerRecentRecord(runnerId),
                battle.getRunnerStatus(runnerId));

        publishRunnerFinishedEventIfRunnerFinished(battle, runnerId);
        return new RunnerDistanceResponse(runnerId, battle.getRunnerRecentDistance(runnerId));
    }

    private void publishRunnerFinishedEventIfRunnerFinished(Battle battle, String runnerId) {
        if (battle.isRunnerFinished(runnerId)) {
            eventPublisher.publishEvent(new RunnerFinishedEvent(battle.getId(), runnerId));
        }
    }

    private Battle findBattleExceptRunnerRecords(String battleId, String runnerId) {
        return battleRepository
                .findBattleExceptRunnerRecords(battleId, runnerId)
                .orElseThrow(() -> new BattleNotFoundException(battleId, runnerId));
    }

    private List<GpsData> createGpsData(RunnerRecordRequest request) {
        return request.record().stream().map(GpsRequest::toEntity).toList();
    }

    public FinishedBattleResponse getFinishedBattle(String battleId, String runnerId) {
        final Battle battle = findBattleExceptRunnerRecords(battleId, runnerId);
        final List<Runner> runners = battle.getRunners();

        return new FinishedBattleResponse(
                battle.getTargetDistance(),
                battle.getStartTime(),
                toFinishedRunnerResponses(runners));
    }

    private List<FinishedRunnerResponse> toFinishedRunnerResponses(List<Runner> runners) {
        final List<Runner> finishedRunners =
                runners.stream()
                        .filter(Runner::isFinished)
                        .sorted(comparing(Runner::getRecentRunnerRecord))
                        .toList();

        final List<FinishedRunnerResponse> result = new ArrayList<>();

        int rank = 1;
        for (Runner runner : finishedRunners) {
            FinishedRunnerResponse response = toFinishedRunnerResponse(runner, rank++);
            result.add(response);
        }

        return result;
    }

    private FinishedRunnerResponse toFinishedRunnerResponse(Runner runner, int rank) {
        return new FinishedRunnerResponse(runner.getId(), rank, runner.getEndTime());
    }

    public MessageResponse changeRunnerFinished(String runnerId) {
        battleRepository.updateReadyOrRunningRunnerStatus(
                runnerId, List.of(RunnerStatus.READY, RunnerStatus.RUNNING), RunnerStatus.FINISHED);

        return new MessageResponse("요청이 정상적으로 처리되었습니다.");
    }
}
