package online.partyrun.partyrunbattleservice.domain.battle.service;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyBattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("BattleService")
class BattleServiceTest {

    @Autowired BattleService battleService;

    @Autowired RunnerRepository runnerRepository;
    @Autowired BattleRepository battleRepository;

    @Autowired MongoTemplate mongoTemplate;

    @AfterEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_생성할_때 {

        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Runner 박현준 = runnerRepository.save(new Runner("박현준"));
        Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));
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
            Runner 장세연 = runnerRepository.save(new Runner("장세연"));
            Runner 이승열 = runnerRepository.save(new Runner("이승열"));

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

        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
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
            Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));

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
        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우)));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀과_러너의_아이디를_받으면 {

            @Test
            @DisplayName("러너의 상태를 변경한다.")
            void changeRunnerStatus() {
                battleService.setRunnerRunning(배틀.getId(), 박성우.getId());

                assertThat(battleRepository.findById(배틀.getId()).orElseThrow().getRunners().get(0).getStatus())
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
                assertThatThrownBy(() -> battleService.setRunnerRunning(invalidBattleId, 박성우.getId()))
                      .isInstanceOf(BattleNotFoundException.class);
            }
        }
    }
}
