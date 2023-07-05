package online.partyrun.partyrunbattleservice.domain.battle.entity;

import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidDistanceException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidRunnerNumberInBattleException;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

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

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_변경할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 상태를_받으면 {

            Battle 배틀 = new Battle(1000, List.of(박성우, 박현준));
            RunnerStatus runnerStatus = RunnerStatus.RUNNING;

            @Test
            @DisplayName("러너의 상태를 변경한다.")
            void changeRunnerStatus() {
                배틀.changeRunnerStatus(박성우.getId(), runnerStatus);
                assertThat(박성우.getStatus()).isEqualTo(runnerStatus);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_내에_러너가_존재하지_않으면 {

            Battle 배틀 = new Battle(1000, List.of(박성우, 박현준));

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(() -> 배틀.changeRunnerStatus(노준혁.getId(), RunnerStatus.RUNNING))
                        .isInstanceOf(RunnerNotFoundException.class);
            }
        }
    }
}