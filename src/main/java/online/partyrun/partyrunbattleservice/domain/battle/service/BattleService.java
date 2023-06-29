package online.partyrun.partyrunbattleservice.domain.battle.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleMapper;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunningBattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.service.RunnerService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleService {

    BattleMapper battleMapper;
    BattleRepository battleRepository;
    RunnerService runnerService;

    public BattleResponse createBattle(BattleCreateRequest request) {
        final List<String> runnerIds = request.getRunnerIds();
        final List<Runner> runners = runnerService.findAllById(runnerIds);

        validateRunnerInRunningBattle(runners);

        final Battle battle = battleRepository.save(new Battle(runners));
        return battleMapper.toResponse(battle);
    }

    private void validateRunnerInRunningBattle(List<Runner> runners) {
        final List<Battle> runningBattle =
                battleRepository.findByStatusAndRunnersIn(BattleStatus.RUNNING, runners);
        if (!runningBattle.isEmpty()) {
            throw new RunnerAlreadyRunningInBattleException(runners, runningBattle);
        }
    }

    public BattleResponse getRunningBattle(String runnerId) {
        final Battle battle = battleRepository.findByStatusAndRunnersId(BattleStatus.RUNNING, runnerId)
                .orElseThrow(() -> new RunningBattleNotFoundException(runnerId));

        return battleMapper.toResponse(battle);
    }
}
