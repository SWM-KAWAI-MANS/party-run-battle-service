package online.partyrun.partyrunbattleservice.domain.battle.service;

import online.partyrun.partyrunbattleservice.domain.battle.config.TestApplicationContextConfig;
import online.partyrun.partyrunbattleservice.domain.battle.config.TestTimeConfig;
import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.battle.event.BattleRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleAlreadyFinishedException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyBattleNotFoundException;
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

    @Autowired
    BattleService battleService;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ApplicationEventPublisher publisher;
    @Autowired
    Clock clock;

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
                        .isInstanceOf(ReadyBattleNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너의_상태를_RUNNING으로_변경할_때 {
        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 노준혁)));

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
        class 배틀의_상태가_이미_끝난_상태라면 {

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                배틀.changeBattleStatus(BattleStatus.FINISHED);
                battleRepository.save(배틀);
                assertThatThrownBy(() -> battleService.setRunnerRunning(배틀.getId(), 박성우.getId()))
                        .isInstanceOf(BattleAlreadyFinishedException.class);
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
    class 배틀의_상태를_RUNNING으로_변경할_떄 {
        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 노준혁)));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀의_id를_받으면 {

            @Test
            @DisplayName("배틀의 상태를 변경한다.")
            void throwException() {
                final BattleStartTimeResponse response = battleService.setBattleRunning(배틀.getId());
                final LocalDateTime startTime = LocalDateTime.now(clock).plusSeconds(5);

                final Battle 조회된_배틀 = battleRepository.findById(배틀.getId()).orElseThrow();

                assertAll(
                        () -> assertThat(response.getStartTime()).isEqualTo(startTime),
                        () -> assertThat(조회된_배틀.getStatus()).isEqualTo(BattleStatus.RUNNING));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀이_존재하지_않으면 {

            String invalidBattleId = "invalidBattleId";

            @Test
            @DisplayName("예외를 던진다.")
            void throwException() {
                assertThatThrownBy(() -> battleService.setBattleRunning(invalidBattleId))
                        .isInstanceOf(BattleNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 거리를_계산할_때 {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        GpsRequest GPS_REQUEST1 = new GpsRequest(1, 1, 1, now);
        GpsRequest GPS_REQUEST2 = new GpsRequest(2, 2, 2, now.plusSeconds(1));
        GpsRequest GPS_REQUEST3 = new GpsRequest(3, 3, 3, now.plusSeconds(2));
        RunnerRecordRequest RECORD_REQUEST1 = new RunnerRecordRequest(List.of(GPS_REQUEST1, GPS_REQUEST2, GPS_REQUEST3));
        Runner 박성우 = new Runner("박성우");
        Battle 진행중인_배틀;

        @BeforeEach
        void setUp() {
            Battle 배틀 = new Battle(1000, List.of(박성우));
            배틀.changeRunnerStatus(박성우.getId(), RunnerStatus.RUNNING);
            배틀.changeBattleStatus(BattleStatus.RUNNING);
            배틀.setStartTime(now.minusSeconds(2), now.minusSeconds(1));

            진행중인_배틀 = mongoTemplate.save(배틀);
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 기존에_기록이_존재하지_않으면 {

            @Test
            @DisplayName("새로운 기록을 저장한다")
            void createNewRecord() {
                RunnerDistanceResponse response = battleService.calculateDistance(진행중인_배틀.getId(), 박성우.getId(), RECORD_REQUEST1);
                assertAll(
                        () -> assertThat(response.runnerId()).isEqualTo(박성우.getId()),
                        () -> assertThat(response.distance()).isPositive()
                );
            }
        }

        @Test
        @DisplayName("배틀을 조회하지 못하면 예외를 던진다.")
        void throwException() {
            assertThatThrownBy(() -> battleService.calculateDistance("invalidBattleId", 박성우.getId(), RECORD_REQUEST1))
                    .isInstanceOf(BattleNotFoundException.class);
        }
    }
}
