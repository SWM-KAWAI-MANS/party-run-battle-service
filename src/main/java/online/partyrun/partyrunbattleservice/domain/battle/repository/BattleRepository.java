package online.partyrun.partyrunbattleservice.domain.battle.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface BattleRepository extends MongoRepository<Battle, String> {
    boolean existsByIdAndRunnersIdAndRunnersStatus(
            String battleId, String runnerId, RunnerStatus status);

    boolean existsByRunnersAndRunnersStatusIn(String runnerId, List<RunnerStatus> ready);

    boolean existsByRunnersIdInAndRunnersStatusIn(List<String> runnersId, List<RunnerStatus> ready);

    Optional<Battle> findByRunnersIdAndRunnersStatus(String runnerId, RunnerStatus runnerStatus);

    @Query(value = "{ 'id': ?0, 'runners.id': ?1 }", fields = "{'runners.runnerRecords': 0}")
    Optional<Battle> findBattleExceptRunnerRecords(String battleId, String runnerId);

    @Query(value = "{'id': ?0, 'runners.id': ?1}")
    @Update(
            "{'$push': {'runners.$.runnerRecords': {'$each': ?2}}, "
                    + "'$set' :  {'runners.$.recentRunnerRecord': ?3, 'runners.$.status': ?4}}")
    void addRunnerRecordsAndUpdateRunnerStatus(
            String battleId,
            String runnerId,
            List<RunnerRecord> runnerRecords,
            RunnerRecord recentRecord,
            RunnerStatus runnerStatus);

    @Query(value = "{'runners.id': ?0, 'runners.status': { $in: ?1 }}")
    @Update("{'$set' :  {'runners.$.status': ?2}}")
    void updateReadyOrRunningRunnerStatus(
            String runnerId, List<RunnerStatus> preRunnerStatus, RunnerStatus runnerStatus);
}
