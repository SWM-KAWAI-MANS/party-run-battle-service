package online.partyrun.partyrunbattleservice.domain.battle.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BattleRepository extends MongoRepository<Battle, String> {
    List<Battle> findByStatusAndRunnersIn(BattleStatus status, List<Runner> runners);

    Optional<Battle> findByStatusAndRunnersId(BattleStatus status, String runnersId);
}
