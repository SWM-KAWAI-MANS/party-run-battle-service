package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(WebSocketTestConfiguration.class)
@DisplayName("BattleWebSocketAcceptance")
public class BattleWebSocketAcceptanceTest extends AcceptanceTest {

    @Autowired
    WebSocketStompClient webSocketStompClient;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    RunnerRepository runnerRepository;
    @Autowired
    JwtGenerator jwtGenerator;

    private CompletableFuture<StompSession> 웹소켓_연결(String accessToken) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", accessToken);

        final CompletableFuture<StompSession> stompSessionFuture =
                webSocketStompClient.connectAsync(
                        "ws://localhost:" + port + "/api/battle/ws",
                        headers,
                        new StompSessionHandlerAdapter() {
                            @Override
                            public void handleException(
                                    StompSession session,
                                    StompCommand command,
                                    StompHeaders headers,
                                    byte[] payload,
                                    Throwable exception) {
                                session.disconnect();
                            }
                        });
        return stompSessionFuture;
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 웹_소켓_연결을_실행할_때 {

        String accessToken =
                "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IuuwleyEseyasCIsInJvbGUiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTAxNjg4MTk0Mzk2fQ.zRmofIpdozhKUCNijYkrOnUIlC5y4oY136FAJqJLKL_CMAdZR1c_QDbxaGym5nmrAJJ2c7dyf7qgAig70n5org";

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 정상적인_러너가_요청한다면 {

            @Test
            @DisplayName("연결에 성공한다.")
            void successToConnection() throws Exception {
                final CompletableFuture<StompSession> stompSessionFuture = 웹소켓_연결(accessToken);

                assertThat(stompSessionFuture.get(5, TimeUnit.SECONDS)).isNotNull();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 비정상적인_러너가_요청한다면 {

            String invalidAccessToken = "invalid.access.token";

            @Test
            @DisplayName("연결에 실패한다.")
            void failToConnection() {
                final CompletableFuture<StompSession> stompSessionFuture =
                        웹소켓_연결(invalidAccessToken);

                assertThatThrownBy(() -> stompSessionFuture.get(5, TimeUnit.SECONDS))
                        .isInstanceOf(Exception.class);
            }
        }
    }
}

