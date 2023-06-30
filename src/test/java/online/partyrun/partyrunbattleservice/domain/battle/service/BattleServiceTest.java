package online.partyrun.partyrunbattleservice.domain.battle.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunningBattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@SpringBootTest
@DisplayName("BattleService")
class BattleServiceTest {

    @Autowired BattleService battleService;

    @Autowired RunnerRepository runnerRepository;

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
                new BattleCreateRequest(List.of(박성우.getId(), 박현준.getId(), 노준혁.getId()));

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
        BattleCreateRequest request = new BattleCreateRequest(List.of(박성우.getId()));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class runner의_id가_주어지면 {

            @Test
            @DisplayName("진행중인 battle 정보 반환한다.")
            void returnBattleId() {
                battleService.createBattle(request);

                final BattleResponse response = battleService.getRunningBattle(박성우.getId());
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
                assertThatThrownBy(() -> battleService.getRunningBattle(노준혁.getId()))
                        .isInstanceOf(RunningBattleNotFoundException.class);
            }
        }
    }
}
