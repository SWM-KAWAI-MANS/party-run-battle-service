package online.partyrun.partyrunbattleservice.domain.single.entity;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.exception.SingleRunnerRecordEmptyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

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
            assertThatThrownBy(() -> new Single("runnerId", records))
                    .isInstanceOf(SingleRunnerRecordEmptyException.class);
        }
    }
}
