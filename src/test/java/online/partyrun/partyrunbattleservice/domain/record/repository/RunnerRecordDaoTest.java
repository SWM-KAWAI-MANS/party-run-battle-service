package online.partyrun.partyrunbattleservice.domain.record.repository;

import static online.partyrun.partyrunbattleservice.fixture.Battle.BattleFixture.*;
import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.*;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import online.partyrun.partyrunbattleservice.domain.record.entity.Record;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DataMongoTest
@DisplayName("RunnerRecordDao")
class RunnerRecordDaoTest {

    MongoTemplate mongoTemplate;
    RunnerRecordDao runnerRecordDao;

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Autowired
    public RunnerRecordDaoTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.runnerRecordDao = new RunnerRecordDao(mongoTemplate);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getRunnerIds는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하는_battleId를_받으면 {

            @Test
            @DisplayName("배틀에 속한 러너들의 id를 반환한다.")
            void returnRunnerIds() {
                String battleId = mongoTemplate.save(배틀1).getId();
                List<String> result = runnerRecordDao.getRunnerIds(battleId);

                assertThat(result).containsExactly(박성우.getId(), 박현준.getId(), 노준혁.getId());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하지_않는_battleId를_받으면 {
            String invalidBattleId = "invalid battle id";

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void returnEmpty() {
                List<String> result = runnerRecordDao.getRunnerIds(invalidBattleId);

                assertThat(result).isEmpty();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class battleId를_받지않으면 {
            String invalidBattleId = null;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> runnerRecordDao.getRunnerIds(invalidBattleId))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class saveAll_은 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RunnerRecord_들을_받으면 {
            List<RunnerRecord> 러너_기록들 = List.of(박성우_기록, 박현준_기록, 노준혁_기록);

            @Test
            @DisplayName("DB에 저장한다")
            void saveAll() {
                runnerRecordDao.saveAll(러너_기록들);

                List<RunnerRecord> results = mongoTemplate.findAll(RunnerRecord.class);
                assertThat(results).usingRecursiveComparison().isEqualTo(러너_기록들);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 빈_RunnerRecord_를_받으면 {
            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("예외를 던진다.")
            void throwExceptions(List<RunnerRecord> records) {
                assertThatThrownBy(() -> runnerRecordDao.saveAll(records))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class RunnerRecord가_존재할_떄 {
        String battleId = 박성우_기록.getBattleId();
        String runnerId = 박성우_기록.getRunnerId();

        @BeforeEach
        void setUp() {
            mongoTemplate.save(박성우_기록);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class pushNewRecords는 {
            List<Record> records = List.of(RECORD_1, RECORD_2, RECORD_3);

            @Test
            @DisplayName("새로운 Record를 삽입한다.")
            void pushRecord() {
                runnerRecordDao.pushNewRecords(battleId, runnerId, records);

                Query query =
                        Query.query(where("battleId").is(battleId).and("runnerId").is(runnerId));
                final RunnerRecord result = mongoTemplate.findOne(query, RunnerRecord.class);

                assertThat(result.getRecords()).hasSize(3);
            }

            @ParameterizedTest
            @MethodSource("invalidParameters")
            @DisplayName("잘못된 파라미터가 들어오면 예외를 던진다.")
            void throwException(String battleId, String runnerId, List<Record> records) {
                Assertions.assertThatThrownBy(
                                () -> runnerRecordDao.pushNewRecords(battleId, runnerId, records))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            public static Stream<Arguments> invalidParameters() {
                return Stream.of(
                        Arguments.of(null, "runnerId", List.of(RECORD_1)),
                        Arguments.of("battleId", null, List.of(RECORD_1)),
                        Arguments.of("battleId", "runnerId", null),
                        Arguments.of("battleId", "runnerId", List.of()));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class findLatestRecord는 {

            @Test
            @DisplayName("가장 최근의 기록을 조회한다.")
            void findLatestRecord() {
                runnerRecordDao.pushNewRecords(
                        battleId, runnerId, List.of(RECORD_2, RECORD_3, RECORD_1));

                Record record = runnerRecordDao.findLatestRecord(battleId, runnerId).orElseThrow();

                assertThat(record.getGpsData().getTime())
                        .isCloseTo(RECORD_3.getGpsData().getTime(), within(1, ChronoUnit.MILLIS));
            }

            @Test
            @DisplayName("가장 최근의 기록이 없다면 empty를 반환한다.")
            void returnEmpty() {
                final Optional<Record> latestRecord =
                        runnerRecordDao.findLatestRecord(battleId, runnerId);
                assertThat(latestRecord).isEmpty();
            }

            @ParameterizedTest
            @MethodSource("invalidParameters")
            @DisplayName("잘못된 파라미터가 입력되면 예외를 던진다.")
            void throwException(String battleId, String runnerId) {
                Assertions.assertThatThrownBy(
                                () -> runnerRecordDao.findLatestRecord(battleId, runnerId))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            public static Stream<Arguments> invalidParameters() {
                return Stream.of(Arguments.of(null, "runnerId"), Arguments.of("battleId", null));
            }
        }
    }
}
