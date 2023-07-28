package online.partyrun.partyrunbattleservice.domain.runner.entity;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RunnerStatus")
class RunnerStatusTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class isReady_메소드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class READY_상태라면 {
            RunnerStatus runnerStatus = RunnerStatus.READY;

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                assertThat(runnerStatus.isReady()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class READY_상태가_아니면 {
            RunnerStatus runnerStatus = RunnerStatus.RUNNING;

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(runnerStatus.isReady()).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class isRunning_메소드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING_상태라면 {
            RunnerStatus runnerStatus = RunnerStatus.RUNNING;

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                assertThat(runnerStatus.isRunning()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING_상태가_아니면 {
            RunnerStatus runnerStatus = RunnerStatus.READY;

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(runnerStatus.isRunning()).isFalse();
            }
        }
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class isFinished_메소드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class FINISHED_상태라면 {
            RunnerStatus runnerStatus = RunnerStatus.FINISHED;

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                assertThat(runnerStatus.isFinished()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING_상태가_아니면 {
            RunnerStatus runnerStatus = RunnerStatus.READY;

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(runnerStatus.isFinished()).isFalse();
            }
        }
    }
}
