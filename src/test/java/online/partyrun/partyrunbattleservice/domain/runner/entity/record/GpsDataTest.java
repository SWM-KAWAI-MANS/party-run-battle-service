package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import online.partyrun.partyrunbattleservice.domain.runner.exception.GpsTimeNullException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.PastGpsDataTimeException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("GpsData")
class GpsDataTest {
    LocalDateTime time = LocalDateTime.now();
    int durationNano = 500_000_000;
    GpsData GPSDATA_0 = GpsData.of(0, 0, 1, time);
    GpsData GPSDATA_1 = GpsData.of(0.0001, 0.0001, 1, time.plusNanos(durationNano * 2L));
    GpsData GPSDATA_2 = GpsData.of(0.0002, 0.0002, 2, time.plusNanos(durationNano * 3L));

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GpsData_사이의_거리를_측정_시 {

        @Test
        @DisplayName("GpsData가 null이면 예외를 던진다.")
        void throwNullException() {
            assertThatThrownBy(() -> GPSDATA_0.calculateDistance(null))
                    .isInstanceOf(InvalidGpsDataException.class);
        }

        @Test
        @DisplayName("과거 시간의 데이터와 비교시 예외를 던진다.")
        void throwPastDataException() {
            assertThatThrownBy(() -> GPSDATA_1.calculateDistance(GPSDATA_0))
                    .isInstanceOf(PastGpsDataTimeException.class);
        }

        @Test
        @DisplayName("GpsData를 받으면 거리를 계산한다.")
        void calculate() {
            assertThat(GPSDATA_0.calculateDistance(GPSDATA_0)).isZero();
        }
    }

    @Test
    @DisplayName("비교 시 gps 시간에 따라 비교를 할 수 있다.")
    void compareTo() {
        int result1 = GPSDATA_1.compareTo(GPSDATA_2);
        int result2 = GPSDATA_1.compareTo(GPSDATA_1);
        int result3 = GPSDATA_2.compareTo(GPSDATA_1);

        assertAll(
                () -> assertThat(result1).isNegative(),
                () -> assertThat(result2).isZero(),
                () -> assertThat(result3).isPositive());
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 특정_시간_이전인지_확인할_때 {

        @Test
        @DisplayName("특정 시간보다 이전이라면 true를 반환한다.")
        void returnTrue() {
            boolean result = GPSDATA_1.isBefore(GPSDATA_1.getTime().plusSeconds(100));

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("특정 시간보다 이후라면 false를 반환한다.")
        void returnFalse() {
            boolean result = GPSDATA_1.isBefore(GPSDATA_1.getTime().minusSeconds(100));

            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("gps_생성시_시간이_null이면_예외")
    void throwException() {
        assertThatThrownBy(() -> GpsData.of(1, 1, 1, null))
                .isInstanceOf(GpsTimeNullException.class);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GpsData_사이의_시간을_측정_시 {

        @Test
        @DisplayName("소숫점의 초 단위로 반환한다.")
        void returnDuration() {
            final double duration1 = GPSDATA_0.calculateDuration(GPSDATA_1);
            final double duration2 = GPSDATA_1.calculateDuration(GPSDATA_2);

            assertAll(
                    () -> assertThat(duration1).isEqualTo(1),
                    () ->  assertThat(duration2).isEqualTo(0.5)
            );
        }
    }
}
