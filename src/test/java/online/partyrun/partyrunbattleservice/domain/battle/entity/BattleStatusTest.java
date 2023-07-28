package online.partyrun.partyrunbattleservice.domain.battle.entity;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BattleStatus")
class BattleStatusTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class isFinished_메소드는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_상태가_FINISHED_일_때 {
            BattleStatus battleStatus = BattleStatus.FINISHED;

            @Test
            @DisplayName("True를 반환한다.")
            void returnTrue() {
                assertThat(battleStatus.isFinished()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_상태가_FINISHED가_아닐_때 {
            BattleStatus battleStatus = BattleStatus.RUNNING;

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(battleStatus.isFinished()).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class isReady_메소드는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_상태가_READY_일_때 {
            BattleStatus battleStatus = BattleStatus.READY;

            @Test
            @DisplayName("True를 반환한다.")
            void returnTrue() {
                assertThat(battleStatus.isReady()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_상태가_READY가_아닐_때 {
            BattleStatus battleStatus = BattleStatus.RUNNING;

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(battleStatus.isFinished()).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class isRunning_메소드는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_상태가_RUNNING_일_때 {
            BattleStatus battleStatus = BattleStatus.RUNNING;

            @Test
            @DisplayName("True를 반환한다.")
            void returnTrue() {
                assertThat(battleStatus.isRunning()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_상태가_RUNNING이_아닐_때 {
            BattleStatus battleStatus = BattleStatus.READY;

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(battleStatus.isRunning()).isFalse();
            }
        }
    }
}
