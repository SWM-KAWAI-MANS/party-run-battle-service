package online.partyrun.partyrunbattleservice.domain.battle.repository;

import static org.assertj.core.api.Assertions.assertThat;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@DataMongoTest
@DisplayName("BattleRepository")
class BattleRepositoryTest {

    @Autowired
    private RunnerRepository runnerRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @AfterEach
    void tearDown() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들이_존재할_때 {

        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Runner 박현준 = runnerRepository.save(new Runner("박현준"));
        Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class findByStatusAndRunnersIn은 {
            @Test
            @DisplayName("현재 참여중인 배틀이 없으면 빈 리스트를 반환한다.")
            void returnEmpty() {
                final List<Battle> actual =
                        battleRepository.findByStatusAndRunnersIn(
                                BattleStatus.RUNNING, List.of(박성우, 박현준, 노준혁));
                assertThat(actual).isEmpty();
            }

            @Test
            @DisplayName("현재 참여중인 배틀이 있으면 참여한 배틀 리스트를 반환한다.")
            void returnListBattle() {
                final Battle battle1 = battleRepository.save(new Battle(List.of(박성우, 박현준)));
                final Battle battle2 = battleRepository.save(new Battle(List.of(노준혁)));

                final List<Battle> actual =
                        battleRepository.findByStatusAndRunnersIn(
                                BattleStatus.RUNNING, List.of(박성우, 박현준, 노준혁));

                assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(battle1, battle2));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class findByStatusAndRunnersId는 {
            Battle battle = battleRepository.save(new Battle(List.of(박성우, 박현준)));

            @Test
            @DisplayName("현재 진행중인 상태의 배틀이 존재하면 반환한다.")
            void returnRunningBattle() {
                final Battle 박성우_진행중_배틀 = battleRepository.findByStatusAndRunnersId(BattleStatus.RUNNING, 박성우.getId()).orElseThrow();
                final Battle 박현준_진행중_배틀 = battleRepository.findByStatusAndRunnersId(BattleStatus.RUNNING, 박현준.getId()).orElseThrow();

                assertThat(박성우_진행중_배틀)
                        .usingRecursiveComparison()
                        .isEqualTo(박현준_진행중_배틀)
                        .isEqualTo(battle);
            }

            @Test
            @DisplayName("현재 진행중인 상태의 배틀이 존재하지않으면 빈 값을 반환한다.")
            void returnEmpty() {
                final Optional<Battle> 노준혁_진행중_배틀 = battleRepository.findByStatusAndRunnersId(BattleStatus.RUNNING, 노준혁.getId());

                assertThat(노준혁_진행중_배틀).isEmpty();
            }
        }
    }
}
