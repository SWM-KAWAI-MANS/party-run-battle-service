package online.partyrun.partyrunbattleservice.domain.runner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRunnerException;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@DisplayName("RunnerService")
class RunnerServiceTest {

    @Autowired RunnerService runnerService;
    @Autowired RunnerRepository runnerRepository;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들의_id를_요청했을_때 {

        Runner 박성우 = runnerRepository.save(new Runner("1"));
        Runner 노준혁 = runnerRepository.save(new Runner("2"));
        Runner 박현준 = runnerRepository.save(new Runner("3"));

        @Test
        @DisplayName("러너들을 반환한다.")
        void returnRunners() {
            final List<Runner> runners =
                    runnerService.findAllById(List.of(박성우.getId(), 노준혁.getId(), 박현준.getId()));
            assertThat(runners).hasSize(3);
        }

        @Test
        @DisplayName("존재하지 않는 러너라면 예외를 던진다.")
        void throwException() {
            assertThatThrownBy(() -> runnerService.findAllById(List.of(박성우.getId(), "invalid_id")))
                    .isInstanceOf(InvalidRunnerException.class);
        }
    }
}
