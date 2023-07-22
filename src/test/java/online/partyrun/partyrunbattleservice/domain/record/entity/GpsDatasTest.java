package online.partyrun.partyrunbattleservice.domain.record.entity;

import online.partyrun.partyrunbattleservice.domain.record.exception.GpsDatasEmptyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;
import java.util.List;

import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GpsDatas")
class GpsDatasTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GpsDatas를_생성할_때 {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("빈 리스트를 받으면 예외를 던진다.")
        void throwException(List<GpsData> emptyGpsData) {
            assertThatThrownBy(() -> new GpsDatas(emptyGpsData))
                    .isInstanceOf(GpsDatasEmptyException.class);
        }

        @Test
        @DisplayName("GpsData들을 받으면 정렬하여 생성한다")
        void createGpsDatas() {
            LocalDateTime minTime = LocalDateTime.now();
            final GpsData gpsData1 = GpsData.of(1, 1, 1, minTime.plusSeconds(3), minTime);
            final GpsData gpsData2 = GpsData.of(1, 1, 1, minTime.plusSeconds(2), minTime);
            final GpsData gpsData3 = GpsData.of(1, 1, 1, minTime.plusSeconds(1), minTime);

            final GpsDatas gpsDatas = new GpsDatas(List.of(gpsData1, gpsData2, gpsData3));

            assertThat(gpsDatas.getGpsDatas()).usingRecursiveComparison().isEqualTo(List.of(gpsData3, gpsData2, gpsData1));
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 기록을_생성할_때 {

        @Test
        @DisplayName("기존에 존재하던 기록을 통해 새로운 기록을 만든다.")
        void createRecords() {

            List<Record> actual = GPSDATAS_1.createRecords(RECORD_4);
            double distance1 = GPSDATA_4.calculateDistance(GPSDATA_1);
            double distance2 = GPSDATA_1.calculateDistance(GPSDATA_2);
            double distance3 = GPSDATA_2.calculateDistance(GPSDATA_3);

            Assertions.assertAll(
                    () -> assertThat(actual).hasSize(3),
                    () -> assertThat(actual.get(actual.size() - 1).getDistance())
                            .isEqualTo(RECORD_4.getDistance() + distance1 + distance2 + distance3)
            );
        }

        @Test
        @DisplayName("기존에 존재하던 기록이 없을 때 새로운 기록을 만든다.")
        void createNewRecords() {

            List<Record> actual = GPSDATAS_1.createNewRecords();
            double distance1 = GPSDATA_1.calculateDistance(GPSDATA_2);
            double distance2 = GPSDATA_2.calculateDistance(GPSDATA_3);

            Assertions.assertAll(
                    () -> assertThat(actual).hasSize(3),
                    () -> assertThat(actual.get(actual.size() - 1).getDistance())
                            .isEqualTo(distance1 + distance2)
            );
        }
    }
}