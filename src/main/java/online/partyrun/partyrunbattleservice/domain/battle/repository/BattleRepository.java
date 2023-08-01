package online.partyrun.partyrunbattleservice.domain.battle.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface BattleRepository extends MongoRepository<Battle, String> {
    List<Battle> findByStatusInAndRunnersIn(List<BattleStatus> statuses, List<Runner> runners);

    Optional<Battle> findByStatusAndRunnersId(BattleStatus status, String runnersId);

    boolean existsByIdAndRunnersIdAndStatus(String battleId, String runnerId, BattleStatus status);

    @Query(value = "{ 'id': ?0, 'runners.id': ?1 }", fields = "{'runners.runnerRecords': 0}")
    Optional<Battle> findBattleExceptRunnerRecords(String battleId, String runnerId);

    @Query(value = "{'id': ?0, 'runners.id': ?1}")
    @Update(
            "{'$push': {'runners.$.runnerRecords': {'$each': ?2}}, '$set' :  {'runners.$.status': "
                + " ?3}}")
    void addRunnerRecordsAndUpdateRunnerStatus(
            String battleId,
            String runnerId,
            List<RunnerRecord> runnerRecords,
            RunnerStatus runnerStatus);
}
