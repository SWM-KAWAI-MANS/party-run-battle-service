package online.partyrun.partyrunbattleservice.domain.battle.repository;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("BattleRepository")
class BattleRepositoryTest {

    @Autowired
    private RunnerRepository runnerRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들이_존재할_때 {

        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Runner 박현준 = runnerRepository.save(new Runner("박현준"));
        Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));

        @Test
        @DisplayName("현재 참여중인 배틀이 없으면 빈 리스트를 반환한다.")
        void returnEmpty() {
            final List<Battle> actual = battleRepository.findByStatusAndRunnersIn(BattleStatus.RUNNING, List.of(박성우, 박현준, 노준혁));
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("현재 참여중인 배틀이 있으면 참여한 배틀 리스트를 반환한다.")
        void returnListBattle() {
            final Battle battle1 = battleRepository.save(new Battle(List.of(박성우, 박현준)));
            final Battle battle2 = battleRepository.save(new Battle(List.of(노준혁)));

            final List<Battle> actual = battleRepository.findByStatusAndRunnersIn(BattleStatus.RUNNING, List.of(박성우, 박현준, 노준혁));

            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(battle1, battle2));
        }
    }
}