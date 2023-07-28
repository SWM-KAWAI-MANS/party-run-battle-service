package online.partyrun.partyrunbattleservice.domain.battle.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataMongoTest
@DisplayName("BattleRepository")
class BattleRepositoryTest {

    @Autowired MongoTemplate mongoTemplate;
    @Autowired private BattleRepository battleRepository;
    Runner 박성우 = new Runner("박성우");
    Runner 박현준 = new Runner("박현준");
    Runner 노준혁 = new Runner("노준혁");

    @AfterEach
    void tearDown() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class findByStatusInAndRunnersIn는 {
        @Test
        @DisplayName("현재 참여중인 배틀이 없으면 빈 리스트를 반환한다.")
        void returnEmpty() {
            final List<Battle> actual =
                    battleRepository.findByStatusInAndRunnersIn(
                            List.of(BattleStatus.READY), List.of(박성우, 박현준, 노준혁));
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("현재 참여중인 배틀이 있으면 참여한 배틀 리스트를 반환한다.")
        void returnListBattle() {
            final Battle 배틀1 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준)));
            final Battle 배틀2 = battleRepository.save(new Battle(1000, List.of(노준혁)));

            노준혁.changeRunningStatus();
            배틀2.changeBattleRunning(LocalDateTime.now());
            battleRepository.save(배틀2);

            final List<Battle> actual =
                    battleRepository.findByStatusInAndRunnersIn(
                            List.of(BattleStatus.READY, BattleStatus.RUNNING),
                            List.of(박성우, 박현준, 노준혁));

            assertThat(actual).hasSize(2);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class findByStatusInAndRunnersId는 {
        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준)));

        @Test
        @DisplayName("현재 진행중인 상태의 배틀이 존재하면 반환한다.")
        void returnRunningBattle() {
            final Battle 박성우_진행중_배틀 =
                    battleRepository
                            .findByStatusAndRunnersId(BattleStatus.READY, 박성우.getId())
                            .orElseThrow();
            final Battle 박현준_진행중_배틀 =
                    battleRepository
                            .findByStatusAndRunnersId(BattleStatus.READY, 박현준.getId())
                            .orElseThrow();

            assertThat(박성우_진행중_배틀).usingRecursiveComparison().isEqualTo(박현준_진행중_배틀).isEqualTo(배틀);
        }

        @Test
        @DisplayName("현재 진행중인 상태의 배틀이 존재하지않으면 빈 값을 반환한다.")
        void returnEmpty() {
            final Optional<Battle> 노준혁_진행중_배틀 =
                    battleRepository.findByStatusAndRunnersId(BattleStatus.RUNNING, 노준혁.getId());

            assertThat(노준혁_진행중_배틀).isEmpty();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class existsByIdAndRunnersIdAndStatus는 {
        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준)));

        @Test
        @DisplayName("battleId, runnerId, status를 만족하는 데이터의 존재하면 true를 반환한다.")
        void returnTrue() {
            final boolean result =
                    battleRepository.existsByIdAndRunnersIdAndStatus(
                            배틀.getId(), 박성우.getId(), BattleStatus.READY);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("battleId, runnerId, status를 만족하는 데이터가 없으면 false를 반환한다.")
        void returnFalse() {
            final boolean result1 =
                    battleRepository.existsByIdAndRunnersIdAndStatus(
                            배틀.getId(), 박성우.getId(), BattleStatus.FINISHED);
            final boolean result2 =
                    battleRepository.existsByIdAndRunnersIdAndStatus(
                            배틀.getId(), 노준혁.getId(), BattleStatus.RUNNING);

            assertAll(() -> assertThat(result1).isFalse(), () -> assertThat(result2).isFalse());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_조회_시 {

        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준)));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class findBattleExceptRunnerRecords는 {

            @Test
            @DisplayName("현재까지의 기록들을 반환하지 않는다.")
            void returnNullRecords() {
                Battle result =
                        battleRepository
                                .findBattleExceptRunnerRecords(배틀.getId(), 박성우.getId())
                                .orElseThrow();
                assertThat(
                                result.getRunners().stream()
                                        .allMatch(r -> r.getRunnerRecords().isEmpty()))
                        .isTrue();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class addRunnerRecords는 {
        Battle 배틀;

        @BeforeEach
        void setUp() {
            배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준)));
        }

        LocalDateTime now = LocalDateTime.now();
        RunnerRecord record1 = new RunnerRecord(GpsData.of(0, 0, 0, now), 0);
        RunnerRecord record2 = new RunnerRecord(GpsData.of(1, 1, 1, now), 1);
        RunnerRecord record3 = new RunnerRecord(GpsData.of(2, 2, 2, now), 2);
        RunnerRecord record4 = new RunnerRecord(GpsData.of(3, 3, 3, now), 3);
        List<RunnerRecord> runnerRecords1 = List.of(record1, record2);
        List<RunnerRecord> runnerRecords2 = List.of(record3, record4);

        @Test
        @DisplayName("battleId, ruunerId, 새로운 기록을 입력받으면 runner에게 해당 기록을 저장한다.")
        void addRunnerNewRecords() {
            battleRepository.addRunnerRecords(배틀.getId(), 박성우.getId(), runnerRecords1);
            battleRepository.addRunnerRecords(배틀.getId(), 박성우.getId(), runnerRecords2);

            battleRepository.addRunnerRecords(배틀.getId(), 박현준.getId(), runnerRecords1);

            Battle battle = battleRepository.findById(배틀.getId()).orElseThrow();

            Assertions.assertAll(
                    () -> assertThat(battle.getRunnerRecords(박성우.getId())).hasSize(4),
                    () -> assertThat(battle.getRunnerRecords(박현준.getId())).hasSize(2));
        }
    }
}
