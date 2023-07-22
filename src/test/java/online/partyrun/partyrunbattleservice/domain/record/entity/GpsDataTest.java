package online.partyrun.partyrunbattleservice.domain.record.entity;

import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.GPSDATA_1;
import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.GPSDATA_2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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

            assertThatThrownBy(() -> GpsData.of(1, 1, 1, time, minTime))
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

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GpsData_사이의_거리를_측정_시 {

        @Test
        @DisplayName("GpsData가 null이면 예외를 던진다.")
        void throwException() {
            assertThatThrownBy(() -> GPSDATA_1.calculateDistance(null))
                    .isInstanceOf(InvalidGpsDataException.class);
        }

        @Test
        @DisplayName("GpsData를 받으면 거리를 계산한다.")
        void calculate() {
            assertThat(GPSDATA_1.calculateDistance(GPSDATA_1)).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("비교 시 gps 시간에 따라 크기가 결정된다.")
    void compareTo() {
        int result1 = GPSDATA_1.compareTo(GPSDATA_2);
        int result2 = GPSDATA_1.compareTo(GPSDATA_1);
        int result3 = GPSDATA_2.compareTo(GPSDATA_1);

        assertAll(
                () -> assertThat(result1).isNegative(),
                () -> assertThat(result2).isZero(),
                () -> assertThat(result3).isPositive());
    }
}
