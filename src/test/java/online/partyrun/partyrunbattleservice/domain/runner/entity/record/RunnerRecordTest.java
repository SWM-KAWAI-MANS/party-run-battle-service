package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import online.partyrun.partyrunbattleservice.domain.runner.exception.IllegalRecordDistanceException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

@DisplayName("RunnerRecord")
class RunnerRecordTest {

    LocalDateTime time = LocalDateTime.now();
    GpsData GPSDATA_1 = GpsData.of(1, 1, 1, time);
    GpsData GPSDATA_2 = GpsData.of(2, 2, 2, time.plusSeconds(1));

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class RunnerRecord_생성_시 {

        @Test
        @DisplayName("거리가 0 미만이면 예외를 던진다.")
        void throwDistanceException() {
            assertThatThrownBy(() -> new RunnerRecord(GPSDATA_1, -1))
                    .isInstanceOf(IllegalRecordDistanceException.class);
        }

        @Test
        @DisplayName("GpsData가 null이면 예외를 던진다.")
        void throwGpsDataException() {
            assertThatThrownBy(() -> new RunnerRecord(null, 1))
                    .isInstanceOf(InvalidGpsDataException.class);
        }

        @Test
        @DisplayName("parameter가 정상적이라면 생성한다.")
        void create() {
            assertThatCode(() -> new RunnerRecord(GPSDATA_1, 1)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 현재_기록으로부터_새로운_기록_생성_시 {

        RunnerRecord runnerRecord = new RunnerRecord(GPSDATA_1, 1);

        @Test
        @DisplayName("거리 차이를 통해 새로운 기록을 생성한다.")
        void createNewRecord() {
            RunnerRecord newRecord = runnerRecord.createNewRecord(GPSDATA_2);

            assertThat(newRecord.getDistance()).isGreaterThan(runnerRecord.getDistance());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 기록을_비교할_때 {

        @Test
        @DisplayName("GpsData의 시간이 클수록 크다.")
        void compareTo() {

            RunnerRecord record1 = new RunnerRecord(GPSDATA_1, 0);
            RunnerRecord record2 = new RunnerRecord(GPSDATA_2, 0);

            assertAll(
                    () -> assertThat(record1.compareTo(record2)).isNegative(),
                    () -> assertThat(record1.compareTo(record1)).isZero(),
                    () -> assertThat(record2.compareTo(record1)).isPositive());
        }
    }
}
