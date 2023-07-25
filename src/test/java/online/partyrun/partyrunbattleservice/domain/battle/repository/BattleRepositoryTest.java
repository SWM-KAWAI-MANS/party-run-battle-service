package online.partyrun.partyrunbattleservice.domain.battle.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    class 러너들이_존재할_때 {


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
                배틀2.changeBattleStatus(BattleStatus.RUNNING);
                battleRepository.save(배틀2);

                final List<Battle> actual =
                        battleRepository.findByStatusInAndRunnersIn(
                                List.of(BattleStatus.READY, BattleStatus.RUNNING),
                                List.of(박성우, 박현준, 노준혁));

                assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(배틀1, 배틀2));
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

                assertThat(박성우_진행중_배틀)
                        .usingRecursiveComparison()
                        .isEqualTo(박현준_진행중_배틀)
                        .isEqualTo(배틀);
            }

            @Test
            @DisplayName("현재 진행중인 상태의 배틀이 존재하지않으면 빈 값을 반환한다.")
            void returnEmpty() {
                final Optional<Battle> 노준혁_진행중_배틀 =
                        battleRepository.findByStatusAndRunnersId(
                                BattleStatus.RUNNING, 노준혁.getId());

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
                Battle result = battleRepository.findBattleExceptRunnerRecords(배틀.getId(), 박성우.getId()).orElseThrow();
                assertThat(result.getRunners().stream().allMatch(r -> Objects.isNull(r.getRunnerRecords()))).isTrue();
            }
        }
    }
}
