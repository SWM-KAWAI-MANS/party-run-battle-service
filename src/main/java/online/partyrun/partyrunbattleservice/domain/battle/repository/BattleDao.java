package online.partyrun.partyrunbattleservice.domain.battle.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleDao {

    MongoTemplate mongoTemplate;

    public Battle updateRunnerStatus(String battleId, String runnerId, RunnerStatus runnerStatus) {
        Query query = Query.query(Criteria.where("id").is(battleId).and("runners.id").is(runnerId));
        Update update = new Update().set("runners.$.status", runnerStatus);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);

        return mongoTemplate.findAndModify(query, update, options, Battle.class);
    }

    public void pushNewRecords(Battle battle) {
        throw new UnsupportedOperationException();
    }
}
