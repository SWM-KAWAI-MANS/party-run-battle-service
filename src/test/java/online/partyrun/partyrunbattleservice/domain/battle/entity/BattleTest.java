package online.partyrun.partyrunbattleservice.domain.battle.entity;

import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidDistanceException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidRunnerNumberInBattleException;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Battle")
class BattleTest {

    Runner 박성우 = new Runner("박성우");
    Runner 노준혁 = new Runner("노준혁");
    Runner 박현준 = new Runner("박현준");

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_생성할_때 {

        List<Runner> runnerList = List.of(박성우, 노준혁, 박현준);
        int distance = 1000;

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 거리와_러너의_수가_올바르다면 {

            @Test
            @DisplayName("배틀을 생성한다.")
            void createBattle() {
                assertThatCode(() -> new Battle(distance, runnerList))
                        .doesNotThrowAnyException();
            }

        }
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 거리가_1_미만이라면 {

            @ParameterizedTest
            @ValueSource(ints = {-1, 0})
            @DisplayName("예외를 던진다.")
            void throwException(int distance) {
                assertThatThrownBy(() -> new Battle(distance, runnerList))
                      .isInstanceOf(InvalidDistanceException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너가_없다면 {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("예외를 던진다.")
            void throwException(List<Runner> runners) {
                assertThatThrownBy(() -> new Battle(distance, runners))
                        .isInstanceOf(InvalidRunnerNumberInBattleException.class);
            }
        }
    }
}