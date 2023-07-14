package online.partyrun.partyrunbattleservice.domain.runner.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerAlreadyFinishedException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerStatusCannotBeChangedException;

import org.junit.jupiter.api.*;

@DisplayName("Runner")
class RunnerTest {

    Runner 박성우 = new Runner("박성우");

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_변경할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_정상적이라면 {
            RunnerStatus status = RunnerStatus.RUNNING;

            @Test
            @DisplayName("상태를 변경한다")
            void changeStatus() {
                박성우.changeStatus(status);
                assertThat(박성우.getStatus()).isEqualTo(status);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 현재_상태가_FINISHED라면 {
            RunnerStatus status = RunnerStatus.RUNNING;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                박성우.changeStatus(RunnerStatus.FINISHED);
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerAlreadyFinishedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_READY라면 {
            RunnerStatus status = RunnerStatus.READY;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_null라면 {
            RunnerStatus status = null;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_이전_상태와_같다면 {
            RunnerStatus status = RunnerStatus.RUNNING;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                박성우.changeStatus(status);
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_id가_같은지_확인할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class id가_같다면 {
            String id = 박성우.getId();

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                assertThat(박성우.hasId(id)).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class id가_다르다면 {
            String id = "not equal id";

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(박성우.hasId(id)).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태가_RUNNING인지_확인할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING_이라면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeStatus(RunnerStatus.RUNNING);

                assertThat(박성우.isRunning()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING이_아니라면 {

            @Test
            @DisplayName("false를 반환한다.")
            void returnFalse() {
                박성우.changeStatus(RunnerStatus.FINISHED);

                assertThat(박성우.isRunning()).isFalse();
            }
        }
    }
}
