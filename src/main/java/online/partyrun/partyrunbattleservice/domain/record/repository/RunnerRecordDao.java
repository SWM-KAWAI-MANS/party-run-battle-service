package online.partyrun.partyrunbattleservice.domain.record.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerRecordDao {

    MongoTemplate mongoTemplate;

    public List<String> getRunnerIds(String battleId) {
        Assert.notNull(battleId, "battleId must not be null");

        Aggregation agg =
                Aggregation.newAggregation(
                        match(where("_id").is(battleId)),
                        unwind("runners"),
                        project().andExclude("_id").and("runners.id").as("id"));

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, Battle.class, Document.class);

        return results.getMappedResults().stream()
                .map(document -> document.getString("id"))
                .toList();
    }

    public void saveAll(List<RunnerRecord> records) {
        Assert.notNull(records, "records must not be null");
        Assert.notEmpty(records, "records must not be empty");

        mongoTemplate.insertAll(records);
    }

    public Optional<Record> findLatestRecord(String battleId, String runnerId) {
        Aggregation aggregation =
                Aggregation.newAggregation(
                        RunnerRecord.class,
                        match(where("battleId").is(battleId).and("runnerId").is(runnerId)),
                        unwind("records"),
                        sort(Sort.by(Sort.Direction.DESC, "records.gpsData.time")),
                        limit(1),
                        project()
                                .andExclude("_id")
                                .and("records.gpsData")
                                .as("gpsData")
                                .and("records.distance")
                                .as("distance"));
        AggregationResults<Record> results =
                mongoTemplate.aggregate(aggregation, RunnerRecord.class, Record.class);

        return Optional.ofNullable(results.getUniqueMappedResult());
    }

    public void pushNewRecords(String battleId, String runnerId, List<Record> records) {
        Query query = Query.query(where("battleId").is(battleId).and("runnerId").is(runnerId));
        Update update = new Update().push("records").each(records.toArray());

        mongoTemplate.updateFirst(query, update, RunnerRecord.class);
    }
}
