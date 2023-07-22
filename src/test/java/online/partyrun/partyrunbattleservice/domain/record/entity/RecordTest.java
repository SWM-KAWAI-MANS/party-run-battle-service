package online.partyrun.partyrunbattleservice.domain.record.entity;

import online.partyrun.partyrunbattleservice.domain.record.exception.IllegalRecordDistanceException;
import org.junit.jupiter.api.*;

import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Record")
class RecordTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Record_생성_시 {

        @Test
        @DisplayName("거리가 0 미만이면 예외를 던진다.")
        void throwDistanceException() {
            assertThatThrownBy(() -> new Record(GPSDATA_1, -1))
                    .isInstanceOf(IllegalRecordDistanceException.class);
        }

        @Test
        @DisplayName("GpsData가 null이면 예외를 던진다.")
        void throwGpsDataException() {
            assertThatThrownBy(() -> new Record(null, 1))
                    .isInstanceOf(InvalidGpsDataException.class);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 현재_기록으로부터_새로운_기록_생성_시 {

        @Test
        @DisplayName("거리 차이를 통해 새로운 기록을 생성한다.")
        void createNewRecord() {
            Record actual = RECORD_1.createNewRecord(GPSDATA_2);

            assertThat(actual.getDistance()).isGreaterThan(RECORD_1.getDistance());
        }
    }
}