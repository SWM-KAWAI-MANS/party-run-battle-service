package online.partyrun.partyrunbattleservice.domain.record.service;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.record.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

import static online.partyrun.partyrunbattleservice.fixture.Battle.BattleFixture.*;
import static online.partyrun.partyrunbattleservice.fixture.record.RequestFixture.RECORD_REQUEST1;
import static online.partyrun.partyrunbattleservice.fixture.record.RequestFixture.RECORD_REQUEST2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@SpringBootTest
class RunnerRecordServiceTest {

    @Autowired
    RunnerRecordService recordService;

    @Autowired
    MongoTemplate mongoTemplate;

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
                List<RunnerRecord> results = mongoTemplate.find(Query.query(where("battleId").is(battleId)), RunnerRecord.class);

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

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 거리를_계산할_때 {
        Runner 박성우 = new Runner("박성우");
        Battle 진행중인_배틀;

        @BeforeEach
        void setUp() {
            Battle 배틀 = new Battle(1000, List.of(박성우));
            배틀.changeRunnerStatus(박성우.getId(), RunnerStatus.RUNNING);
            배틀.changeBattleStatus(BattleStatus.RUNNING);
            배틀.setStartTime(LocalDateTime.now().minusMinutes(5), LocalDateTime.now().minusMinutes(1));

            진행중인_배틀 = mongoTemplate.save(배틀);
            mongoTemplate.save(new RunnerRecord(진행중인_배틀.getId(), 박성우.getId()));
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 기존에_기록이_존재하지_않으면 {

            @Test
            @DisplayName("새로운 기록을 저장한다")
            void createNewRecord() {
                RunnerDistanceResponse response = recordService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST1);
                assertThat(response.getDistance()).isPositive();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 기존에_기록이_존재하면 {

            @Test
            @DisplayName("기존 기록에 이어서 저장한다")
            void createNewRecord() {
                RunnerDistanceResponse response1 = recordService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST1);
                RunnerDistanceResponse response2 = recordService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST2);
                assertThat(response2.getDistance()).isGreaterThan(response1.getDistance());
            }
        }
    }
}
