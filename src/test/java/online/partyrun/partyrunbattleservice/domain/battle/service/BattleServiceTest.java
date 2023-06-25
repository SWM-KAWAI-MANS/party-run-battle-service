package online.partyrun.partyrunbattleservice.domain.battle.service;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerResponse;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.service.RunnerService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@DisplayName("BattleService")
class BattleServiceTest {

    @Autowired
    BattleService battleService;

    @MockBean
    RunnerService runnerService;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀에_참가하는_러너들의_id가_주어지면 {
        BattleCreateRequest request = new BattleCreateRequest(List.of("1", "2", "3"));

        @Test
        @DisplayName("생성된 방의 정보를 반환한다.")
        void returnBattle() {
            given(runnerService.findByIds(request.getRunnerIds())).willReturn(
                    List.of(new Runner("1", "박성우"), new Runner("2", "노준혁"), new Runner("3", "박현준"))
            );

            final BattleResponse response = battleService.createBattle(request);
            assertAll(
                    () -> assertThat(response.id()).isNotNull(),
                    () -> assertThat(response.runners())
                            .isEqualTo(List.of(new RunnerResponse("1", "박성우"), new RunnerResponse("2", "노준혁"), new RunnerResponse("3", "박현준"))))
            ;
        }
    }
}