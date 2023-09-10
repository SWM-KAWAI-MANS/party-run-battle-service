package online.partyrun.partyrunbattleservice.domain.single.controller;

import online.partyrun.partyrunbattleservice.domain.single.dto.SingleIdResponse;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleRunnerRecordRequest;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleRunnerRecordsRequest;
import online.partyrun.partyrunbattleservice.domain.single.service.SingleService;
import online.partyrun.testmanager.docs.RestControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SingleController.class)
@DisplayName("SingleController")
class SingleControllerTest extends RestControllerTest {

    private static final String SINGLE_URL = "/singles";

    @MockBean
    SingleService singleService;

    LocalDateTime now = LocalDateTime.now();

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 싱글을_생성할_때 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 러너의_기록이_주어지면 {

            @Test
            @DisplayName("싱글을_생성한다")
            void createSingle() throws Exception {
                SingleRunnerRecordsRequest request = new SingleRunnerRecordsRequest(
                        List.of(
                                new SingleRunnerRecordRequest(0, 0, 0, now, 0),
                                new SingleRunnerRecordRequest(0.0001, 0.0001, 0.0001, now, 1)
                        )
                );
                final SingleIdResponse response = new SingleIdResponse("single id");
                given(singleService.create("defaultUser", request)).willReturn(response);

                final ResultActions actions =
                        mockMvc.perform(
                                post(SINGLE_URL)
                                        .with(csrf())
                                        .header(
                                                "Authorization",
                                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding(StandardCharsets.UTF_8)
                                        .content(toRequestBody(request)));

                actions.andExpect(status().isCreated())
                        .andExpect(content().json(toRequestBody(response)));

                setPrintDocs(actions, "create single");
            }
        }
    }
}
