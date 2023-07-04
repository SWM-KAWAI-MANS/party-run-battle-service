package online.partyrun.partyrunbattleservice.domain.runner.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRunnerException;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerService {

    RunnerRepository runnerRepository;

    // TODO: 2023/07/04 MemberRepository에서 가져와서 runner 생성하는 로직으로 변경하기

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
