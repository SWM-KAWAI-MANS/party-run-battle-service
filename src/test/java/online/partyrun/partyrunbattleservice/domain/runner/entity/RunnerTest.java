package online.partyrun.partyrunbattleservice.domain.runner.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRecentRunnerRecordException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerIsNotFinisedException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerIsNotReadyException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerIsNotRunningException;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DisplayName("Runner")
class RunnerTest {

    Runner 박성우;

    @BeforeEach
    void setUp() {
        박성우 = new Runner("박성우");
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너를_RUNNING_상태로_변경할_때 {

        @Test
        @DisplayName("상태를 변경한다")
        void changeStatus() {
            박성우.changeRunningStatus();
            assertThat(박성우.getStatus()).isEqualTo(RunnerStatus.RUNNING);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class READY_상태가_아니라면 {

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                박성우.changeRunningStatus();
                assertThatThrownBy(() -> 박성우.changeRunningStatus())
                        .isInstanceOf(RunnerIsNotReadyException.class);
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
                박성우.changeRunningStatus();

                assertThat(박성우.isRunning()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class RUNNING이_아니라면 {

            @Test
            @DisplayName("false를 반환한다.")
            void returnFalse() {
                assertThat(박성우.isRunning()).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태가_FINISHED인지_확인할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class FINISHED_이라면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeRunningStatus();
                박성우.changeFinishStatus();

                assertThat(박성우.isFinished()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class FINISHED가_아니라면 {

            @Test
            @DisplayName("false를 반환한다.")
            void returnFalse() {
                assertThat(박성우.isFinished()).isFalse();
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
            assertThatThrownBy(() -> 박성우.addRecords(gpsData))
                    .isInstanceOf(RunnerIsNotRunningException.class);
        }

        @Test
        @DisplayName("최근 기록이 없으면 새롭게 생성한다.")
        void createRecordWithoutRecentRecord() {
            박성우.changeRunningStatus();

            List<GpsData> gpsData = List.of(GPSDATA_1, GPSDATA_2, GPSDATA_3);
            박성우.addRecords(gpsData);

            assertAll(
                    () -> assertThat(박성우.getRecentRunnerRecord().getGpsData()).isEqualTo(GPSDATA_3),
                    () ->
                            assertThat(
                                            박성우.getRunnerRecords().stream()
                                                    .map(RunnerRecord::getGpsData)
                                                    .toList())
                                    .isEqualTo(gpsData),
                    () -> assertThat(박성우.getRunnerRecords().get(0).getDistance()).isEqualTo(0));
        }

        @Test
        @DisplayName("최근 기록이 있으면 최근 기록에 이어서 새롭게 생성한다.")
        void createRecordWithRecentRecord() {
            박성우.changeRunningStatus();

            List<GpsData> recentGpsData = List.of(GPSDATA_1);
            박성우.addRecords(recentGpsData);

            List<GpsData> newGpsData = List.of(GPSDATA_2, GPSDATA_3);
            박성우.addRecords(newGpsData);

            assertAll(
                    () -> assertThat(박성우.getRecentRunnerRecord().getGpsData()).isEqualTo(GPSDATA_3),
                    () ->
                            assertThat(
                                            박성우.getRunnerRecords().stream()
                                                    .map(RunnerRecord::getGpsData)
                                                    .skip(1)
                                                    .toList())
                                    .isEqualTo(newGpsData),
                    () -> assertThat(박성우.getRunnerRecords().get(1).getDistance()).isNotEqualTo(0));
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
            박성우.changeRunningStatus();
            GpsData GPSDATA_1 = GpsData.of(1, 1, 1, LocalDateTime.now());
            GpsData GPSDATA_2 = GpsData.of(2, 2, 2, LocalDateTime.now().plusSeconds(1));

            List<GpsData> recentGpsData = List.of(GPSDATA_1, GPSDATA_2);

            박성우.addRecords(recentGpsData);

            assertThat(박성우.getRecentDistance()).isPositive();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_종료시간을_가져올_때 {

        @Test
        @DisplayName("러너가 종료상태가 아니라면 예외를 던진다.")
        void throwException() {
            assertThatThrownBy(() -> 박성우.getEndTime())
                    .isInstanceOf(RunnerIsNotFinisedException.class);
        }

        @Test
        @DisplayName("종료 시간을 반환한다.")
        void returnEndTime() {
            박성우.changeRunningStatus();
            LocalDateTime now = LocalDateTime.now();
            GpsData GPSDATA_1 = GpsData.of(1, 1, 1, now);
            GpsData GPSDATA_2 = GpsData.of(2, 2, 2, now.plusSeconds(1));

            List<GpsData> recentGpsData = List.of(GPSDATA_1, GPSDATA_2);

            박성우.addRecords(recentGpsData);
            박성우.changeFinishStatus();

            assertThat(박성우.getEndTime()).isEqualTo(now.plusSeconds(1));
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들_끼리_비교를_할_때 {

        Runner 노준혁;

        @BeforeEach
        void setUp() {
            LocalDateTime now = LocalDateTime.now();

            박성우.changeRunningStatus();
            GpsData GPSDATA_1 = GpsData.of(1, 1, 1, now);

            박성우.addRecords(List.of(GPSDATA_1));

            노준혁 = new Runner("노준혁");
            노준혁.changeRunningStatus();
            GpsData GPSDATA_2 = GpsData.of(2, 2, 2, now.plusSeconds(1));

            노준혁.addRecords(List.of(GPSDATA_2));
        }

        @Test
        @DisplayName("최근 기록이 더 이전일 수록 작다.")
        void compareTo() {
            assertThat(박성우.compareTo(노준혁)).isNegative();
        }

        @Test
        @DisplayName("러너들을 정렬 할 때, 최근 기록 시간이 더 작을 수록 앞에 온다.")
        void sort() {
            final List<Runner> runners = new ArrayList<>(List.of(노준혁, 박성우));

            Collections.sort(runners);

            assertThat(runners).containsExactly(박성우, 노준혁);
        }
    }
}
