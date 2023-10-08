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

    @Query(value = "{'runners': {'$elemMatch': {'_id': {'$in': ?0}, 'status': {'$in': ?1}}}}", fields = "{'runners.runnerRecords': 0}")
    List<Battle> findByRunnersIdInAndRunnersStatusIn(List<String> runnersId, List<RunnerStatus> runnerStatuses);

    default boolean existsByRunnersIdInAndRunnersStatusIn(List<String> runnersId, List<RunnerStatus> runnerStatuses) {
        return !findByRunnersIdInAndRunnersStatusIn(runnersId, runnerStatuses).isEmpty();
    }
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
    @Query(value = "{'runners': {'$elemMatch': {'_id': ?0, 'status': {'$in': ?1}}}}", fields = "{'runners.runnerRecords': 0}")
    Optional<Battle> findBattleByRunnerStatus(String runnerId, List<RunnerStatus> preRunnerStatus);

    Optional<Battle> findByIdAndRunnersId(String battleId, String runnerId);

    @Query(value = "{ 'runners.id': ?0 }", fields = "{'runners.runnerRecords': 0}")
    List<Battle> findAllByRunnersIdExceptRunnerRecords(String runnerId);
}
