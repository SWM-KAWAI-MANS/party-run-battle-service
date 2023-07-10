package online.partyrun.partyrunbattleservice.domain.runner.service;

import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("RunnerService")
class RunnerServiceTest {

    @Autowired RunnerService runnerService;
    @Autowired MemberRepository memberRepository;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들의_id를_요청했을_때 {

        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Runner 박현준 = new Runner(memberRepository.save(멤버_박현준).getId());

        @Test
        @DisplayName("러너들을 반환한다.")
        void returnRunners() {
            final List<Runner> runners =
                    runnerService.findAllById(List.of(박성우.getId(), 노준혁.getId(), 박현준.getId()));
            assertThat(runners).hasSize(3);
        }
    }
}
