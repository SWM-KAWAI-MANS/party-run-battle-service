package online.partyrun.partyrunbattleservice.domain.runner.repository;

import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RunnerRepository extends MongoRepository<Runner, String> {}
