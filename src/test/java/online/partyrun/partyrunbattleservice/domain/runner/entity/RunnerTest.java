package online.partyrun.partyrunbattleservice.domain.runner.entity;

import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerStatusCannotBeChangedException;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Runner")
class RunnerTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_변경할_때 {
        Runner 박성우 = new Runner("박성우");

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_정상적이라면 {
            RunnerStatus runnerStatus = RunnerStatus.RUNNING;

            @Test
            @DisplayName("상태를 변경한다")
            void changeStatus() {
                박성우.changeStatus(runnerStatus);
                assertThat(박성우.getStatus()).isEqualTo(runnerStatus);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_READY라면 {
            RunnerStatus runnerStatus = RunnerStatus.READY;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 박성우.changeStatus(runnerStatus))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_null라면 {
            RunnerStatus runnerStatus = null;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 박성우.changeStatus(runnerStatus))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_이전_상태와_같다면 {
            RunnerStatus runnerStatus = RunnerStatus.RUNNING;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                박성우.changeStatus(runnerStatus);
                assertThatThrownBy(() -> 박성우.changeStatus(runnerStatus))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }
    }
}
