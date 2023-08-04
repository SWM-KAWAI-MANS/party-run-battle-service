package online.partyrun.partyrunbattleservice.domain.battle.service;

import online.partyrun.partyrunbattleservice.domain.battle.config.TestApplicationContextConfig;
import online.partyrun.partyrunbattleservice.domain.battle.config.TestTimeConfig;
import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerFinishedEvent;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyRunnerNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@SpringBootTest
@Import({TestApplicationContextConfig.class, TestTimeConfig.class})
@DisplayName("BattleService")
class BattleServiceTest {

    @Autowired BattleService battleService;
    @Autowired BattleRepository battleRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired MongoTemplate mongoTemplate;
    @Autowired ApplicationEventPublisher publisher;
    @Autowired Clock clock;
    LocalDateTime now;

    @BeforeEach
    void setUpNow() {
        now = LocalDateTime.now(clock);
    }

    @AfterEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_생성할_때 {

        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Runner 박현준 = new Runner(memberRepository.save(멤버_박현준).getId());
        BattleCreateRequest request =
                new BattleCreateRequest(1000, List.of(박성우.getId(), 박현준.getId(), 노준혁.getId()));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 현재_배틀에_참여하고_있는_러너가_없다면 {
            @Test
            @DisplayName(" 생성된 배틀의 정보를 반환한다.")
            void returnBattle() {
                final BattleResponse response = battleService.createBattle(request);
                assertThat(response.id()).isNotNull();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너들이_현재_다른_배틀에_모두_참여하고_있다면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwExceptionsByAllRunner() {
                battleService.createBattle(request);

                assertThatThrownBy(() -> battleService.createBattle(request))
                        .isInstanceOf(RunnerAlreadyRunningInBattleException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 한명이라도_다른_배틀에_참여하고_있는_러너가_있다면 {
            Runner 장세연 = new Runner(memberRepository.save(멤버_장세연).getId());
            Runner 이승열 = new Runner(memberRepository.save(멤버_이승열).getId());

            @Test
            @DisplayName("예외를 던진다.")
            void throwExceptionsByOneRunner() {

                battleService.createBattle(request);

                assertThatThrownBy(
                                () ->
                                        battleService.createBattle(
                                                new BattleCreateRequest(
                                                        1000,
                                                        List.of(
                                                                박성우.getId(),
                                                                장세연.getId(),
                                                                이승열.getId()))))
                        .isInstanceOf(RunnerAlreadyRunningInBattleException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_조회할_때 {

        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        BattleCreateRequest request = new BattleCreateRequest(1000, List.of(박성우.getId()));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class runner의_id가_주어지면 {

            @Test
            @DisplayName("진행중인 battle 정보 반환한다.")
            void returnBattleId() {
                battleService.createBattle(request);

                final BattleResponse response = battleService.getReadyBattle(박성우.getId());
                assertThat(response.id()).isNotNull();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 현재_배틀에_참여하고있지_않는_runner의_id가_주어지면 {
            Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(() -> battleService.getReadyBattle(노준혁.getId()))
                        .isInstanceOf(ReadyRunnerNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_RUNNING으로_변경할_때 {

        Runner 박성우;
        Runner 노준혁;
        Battle 배틀;

        @BeforeEach
        void setUp() {
            박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
            노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
            배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 노준혁), now));
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀과_러너의_아이디를_받으면 {

            @Test
            @DisplayName("러너의 상태를 변경한다.")
            void changeRunnerStatus() {
                battleService.setRunnerRunning(배틀.getId(), 박성우.getId());

                assertThat(
                                battleRepository
                                        .findById(배틀.getId())
                                        .orElseThrow()
                                        .getRunners()
                                        .get(0)
                                        .getStatus())
                        .isEqualTo(RunnerStatus.RUNNING);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_존재하지_않으면 {

            String invalidBattleId = "invalidBattleId";

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(
                                () -> battleService.setRunnerRunning(invalidBattleId, 박성우.getId()))
                        .isInstanceOf(BattleNotFoundException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모든_러너의_상태가_변경되었다면 {

            @Test
            @DisplayName("BattleRunningEvent을 publish한다.")
            void publishEvent() {
                battleService.setRunnerRunning(배틀.getId(), 박성우.getId());
                battleService.setRunnerRunning(배틀.getId(), 노준혁.getId());
                then(publisher).should(times(1)).publishEvent(new BattleRunningEvent(배틀.getId()));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모든_러너의_상태가_변경되지_않았다면 {

            @Test
            @DisplayName("BattleRunningEvent을 publish 하지 않는다.")
            void notPublish() {
                battleService.setRunnerRunning(배틀.getId(), 노준혁.getId());
                then(publisher).should(never()).publishEvent(any(BattleRunningEvent.class));
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_시작할_때 {
        Runner 박성우;
        Runner 노준혁;
        Battle 배틀;

        @BeforeEach
        void setUp() {
            박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
            노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
            Battle battle = new Battle(1000, List.of(박성우, 노준혁), now);
            박성우.changeRunningStatus();
            노준혁.changeRunningStatus();
            배틀 = battleRepository.save(battle);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀의_id를_받으면 {

            @Test
            @DisplayName("시작 시간을 설정한다.")
            void changeBattleStatus() {
                final BattleStartedResponse response = battleService.start(배틀.getId());
                final LocalDateTime startTime = LocalDateTime.now(clock).plusSeconds(5);

                assertThat((LocalDateTime) response.getData().get("startTime"))
                        .isEqualTo(startTime);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_존재하지_않으면 {

            String invalidBattleId = "invalidBattleId";

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(() -> battleService.start(invalidBattleId))
                        .isInstanceOf(BattleNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 거리를_계산할_때 {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        GpsRequest GPS_REQUEST0 = new GpsRequest(0, 0, 0, now);
        GpsRequest GPS_REQUEST1 = new GpsRequest(0.001, 0.001, 0.001, now);
        GpsRequest GPS_REQUEST2 = new GpsRequest(0.002, 0.002, 0.002, now.plusSeconds(1));
        GpsRequest GPS_REQUEST3 = new GpsRequest(0.003, 0.003, 0.003, now.plusSeconds(2));
        GpsRequest GPS_REQUEST4 = new GpsRequest(0.004, 0.004, 0.004, now.plusSeconds(3));
        GpsRequest GPS_REQUEST5 = new GpsRequest(0.005, 0.005, 0.005, now.plusSeconds(4));
        GpsRequest GPS_REQUEST6 = new GpsRequest(0.006, 0.006, 0.006, now.plusSeconds(5));
        GpsRequest GPS_REQUEST7 = new GpsRequest(0.007, 0.007, 0.007, now.plusSeconds(6));
        RunnerRecordRequest RECORD_REQUEST1 =
                new RunnerRecordRequest(
                        List.of(GPS_REQUEST0, GPS_REQUEST1, GPS_REQUEST2, GPS_REQUEST3));
        RunnerRecordRequest RECORD_REQUEST2 =
                new RunnerRecordRequest(
                        List.of(GPS_REQUEST4, GPS_REQUEST5, GPS_REQUEST6, GPS_REQUEST7));
        Runner 박성우 = new Runner("박성우");
        Battle 진행중인_배틀;

        @BeforeEach
        void setUp() {
            Battle 배틀 = new Battle(1000, List.of(박성우), now);
            배틀.changeRunnerRunningStatus(박성우.getId());
            배틀.setStartTime(now.minusMinutes(1));

            진행중인_배틀 = mongoTemplate.save(배틀);
        }

        @Test
        @DisplayName("기존에 기록이 존재하지 않으면 새로운 기록을 저장한다")
        void createNewRecord() {
            RunnerDistanceResponse response =
                    battleService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST1);
            assertAll(
                    () -> assertThat(response.getData().get("runnerId")).isEqualTo(박성우.getId()),
                    () -> assertThat((double) response.getData().get("distance")).isPositive());
        }

        @Test
        @DisplayName("기존에 기록이 존재하면 이어서 저장한다")
        void createRecord() {
            final RunnerDistanceResponse response1 =
                    battleService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST1);
            final RunnerDistanceResponse response2 =
                    battleService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST2);
            assertThat((double) response2.getData().get("distance"))
                    .isGreaterThan((double) response1.getData().get("distance"));
        }

        @Test
        @DisplayName("배틀을 조회하지 못하면 예외를 던진다.")
        void throwException() {
            assertThatThrownBy(
                            () ->
                                    battleService.calculateDistance(
                                            "invalidBattleId", 박성우.getId(), RECORD_REQUEST1))
                    .isInstanceOf(BattleNotFoundException.class);
        }

        @Test
        @DisplayName("러너가 종료 상태라면 이벤트를 던진다.")
        void publishRunnerFinishEvent() {
            battleService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST1);
            battleService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST2);
            then(publisher)
                    .should(times(1))
                    .publishEvent(new RunnerFinishedEvent(진행중인_배틀.getId(), 박성우.getId()));
        }
    }
}
