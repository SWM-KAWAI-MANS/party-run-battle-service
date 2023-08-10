package online.partyrun.partyrunbattleservice.domain.battle.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleIdResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.MessageResponse;
import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidNumberOfBattleRunnerException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyRunnerNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerResponse;
import online.partyrun.testmanager.docs.RestControllerTest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(BattleController.class)
@DisplayName("BattleController")
class BattleControllerTest extends RestControllerTest {

    private static final String BATTLE_URL = "/battles";

    @MockBean BattleService battleService;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_생성할_때 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 정상적인_러너들의_id가_주어지면 {

            BattleCreateRequest request = new BattleCreateRequest(1000, List.of("1", "2", "3"));
            BattleIdResponse response = new BattleIdResponse("battle_id");

            @Test
            @DisplayName("배틀 생성을 수행한다")
            void createBattle() throws Exception {
                given(battleService.createBattle(request)).willReturn(response);

                final ResultActions actions =
                        mockMvc.perform(
                                post(BATTLE_URL)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding(StandardCharsets.UTF_8)
                                        .content(toRequestBody(request)));
                actions.andExpect(status().isCreated())
                        .andExpect(content().json(toRequestBody(response)));

                setPrintDocs(actions, "create battle");
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너_요청이_잘못되었다면 {

            public static Stream<Arguments> invalidBattleRequest() {
                return Stream.of(
                        Arguments.of(null, "body is null"),
                        Arguments.of(
                                new BattleCreateRequest(1000, List.of("2", "3")),
                                "number of runners error"),
                        Arguments.of(new BattleCreateRequest(1000, null), "runner is null"));
            }

            @ParameterizedTest
            @MethodSource("invalidBattleRequest")
            @DisplayName("BadRequest를 응답한다.")
            void returnException(BattleCreateRequest invalidRequest, String message)
                    throws Exception {
                given(battleService.createBattle(invalidRequest))
                        .willThrow(new InvalidNumberOfBattleRunnerException(2));

                final ResultActions actions =
                        mockMvc.perform(
                                post(BATTLE_URL)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding(StandardCharsets.UTF_8)
                                        .content(toRequestBody(invalidRequest)));
                actions.andExpect(status().isBadRequest());

                setPrintDocs(actions, String.format("runner request is invalid, %s", message));
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_조회를_요청할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀에_참여하고_있는_러너의_id가_주어지면 {

            BattleIdResponse response = new BattleIdResponse("battle_id");

            @Test
            @DisplayName("배틀 정보를 반환한다.")
            void getRunningBattle() throws Exception {
                given(battleService.getReadyBattle("defaultUser")).willReturn(response);

                final ResultActions actions =
                        mockMvc.perform(
                                get(String.format("%s/join", BATTLE_URL))
                                        .header(
                                                "Authorization",
                                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding(StandardCharsets.UTF_8));

                actions.andExpect(status().isOk())
                        .andExpect(content().json(toRequestBody(response)));

                setPrintDocs(actions, "get battle");
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀에_참여하고_있지_않은_러너의_id가_주어지면 {

            @Test
            @DisplayName("not found를 반환한다.")
            void getRunningBattle() throws Exception {
                given(battleService.getReadyBattle("defaultUser"))
                        .willThrow(new ReadyRunnerNotFoundException("defaultUser"));

                final ResultActions actions =
                        mockMvc.perform(
                                get(String.format("%s/join", BATTLE_URL))
                                        .header(
                                                "Authorization",
                                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding(StandardCharsets.UTF_8));

                actions.andExpect(status().isNotFound());

                setPrintDocs(actions, "battle not found");
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_결과를_조회할_때 {

        @Test
        @DisplayName("러너들의 결과를 반환한다.")
        void getFinishedBattle() throws Exception {
            String battleId = "battleId";
            LocalDateTime now = LocalDateTime.now();

            BattleResponse response =
                    new BattleResponse(
                            1000,
                            now.minusMinutes(5),
                            List.of(
                                    new RunnerResponse("parkseongwoo", 1, now),
                                    new RunnerResponse(
                                            "nojunhyuk", 2, now.plusMinutes(1))));

            given(battleService.getBattle(battleId, "defaultUser")).willReturn(response);

            final ResultActions actions =
                    mockMvc.perform(
                            get(String.format("%s/%s", BATTLE_URL, battleId))
                                    .header(
                                            "Authorization",
                                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8));

            actions.andExpect(status().isOk()).andExpect(content().json(toRequestBody(response)));

            setPrintDocs(actions, "get finished battle");
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너가_접속했을_때 {

        @Test
        @DisplayName("배틀에 속해있다면 종료시킨다.")
        void changeRunnerFinished() throws Exception {
            MessageResponse response = new MessageResponse("요청이 정상적으로 처리되었습니다.");
            given(battleService.changeRunnerFinished("defaultUser")).willReturn(response);

            final ResultActions actions =
                    mockMvc.perform(
                            post(String.format("%s/runners/finished", BATTLE_URL))
                                    .with(csrf())
                                    .header(
                                            "Authorization",
                                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8));

            actions.andExpect(status().isOk()).andExpect(content().json(toRequestBody(response)));

            setPrintDocs(actions, "get finished battle");
        }
    }
}
