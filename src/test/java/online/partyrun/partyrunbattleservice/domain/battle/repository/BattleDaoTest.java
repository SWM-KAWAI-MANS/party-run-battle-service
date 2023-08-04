package online.partyrun.partyrunbattleservice.domain.battle.repository;

import static org.assertj.core.api.Assertions.assertThat;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@DisplayName("BattleDao")
@SpringBootTest
class BattleDaoTest {

    @Autowired BattleDao battleDao;

    @Autowired BattleRepository battleRepository;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_update할_때 {
        Runner 박성우 = new Runner("박성우");
        Runner 박현준 = new Runner("박현준");
        Runner 노준혁 = new Runner("노준혁");
        Battle 배틀 =
                battleRepository.save(
                        new Battle(1000, List.of(박성우, 박현준, 노준혁), LocalDateTime.now()));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너의_상태를_받으면 {

            @Test
            @DisplayName("배틀을 업데이트 후 반환한다.")
            void updateBattle() {
                Battle battle =
                        battleDao.updateRunnerStatus(배틀.getId(), 박성우.getId(), RunnerStatus.RUNNING);

                assertThat(battle.getRunnerStatus(박성우.getId())).isEqualTo(RunnerStatus.RUNNING);
            }
        }
    }
}
