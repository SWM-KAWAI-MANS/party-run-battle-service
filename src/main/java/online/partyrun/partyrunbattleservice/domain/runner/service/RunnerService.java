package online.partyrun.partyrunbattleservice.domain.runner.service;

import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunnerService {

    public List<Runner> findByIds(List<String> runnerIds) {
        return runnerIds.stream().map(id -> new Runner(id, id + "님")).toList();
    }
}
