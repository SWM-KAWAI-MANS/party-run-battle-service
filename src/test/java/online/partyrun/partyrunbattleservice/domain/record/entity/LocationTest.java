package online.partyrun.partyrunbattleservice.domain.record.entity;

import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.LOCATION1;
import static online.partyrun.partyrunbattleservice.fixture.record.RecordFixture.LOCATION2;

import static org.assertj.core.api.Assertions.*;

import online.partyrun.partyrunbattleservice.domain.record.exception.DistanceCalculatorEmptyException;
import online.partyrun.partyrunbattleservice.domain.record.exception.IllegalLocationException;
import online.partyrun.partyrunbattleservice.domain.record.exception.LocationEmptyException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Location")
class LocationTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Location을_생성할_때 {

        @ParameterizedTest
        @CsvSource(value = {"181,0,0", "-181,0,0", "0,91,0", "0,-91,0", "0,0,-1", "0,0,50001"})
        @DisplayName("잘못된 위치정보가 들어오면 예외를 던진다.")
        void notCreatLocation(double longitude, double latitude, double altitude) {
            assertThatThrownBy(() -> Location.of(longitude, latitude, altitude))
                    .isInstanceOf(IllegalLocationException.class);
        }

        @ParameterizedTest
        @CsvSource(value = {"180,0,0", "-180,0,0", "0,90,0", "0,-90,0", "0,0,0", "0,0,50000"})
        @DisplayName("위치정보를 통해 생성한다.")
        void creatLocation(double longitude, double latitude, double altitude) {
            assertThatCode(() -> Location.of(longitude, latitude, altitude))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 다른_위치와의_거리를_구할_때 {

        @Test
        @DisplayName("잘못된 위치가 들어오면 예외를 던진다.")
        void throwLocationException() {
            assertThatThrownBy(() -> LOCATION1.calculateDistance(null, (point1, point2) -> 1d))
                    .isInstanceOf(LocationEmptyException.class);
        }

        @Test
        @DisplayName("잘못된 계산 구현체가 들어오면 예외를 던진다.")
        void throwCalculatorException() {
            assertThatThrownBy(() -> LOCATION1.calculateDistance(LOCATION2, null))
                    .isInstanceOf(DistanceCalculatorEmptyException.class);
        }

        @Test
        @DisplayName("계산 구현체에 맞게 계산한다.")
        void calculateDistance() {
            double expected = 1;
            final double actual =
                    LOCATION1.calculateDistance(LOCATION2, (point1, point2) -> expected);

            assertThat(actual).isEqualTo(expected);
        }
    }
}
