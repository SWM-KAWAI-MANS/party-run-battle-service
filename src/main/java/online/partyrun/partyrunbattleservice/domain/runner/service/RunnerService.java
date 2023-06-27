package online.partyrun.partyrunbattleservice.domain.runner.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;

import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRunnerException;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerService {

    RunnerRepository runnerRepository;

    public List<Runner> findAllById(List<String> runnerIds) {
        final List<Runner> runners = runnerRepository.findAllById(runnerIds);
        validateRunners(runners, runnerIds);
        return runners;
    }

    private void validateRunners(List<Runner> runners, List<String> runnerIds) {
        if (runners.size() != runnerIds.size()) {
            throw new InvalidRunnerException(runnerIds);
        }
    }
}
