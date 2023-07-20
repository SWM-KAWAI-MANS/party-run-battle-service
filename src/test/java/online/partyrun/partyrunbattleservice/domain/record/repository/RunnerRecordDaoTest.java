package online.partyrun.partyrunbattleservice.domain.record.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@DisplayName("RunnerRecordDao")
class RunnerRecordDaoTest {

    MongoTemplate mongoTemplate;
    RunnerRecordDao runnerRecordDao;

    @Autowired
    public RunnerRecordDaoTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.runnerRecordDao = new RunnerRecordDao(mongoTemplate);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getRunnerIds는 {
        Runner 박성우 = new Runner("박성우");
        Runner 박현준 = new Runner("박현준");
        Runner 노준혁 = new Runner("노준혁");
        Battle 배틀 = mongoTemplate.save(new Battle(1000, List.of(박성우, 박현준, 노준혁)));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하는_battleId를_받으면 {
            String battleId = 배틀.getId();

            @Test
            @DisplayName("배틀에 속한 러너들의 id를 반환한다.")
            void returnRunnerIds() {
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
            RunnerRecord 박성우_기록 = new RunnerRecord("배틀", "박성우");
            RunnerRecord 박현준_기록 = new RunnerRecord("배틀", "박현준");
            RunnerRecord 노준혁_기록 = new RunnerRecord("배틀", "노준혁");
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
}