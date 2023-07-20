package online.partyrun.partyrunbattleservice.domain.record.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerRecordDao {

    MongoTemplate mongoTemplate;

    public List<String> getRunnerIds(String battleId) {
        Aggregation agg =
                Aggregation.newAggregation(
                        match(where("_id").is(battleId)),
                        unwind("runners"),
                        project().andExclude("_id").and("runners.id").as("id"));

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, Battle.class, Document.class);

        return results.getMappedResults().stream()
                .map(document -> document.getString("id"))
                .collect(Collectors.toList());
    }

    public void saveAll(List<RunnerRecord> records) {
        mongoTemplate.insertAll(records);
    }
}
