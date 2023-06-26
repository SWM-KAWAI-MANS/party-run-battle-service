package online.partyrun.partyrunbattleservice.domain.battle.entity;

import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidNumberOfBattleRunnerException;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Battle")
class BattleTest {

    Runner 박성우 = new Runner("1", "박성우");
    Runner 노준혁 = new Runner("2", "노준혁");
    Runner 박현준 = new Runner("3", "박현준");

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들이_주어졌을_때 {

        List<Runner> runners = List.of(박성우, 노준혁, 박현준);

        @Test
        @DisplayName("방을 생성한다.")
        void createBattle() {
            final Battle battle = new Battle(runners);
            assertThat(battle.getRunners()).isNotEmpty();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_인원이_올바르지_않을때 {

        List<Runner> runners = List.of(박성우, 노준혁);

        @Test
        @DisplayName("방을 생성한다.")
        void createBattle() {
            assertThatThrownBy(() -> new Battle(runners))
                    .isInstanceOf(InvalidNumberOfBattleRunnerException.class);
        }
    }
}
