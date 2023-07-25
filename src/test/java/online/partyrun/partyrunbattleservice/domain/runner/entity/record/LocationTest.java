package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import online.partyrun.partyrunbattleservice.domain.battle.exception.IllegalLocationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}