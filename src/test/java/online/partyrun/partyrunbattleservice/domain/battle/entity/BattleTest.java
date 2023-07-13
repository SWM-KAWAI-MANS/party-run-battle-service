package online.partyrun.partyrunbattleservice.domain.battle.entity;

import static org.assertj.core.api.Assertions.*;

import online.partyrun.partyrunbattleservice.domain.battle.exception.*;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerNotFoundException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
                assertThatCode(() -> new Battle(distance, runnerList)).doesNotThrowAnyException();
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

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_FINISHED_상태라면 {

            Battle 배틀 = new Battle(1000, List.of(박성우, 박현준));

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                배틀.changeBattleStatus(BattleStatus.FINISHED);
                assertThatThrownBy(() -> 배틀.changeRunnerStatus(노준혁.getId(), RunnerStatus.RUNNING))
                        .isInstanceOf(BattleAlreadyFinishedException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀의_상태를_변경할_때 {

        Battle 배틀 = new Battle(1000, List.of(박성우, 박현준));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 상태를_받으면 {

            BattleStatus status = BattleStatus.RUNNING;

            @Test
            @DisplayName("배틀의 상태를 변경한다.")
            void changeBattleStatus() {
                배틀.changeBattleStatus(status);
                assertThat(배틀.getStatus()).isEqualTo(status);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_FINISHED_상태라면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                배틀.changeBattleStatus(BattleStatus.FINISHED);
                assertThatThrownBy(() -> 배틀.changeBattleStatus(BattleStatus.RUNNING))
                        .isInstanceOf(BattleAlreadyFinishedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 새로운_상태가_null_또는_READY라면 {

            private static List<Arguments> BattleStatusProvider() {
                return List.of(Arguments.of(BattleStatus.READY));
            }

            @ParameterizedTest
            @NullSource
            @MethodSource("BattleStatusProvider")
            @DisplayName("예외를 던진다.")
            void throwException(BattleStatus status) {
                assertThatThrownBy(() -> 배틀.changeBattleStatus(status))
                        .isInstanceOf(BattleStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 새로운_상태가_현재_상태와_같다면 {

            BattleStatus status = BattleStatus.RUNNING;

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                배틀.changeBattleStatus(status);
                assertThatThrownBy(() -> 배틀.changeBattleStatus(status))
                        .isInstanceOf(BattleStatusCannotBeChangedException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀의_시작_시간을_설정할_떄 {
        Battle 배틀 = new Battle(1000, List.of(박성우, 박현준));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 시작_시간이_생성_시간_보다_같거나_이후라면 {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.plusSeconds(1);

            @Test
            @DisplayName("시작 시간을 설정한다")
            void setStartTime() {
                배틀.setStartTime(now, startTime);

                assertThat(배틀.getStartTime()).isEqualTo(startTime);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 시작_시간이_생성_시간_보다_같거나_이전이라면 {

            public static Stream<Arguments> invalidStartTime() {
                final LocalDateTime now = LocalDateTime.now();
                return Stream.of(Arguments.of(now, now), Arguments.of(now, now.minusSeconds(1)));
            }

            @ParameterizedTest
            @MethodSource("invalidStartTime")
            @DisplayName("예외를 던진다.")
            void throwException(LocalDateTime now, LocalDateTime startTime) {

                assertThatThrownBy(() -> 배틀.setStartTime(now, startTime))
                        .isInstanceOf(InvalidBattleStartTimeException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_찾을_떄 {
        Battle 배틀 = new Battle(1000, List.of(박성우, 박현준));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너의_id를_받으면 {

            String runnerId = 박성우.getId();

            @Test
            @DisplayName("러너의 상태를 반환한다.")
            void returnRunnerStatus() {
                RunnerStatus runnerStatus = 배틀.getRunnerStatus(runnerId);

                assertThat(runnerStatus).isEqualTo(박성우.getStatus());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하지_않는_러너라면 {

            String invalidRunnerId = "invalidRunnerId";

            @Test
            @DisplayName("러너의 상태를 반환한다.")
            void returnRunnerStatus() {
                assertThatThrownBy(() -> 배틀.getRunnerStatus(invalidRunnerId))
                        .isInstanceOf(RunnerNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀의_러너들이_모두_RUNNING_상태인지_확인할_때 {

        Runner 박성우 = new Runner("박성우");
        Runner 노준혁 = new Runner("노준혁");
        Battle 배틀 = new Battle(1000, List.of(박성우, 노준혁));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_맞다면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeStatus(RunnerStatus.RUNNING);
                노준혁.changeStatus(RunnerStatus.RUNNING);

                assertThat(배틀.isAllRunnersRunningStatus()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 한명이라도_아니라면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeStatus(RunnerStatus.FINISHED);

                assertThat(배틀.isAllRunnersRunningStatus()).isFalse();
            }
        }
    }
}
