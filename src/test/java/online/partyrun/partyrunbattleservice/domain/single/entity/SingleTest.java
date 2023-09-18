package online.partyrun.partyrunbattleservice.domain.single.entity;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.exception.SingleRunnerRecordEmptyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Single")
class SingleTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Single_기록을_생성할_때 {

        @DisplayName("달린 기록이 비었다면 예외를 던진다.")
        @NullAndEmptySource
        @ParameterizedTest
        void throwException(List<RunnerRecord> records) {
            assertThatThrownBy(() -> new Single("runnerId", new RunningTime(1, 1, 1), records))
                    .isInstanceOf(SingleRunnerRecordEmptyException.class);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Single_기록의_주인을_판단할_때 {

        final String runnerId = "runnerId";

        @Test
        @DisplayName("주인이라면 true를 반환한다.")
        void returnTrue() {
            final Single single = new Single(runnerId, new RunningTime(1, 1, 1), List.of(new RunnerRecord(GpsData.of(1, 1, 1, LocalDateTime.now()), 0)));
            assertThat(single.isOwner(runnerId)).isTrue();
        }

        @Test
        @DisplayName("주인이 아니라면 false를 반환한다.")
        void returnFalse() {
            final Single single = new Single(runnerId, new RunningTime(1, 1, 1), List.of(new RunnerRecord(GpsData.of(1, 1, 1, LocalDateTime.now()), 0)));
            assertThat(single.isOwner("notOwner")).isFalse();
        }
    }
}
