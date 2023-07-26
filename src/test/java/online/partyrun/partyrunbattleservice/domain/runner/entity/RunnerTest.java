package online.partyrun.partyrunbattleservice.domain.runner.entity;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRecentRunnerRecordException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerAlreadyFinishedException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerIsNotRunningException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerStatusCannotBeChangedException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Runner")
class RunnerTest {

    Runner 박성우;

    @BeforeEach
    void setUp() {
        박성우 = new Runner("박성우");
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_변경할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_정상적이라면 {
            RunnerStatus status = RunnerStatus.RUNNING;

            @Test
            @DisplayName("상태를 변경한다")
            void changeStatus() {
                박성우.changeStatus(status);
                assertThat(박성우.getStatus()).isEqualTo(status);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 현재_상태가_FINISHED라면 {
            RunnerStatus status = RunnerStatus.RUNNING;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                박성우.changeStatus(RunnerStatus.FINISHED);
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerAlreadyFinishedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_READY라면 {
            RunnerStatus status = RunnerStatus.READY;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_null라면 {
            RunnerStatus status = null;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 변경할_상태가_이전_상태와_같다면 {
            RunnerStatus status = RunnerStatus.RUNNING;

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                박성우.changeStatus(status);
                assertThatThrownBy(() -> 박성우.changeStatus(status))
                        .isInstanceOf(RunnerStatusCannotBeChangedException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_id가_같은지_확인할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class id가_같다면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                String id = 박성우.getId();
                assertThat(박성우.hasId(id)).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class id가_다르다면 {
            String id = "not equal id";

            @Test
            @DisplayName("false를 반환한다.")
            void returnTrue() {
                assertThat(박성우.hasId(id)).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태가_RUNNING인지_확인할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING_이라면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeStatus(RunnerStatus.RUNNING);

                assertThat(박성우.isRunning()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING이_아니라면 {

            @Test
            @DisplayName("false를 반환한다.")
            void returnFalse() {
                박성우.changeStatus(RunnerStatus.FINISHED);

                assertThat(박성우.isRunning()).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 새로운_기록을_만들_때 {
        LocalDateTime time = LocalDateTime.now();
        GpsData GPSDATA_1 = GpsData.of(1, 1, 1, time);
        GpsData GPSDATA_2 = GpsData.of(2, 2, 2, time.plusSeconds(1));
        GpsData GPSDATA_3 = GpsData.of(3, 3, 3, time.plusSeconds(2));

        @Test
        @DisplayName("러너가 RUNNING 상태가 아니면 예외를 던진다.")
        void throwException() {
            List<GpsData> gpsData = List.of(GPSDATA_1, GPSDATA_2, GPSDATA_3);
            assertThatThrownBy(() -> 박성우.createNewRecords(gpsData))
                    .isInstanceOf(RunnerIsNotRunningException.class);
        }

        @Test
        @DisplayName("최근 기록이 없으면 새롭게 생성한다.")
        void createRecordWithoutRecentRecord() {
            박성우.changeStatus(RunnerStatus.RUNNING);

            List<GpsData> gpsData = List.of(GPSDATA_1, GPSDATA_2, GPSDATA_3);
            박성우.createNewRecords(gpsData);

            assertAll(
                    () -> assertThat(박성우.getRecentRunnerRecord().getGpsData()).isEqualTo(GPSDATA_3),
                    () -> assertThat(박성우.getRunnerRecords().stream().map(RunnerRecord::getGpsData).toList()).isEqualTo(gpsData),
                    () -> assertThat(박성우.getRunnerRecords().get(0).getDistance()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("최근 기록이 있으면 최근 기록에 이어서 새롭게 생성한다.")
        void createRecordWithRecentRecord() {
            박성우.changeStatus(RunnerStatus.RUNNING);

            List<GpsData> recentGpsData = List.of(GPSDATA_1);
            박성우.createNewRecords(recentGpsData);

            List<GpsData> newGpsData = List.of(GPSDATA_2, GPSDATA_3);
            박성우.createNewRecords(newGpsData);

            assertAll(
                    () -> assertThat(박성우.getRecentRunnerRecord().getGpsData()).isEqualTo(GPSDATA_3),
                    () -> assertThat(박성우.getRunnerRecords().stream().map(RunnerRecord::getGpsData).skip(1).toList()).isEqualTo(newGpsData),
                    () -> assertThat(박성우.getRunnerRecords().get(1).getDistance()).isNotEqualTo(0)
            );
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_최근_거리를_가져올_때 {

        @Test
        @DisplayName("최신 기록이 없으면 예외를 던진다.")
        void throwException() {
            assertThatThrownBy(() -> 박성우.getRecentDistance())
                    .isInstanceOf(InvalidRecentRunnerRecordException.class);
        }

        @Test
        @DisplayName("최신 기록이 있으면 최신 거리를 조회한다.")
        void returnDistance() {
            박성우.changeStatus(RunnerStatus.RUNNING);
            GpsData GPSDATA_1 = GpsData.of(1, 1, 1, LocalDateTime.now());
            GpsData GPSDATA_2 = GpsData.of(2, 2, 2, LocalDateTime.now().plusSeconds(1));

            List<GpsData> recentGpsData = List.of(GPSDATA_1, GPSDATA_2);

            박성우.createNewRecords(recentGpsData);

            assertThat(박성우.getRecentDistance()).isPositive();
        }
    }
}