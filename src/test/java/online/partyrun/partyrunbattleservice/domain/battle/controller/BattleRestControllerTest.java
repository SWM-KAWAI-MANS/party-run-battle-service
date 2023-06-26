package online.partyrun.partyrunbattleservice.domain.battle.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidNumberOfBattleRunnerException;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerResponse;
import online.partyrun.testmanager.docs.RestControllerNoneAuthTest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@DisplayName("BattleRestController")
class BattleRestControllerTest extends RestControllerNoneAuthTest {

    @MockBean BattleService battleService;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 정상적인_러너들의_id가_주어졌을_때 {

        BattleCreateRequest request = new BattleCreateRequest(List.of("1", "2", "3"));

        @Test
        @DisplayName("방 생성을 수행한다")
        void createBattle() throws Exception {
            given(battleService.createBattle(request))
                    .willReturn(
                            new BattleResponse(
                                    "battle_id",
                                    List.of(
                                            new RunnerResponse("1", "박성우"),
                                            new RunnerResponse("2", "노준혁"),
                                            new RunnerResponse("3", "박현준"))));

            final ResultActions actions =
                    mockMvc.perform(
                            post("/battle")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(toRequestBody(request)));
            actions.andExpect(status().isCreated());

            setPrintDocs(actions, "create battle");
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너_요청이_잘못되었을_때 {

        public static Stream<Arguments> invalidBattleRequest() {
            return Stream.of(
                    Arguments.of(null, 1),
                    Arguments.of(new BattleCreateRequest(List.of("2", "3")), 2),
                    Arguments.of(new BattleCreateRequest(null), 3));
        }

        @ParameterizedTest
        @MethodSource("invalidBattleRequest")
        @DisplayName("BadRequest를 응답한다.")
        void returnException(BattleCreateRequest invalidRequest, int order) throws Exception {
            given(battleService.createBattle(invalidRequest))
                    .willThrow(new InvalidNumberOfBattleRunnerException(2));

            final ResultActions actions =
                    mockMvc.perform(
                            post("/battle")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(toRequestBody(invalidRequest)));
            actions.andExpect(status().isBadRequest());

            setPrintDocs(actions, String.format("number of runner is invalid %d", order));
        }
    }
}
