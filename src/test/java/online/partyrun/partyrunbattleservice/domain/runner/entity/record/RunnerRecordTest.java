package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import online.partyrun.partyrunbattleservice.domain.runner.exception.IllegalRecordDistanceException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("RunnerRecord")
class RunnerRecordTest {

    LocalDateTime time = LocalDateTime.now();
    GpsData GPSDATA_0 = GpsData.of(0, 0, 1, time);
    GpsData GPSDATA_1 = GpsData.of(0.00001, 0.00001, 1, time.plusSeconds(1));
    GpsData GPSDATA_2 = GpsData.of(0.00002, 0.00002, 1, time.plusSeconds(2));
    GpsData GPSDATA_3 = GpsData.of(0.0001, 0.0001, 1, time.plusSeconds(1));

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

        RunnerRecord runnerRecord = new RunnerRecord(GPSDATA_0, 1);

        @Test
        @DisplayName("거리 차이를 통해 새로운 기록을 생성한다.")
        void createNewRecord() {
            Optional<RunnerRecord> newRecord = runnerRecord.createNextRecord(GPSDATA_1);

            assertAll(
                    () -> assertThat(newRecord).isNotEmpty(),
                    () ->  assertThat(newRecord.get().getDistance()).isGreaterThan(runnerRecord.getDistance())

            );
        }

        @Test
        @DisplayName("같은 시각의 GPS Data가 이미 존재하는 경우, 기록으로 추가하지 않는다.")
        void cannotCreateNewRecord() {
            Optional<RunnerRecord> newRecord = runnerRecord.createNextRecord(GPSDATA_0);

            assertThat(newRecord).isEmpty();
        }

        @Test
        @DisplayName("제한 속력을 넘어가면, 기록으로 추가하지 않는다.")
        void invalidSpeed() {
            Optional<RunnerRecord> newRecord = runnerRecord.createNextRecord(GPSDATA_3);
            assertThat(newRecord).isEmpty();
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
