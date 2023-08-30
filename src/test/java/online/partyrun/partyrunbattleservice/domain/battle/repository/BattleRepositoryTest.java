package online.partyrun.partyrunbattleservice.domain.battle.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataMongoTest
@DisplayName("BattleRepository")
class BattleRepositoryTest {

    @Autowired MongoTemplate mongoTemplate;
    @Autowired private BattleRepository battleRepository;
    Runner 박성우;
    Runner 박현준;
    Runner 노준혁;
    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        박성우 = new Runner("박성우");
        박현준 = new Runner("박현준");
        노준혁 = new Runner("노준혁");
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class existsByRunnersIdInAndRunnersStatusIn는 {
        @Test
        @DisplayName("러너가 현재 달리는 중이거나, 준비 상태라면 True를 반환한다.")
        void returnFalse() {
            boolean actual =
                    battleRepository.existsByRunnersIdInAndRunnersStatusIn(
                            List.of(박성우.getId(), 박현준.getId(), 노준혁.getId()),
                            List.of(RunnerStatus.READY, RunnerStatus.RUNNING));
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("모든 러너가 현재 달리는 중이거나, 준비 상태라면 True를 반환한다.")
        void returnTrueAllRunner() {
            final Battle 배틀1 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준), now));
            final Battle 배틀2 = battleRepository.save(new Battle(1000, List.of(노준혁), now));

            노준혁.changeRunningStatus();
            battleRepository.save(배틀2);

            boolean actual =
                    battleRepository.existsByRunnersIdInAndRunnersStatusIn(
                            List.of(박성우.getId(), 박현준.getId(), 노준혁.getId(), "invalid runner"),
                            List.of(RunnerStatus.READY, RunnerStatus.RUNNING));

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("한명의 러너라도 현재 달리는 중이거나, 준비 상태라면 True를 반환한다.")
        void returnTrueOneRunner() {
            final Battle 배틀2 = battleRepository.save(new Battle(1000, List.of(노준혁), now));

            boolean actual =
                    battleRepository.existsByRunnersIdInAndRunnersStatusIn(
                            List.of(박성우.getId(), 박현준.getId(), 노준혁.getId()),
                            List.of(RunnerStatus.READY, RunnerStatus.RUNNING));

            assertThat(actual).isTrue();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class findByRunnersIdAndRunnersStatus는 {
        Battle 배틀;

        @BeforeEach
        void setUp() {
            배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준), now));
        }

        @Test
        @DisplayName("현재 준비 중인 배틀이 존재하면 반환한다.")
        void returnReadyBattle() {
            final Battle 박성우_진행중_배틀 =
                    battleRepository
                            .findByRunnersIdAndRunnersStatus(박성우.getId(), RunnerStatus.READY)
                            .orElseThrow();
            final Battle 박현준_진행중_배틀 =
                    battleRepository
                            .findByRunnersIdAndRunnersStatus(박현준.getId(), RunnerStatus.READY)
                            .orElseThrow();

            assertThat(박성우_진행중_배틀).usingRecursiveComparison().isEqualTo(박현준_진행중_배틀);
        }

        @Test
        @DisplayName("현재 진행중인 상태의 배틀이 존재하지않으면 빈 값을 반환한다.")
        void returnEmpty() {
            final Optional<Battle> 노준혁_진행중_배틀 =
                    battleRepository.findByRunnersIdAndRunnersStatus(
                            노준혁.getId(), RunnerStatus.RUNNING);

            assertThat(노준혁_진행중_배틀).isEmpty();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class existsByIdAndRunnersIdAndRunnersStatus는 {
        Battle 배틀;

        @BeforeEach
        void setUp() {
            배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준), now));
        }

        @Test
        @DisplayName("battleId, runnerId, status를 만족하는 데이터의 존재하면 true를 반환한다.")
        void returnTrue() {
            final boolean result =
                    battleRepository.existsByIdAndRunnersIdAndRunnersStatus(
                            배틀.getId(), 박성우.getId(), RunnerStatus.READY);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("battleId, runnerId, status를 만족하는 데이터가 없으면 false를 반환한다.")
        void returnFalse() {
            final boolean result1 =
                    battleRepository.existsByIdAndRunnersIdAndRunnersStatus(
                            배틀.getId(), 박성우.getId(), RunnerStatus.FINISHED);
            final boolean result2 =
                    battleRepository.existsByIdAndRunnersIdAndRunnersStatus(
                            배틀.getId(), 노준혁.getId(), RunnerStatus.RUNNING);

            assertAll(() -> assertThat(result1).isFalse(), () -> assertThat(result2).isFalse());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_조회_시 {

        Battle 배틀;

        @BeforeEach
        void setUp() {
            배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 노준혁), now));
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class findBattleExceptRunnerRecords는 {

            @BeforeEach
            void setUp() {
                LocalDateTime now = LocalDateTime.now();
                노준혁.changeRunningStatus();
                박성우.changeRunningStatus();
                배틀.setStartTime(now);
                배틀.addRecords(박성우.getId(), List.of(GpsData.of(0, 0, 0, now.plusSeconds(10))));
                배틀.addRecords(노준혁.getId(), List.of(GpsData.of(0, 0, 0, now.plusSeconds(10))));
                battleRepository.save(배틀);
            }

            @Test
            @DisplayName("현재까지의 기록들을 반환하지 않는다.")
            void returnNullRecords() {
                Battle result = battleRepository
                                .findBattleExceptRunnerRecords(배틀.getId(), 박성우.getId())
                                .orElseThrow();

                assertThat(result.getRunners().stream().allMatch(r -> r.getRunnerRecords().isEmpty())).isTrue();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class addRunnerRecords는 {
        Battle 배틀;

        @BeforeEach
        void setUp() {
            int targetDistance = 100000;
            배틀 = battleRepository.save(new Battle(targetDistance, List.of(박성우, 박현준), now));
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
            battleRepository.addRunnerRecordsAndUpdateRunnerStatus(
                    배틀.getId(),
                    박성우.getId(),
                    runnerRecords1,
                    박성우.getRecentRunnerRecord(),
                    RunnerStatus.RUNNING);
            battleRepository.addRunnerRecordsAndUpdateRunnerStatus(
                    배틀.getId(),
                    박성우.getId(),
                    runnerRecords2,
                    박성우.getRecentRunnerRecord(),
                    RunnerStatus.FINISHED);
            battleRepository.addRunnerRecordsAndUpdateRunnerStatus(
                    배틀.getId(),
                    박현준.getId(),
                    runnerRecords1,
                    박현준.getRecentRunnerRecord(),
                    RunnerStatus.RUNNING);

            Battle battle = battleRepository.findById(배틀.getId()).orElseThrow();

            assertAll(
                    () -> assertThat(battle.getRunnerRecords(박성우.getId())).hasSize(4),
                    () -> assertThat(battle.isRunnerFinished(박성우.getId())).isTrue(),
                    () -> assertThat(battle.getRunnerRecords(박현준.getId())).hasSize(2),
                    () -> assertThat(battle.isRunnerFinished(박현준.getId())).isFalse());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class findBattleByRunnerStatus는 {

        Battle 배틀1;
        Battle 배틀2;

        @BeforeEach
        void setUp() {
            배틀1 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준), now));
            노준혁.changeRunningStatus();
            배틀2 = battleRepository.save(new Battle(1000, List.of(노준혁), now));
        }

        @Test
        @DisplayName("특정 상태의 러너를 가진 배틀을 조회한다.")
        void returnBattle() {
            final Battle battle1 = battleRepository.findBattleByRunnerStatus(
                    박성우.getId(),
                    List.of(RunnerStatus.READY, RunnerStatus.RUNNING)).orElseThrow();
            final Battle battle2 = battleRepository.findBattleByRunnerStatus(
                    노준혁.getId(),
                    List.of(RunnerStatus.READY, RunnerStatus.RUNNING)).orElseThrow();

            assertAll(
                    () -> assertThat(battle1.getId()).isEqualTo(배틀1.getId()),
                    () -> assertThat(battle2.getId()).isEqualTo(배틀2.getId())
            );
        }

        @Test
        @DisplayName("특정 상태의 러너가 없다면 배틀을 반환하지 않는다.")
        void returnNull() {
            final Optional<Battle> battle1 = battleRepository.findBattleByRunnerStatus(
                    박성우.getId(),
                    List.of(RunnerStatus.FINISHED));
            final Optional<Battle> battle2 = battleRepository.findBattleByRunnerStatus(
                    노준혁.getId(),
                    List.of(RunnerStatus.FINISHED));

            assertAll(
                    () -> assertThat(battle1).isEmpty(),
                    () -> assertThat(battle2).isEmpty()
            );
        }
    }
}
