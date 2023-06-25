package online.partyrun.partyrunbattleservice.domain.runner.service;

import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("RunnerService")
class RunnerServiceTest {

    @Autowired
    RunnerService runnerService;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들의_id를_요청하면 {

        @Test
        @DisplayName("러너들을 반환한다.")
        void returnRunners() {
            final List<Runner> runners = runnerService.findByIds(List.of("1", "2", "3"));
            assertThat(runners).hasSize(3);
        }
    }

}