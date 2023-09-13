package online.partyrun.partyrunbattleservice.domain.single.entity;

import online.partyrun.partyrunbattleservice.domain.single.exception.IllegalRunningTimeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RunningTime")
class RunningTimeTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class RunningTime을_생성할_때 {

        @ParameterizedTest
        @DisplayName("잘못된 시간이 들어오면 예외를 던진다.")
        @CsvSource(value = {"-1,1,1", "1,-1,1", "1,60,1", "1,59,-1", "1,59,60","0,0,0"})
        void throwExceptionForCorrectTime(int hours, int minutes, int seconds) {
            assertThatThrownBy(() -> new RunningTime(hours, minutes, seconds))
                    .isInstanceOf(IllegalRunningTimeException.class);
        }

        @ParameterizedTest
        @DisplayName("올바른 시간이 들어오면 RunningTime을 생성한다.")
        @CsvSource(value = {"0,0,1", "0,1,0", "1,0,0"})
        void create(int hours, int minutes, int seconds) {
            assertThatCode(() -> new RunningTime(hours, minutes, seconds))
                    .doesNotThrowAnyException();
        }
    }
}
