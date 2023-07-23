package online.partyrun.partyrunbattleservice.domain.record.service;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static online.partyrun.partyrunbattleservice.fixture.Battle.BattleFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class RunnerRecordServiceTest {

    @Autowired RunnerRecordService recordService;

    @Autowired MongoTemplate mongoTemplate;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_기록들을_생성할_때 {

        Battle 배틀 = mongoTemplate.save(배틀1);

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_id를_받으면 {

            String battleId = 배틀.getId();

            @Test
            @DisplayName("RunnerRecord들을 생성한다.")
            void createBattleRecord() {
                recordService.createBattleRecord(battleId);
                final List<RunnerRecord> results = mongoTemplate.findAll(RunnerRecord.class);

                assertAll(
                        () ->
                                assertThat(
                                                results.stream()
                                                        .map(RunnerRecord::getBattleId)
                                                        .allMatch(id -> id.equals(battleId)))
                                        .isTrue(),
                        () ->
                                assertThat(results.stream().map(RunnerRecord::getRunnerId).toList())
                                        .containsExactlyInAnyOrder(
                                                박성우.getId(), 박현준.getId(), 노준혁.getId()));
            }
        }
    }
}
