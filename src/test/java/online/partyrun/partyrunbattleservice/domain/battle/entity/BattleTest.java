package online.partyrun.partyrunbattleservice.domain.battle.entity;

import online.partyrun.partyrunbattleservice.domain.battle.exception.*;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataTimeException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Battle")
class BattleTest {

    Runner 박성우;
    Runner 노준혁;
    Runner 박현준;
    Battle 배틀;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_생성할_때 {
        List<Runner> runnerList;
        int distance = 1000;

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            노준혁 = new Runner("노준혁");
            박현준 = new Runner("박현준");
            runnerList = List.of(박성우, 노준혁, 박현준);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 거리와_러너의_수가_올바르다면 {

            @Test
            @DisplayName("배틀을 생성한다.")
            void createBattle() {
                assertThatCode(() -> new Battle(distance, runnerList)).doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 거리가_1_미만이라면 {

            @ParameterizedTest
            @ValueSource(ints = {-1, 0})
            @DisplayName("예외를 던진다.")
            void throwException(int distance) {
                assertThatThrownBy(() -> new Battle(distance, runnerList))
                        .isInstanceOf(InvalidDistanceException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너가_없다면 {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("예외를 던진다.")
            void throwException(List<Runner> runners) {
                assertThatThrownBy(() -> new Battle(distance, runners))
                        .isInstanceOf(InvalidRunnerNumberInBattleException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너를_Running_상태로_변경할_때 {

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            노준혁 = new Runner("노준혁");
            박현준 = new Runner("박현준");
            배틀 = new Battle(1000, List.of(박성우, 박현준));
        }

        @Test
        @DisplayName("러너의 상태를 변경한다.")
        void changeRunnerStatus() {
            배틀.changeRunnerRunningStatus(박성우.getId());
            assertThat(박성우.getStatus()).isEqualTo(RunnerStatus.RUNNING);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀_내에_러너가_존재하지_않으면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(() -> 배틀.changeRunnerRunningStatus(노준혁.getId()))
                        .isInstanceOf(RunnerNotFoundException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_RUNNING_상태라면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                박성우.changeRunningStatus();
                박현준.changeRunningStatus();
                배틀.changeBattleRunning(LocalDateTime.now());

                assertThatThrownBy(() -> 배틀.changeRunnerRunningStatus(박성우.getId()))
                        .isInstanceOf(BattleAlreadyRunningException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_FINISHED_상태라면 {
            @Disabled
            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_Running_상태로_변경할_때 {

        LocalDateTime now;

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            배틀 = new Battle(1000, List.of(박성우));
            now = LocalDateTime.now();
        }

        @Test
        @DisplayName("배틀의 상태를 변경한다.")
        void changeBattleStatus() {
            박성우.changeRunningStatus();
            배틀.changeBattleRunning(now);
            assertThat(배틀.getStatus()).isEqualTo(BattleStatus.RUNNING);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 이미_Running_상태라면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                박성우.changeRunningStatus();

                배틀.changeBattleRunning(now);
                assertThatThrownBy(() -> 배틀.changeBattleRunning(now))
                        .isInstanceOf(BattleAlreadyRunningException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_FINISHED_상태라면 {

            @Disabled
            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모든_러너가_RUNNING_상태가_아니라면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(() -> 배틀.changeBattleRunning(now))
                        .isInstanceOf(AllRunnersAreNotRunningStatusException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_찾을_떄 {

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            배틀 = new Battle(1000, List.of(박성우));
        }

        @Test
        @DisplayName("러너의 상태를 반환한다.")
        void returnRunnerStatus() {
            RunnerStatus runnerStatus = 배틀.getRunnerStatus(박성우.getId());

            assertThat(runnerStatus).isEqualTo(박성우.getStatus());
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하지_않는_러너라면 {

            String invalidRunnerId = "invalidRunnerId";

            @Test
            @DisplayName("예외를 던진다")
            void throwException() {
                assertThatThrownBy(() -> 배틀.getRunnerStatus(invalidRunnerId))
                        .isInstanceOf(RunnerNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀의_러너들이_모두_RUNNING_상태인지_확인할_때 {

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            박현준 = new Runner("박현준");
            배틀 = new Battle(1000, List.of(박성우, 박현준));
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_맞다면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeRunningStatus();
                박현준.changeRunningStatus();

                assertThat(배틀.isAllRunnersRunningStatus()).isTrue();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 한명이라도_아니라면 {

            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                박성우.changeRunningStatus();

                assertThat(배틀.isAllRunnersRunningStatus()).isFalse();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀에_기록을_생성할_때 {

        LocalDateTime battleStartTime;

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            배틀 = new Battle(1000, List.of(박성우));

            박성우.changeRunningStatus();

            battleStartTime = LocalDateTime.now();
            배틀.changeBattleRunning(battleStartTime.minusSeconds(5));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("GpsData가 빈 값이면 예외를 던진다.")
        void throwNullException(List<GpsData> invalidGpsData) {
            assertThatThrownBy(() -> 배틀.addRecords(박성우.getId(), invalidGpsData))
                    .isInstanceOf(InvalidGpsDataException.class);
        }

        @Disabled
        @Test
        @DisplayName("배틀이 이미 종료되었다면 예외를 던진다.")
        void throwBattleAlreadyFinishedException() {
        }

        @Test
        @DisplayName("GpsData가 배틀 시작 시간보다 이전에 생성되었다면 예외를 던진다.")
        void throwGpsDataTimeException() {
            GpsData validGpsData = GpsData.of(1, 1, 1, battleStartTime.plusSeconds(1));
            GpsData invalidGpsData = GpsData.of(1, 1, 1, battleStartTime.minusSeconds(1));
            List<GpsData> gpsData = List.of(validGpsData, invalidGpsData);

            assertThatThrownBy(() -> 배틀.addRecords(박성우.getId(), gpsData))
                    .isInstanceOf(InvalidGpsDataTimeException.class);
        }

        @Test
        @DisplayName("새로운 기록을 만든다.")
        void createNewRecords() {
            GpsData gpsData1 = GpsData.of(1, 1, 1, battleStartTime.plusSeconds(1));
            GpsData gpsData2 = GpsData.of(2, 2, 2, battleStartTime.plusSeconds(2));
            List<GpsData> gpsData = List.of(gpsData1, gpsData2);

            배틀.addRecords(박성우.getId(), gpsData);

            assertThat(박성우.getRunnerRecords()).hasSize(2);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_최신_거리를_가져올_때 {
        LocalDateTime battleStartTime;

        @BeforeEach
        void setUp() {
            박성우 = new Runner("박성우");
            배틀 = new Battle(1000, List.of(박성우));

            박성우.changeRunningStatus();

            battleStartTime = LocalDateTime.now();
            배틀.changeBattleRunning(battleStartTime.minusSeconds(5));

            GpsData gpsData1 = GpsData.of(1, 1, 1, battleStartTime.plusSeconds(1));
            GpsData gpsData2 = GpsData.of(2, 2, 2, battleStartTime.plusSeconds(2));
            List<GpsData> gpsData = List.of(gpsData1, gpsData2);
            배틀.addRecords(박성우.getId(), gpsData);
        }

        @Test
        @DisplayName("최신 기록으로부터 거리를 가져온다.")
        void getDistance() {
            double runnerRecentDistance = 배틀.getRunnerRecentDistance(박성우.getId());
            assertThat(runnerRecentDistance).isPositive();
        }
    }
}
