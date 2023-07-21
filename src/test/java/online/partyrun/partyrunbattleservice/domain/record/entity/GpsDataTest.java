package online.partyrun.partyrunbattleservice.domain.record.entity;

import online.partyrun.partyrunbattleservice.domain.record.exception.InvalidGpsDataTimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

@DisplayName("GpsData")
class GpsDataTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GpsData_생성_시 {

        LocalDateTime time = LocalDateTime.now();

        @Test
        @DisplayName("최소 시간보다 작은 시간 데이터가 들어오면 예외를 던진다.")
        void throwException() {
            LocalDateTime minTime = time.plusSeconds(1);

            Assertions.assertThatThrownBy(() -> GpsData.of(1, 1, 1, time, minTime))
                    .isInstanceOf(InvalidGpsDataTimeException.class);
        }

        @Test
        @DisplayName("최소 시간보다 큰 데이터가 들어오면 예외를 던진다.")
        void returnGpsData() {
            LocalDateTime minTime = time.minusSeconds(1);

            Assertions.assertThatCode(() -> GpsData.of(1, 1, 1, time, minTime))
                    .doesNotThrowAnyException();
        }
    }
}