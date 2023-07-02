package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;

@Import(WebSocketTestConfiguration.class)
@DisplayName("BattleWebSocketAcceptance")
public class BattleWebSocketAcceptanceTest extends AcceptanceTest {

    @Autowired WebSocketStompClient webSocketStompClient;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 웹_소켓_연결을_실행할_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 정상적인_러너가_요청한다면 {
            String accessToken =
                    "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IuuwleyEseyasCIsInJvbGUiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTAxNjg4MTk0Mzk2fQ.zRmofIpdozhKUCNijYkrOnUIlC5y4oY136FAJqJLKL_CMAdZR1c_QDbxaGym5nmrAJJ2c7dyf7qgAig70n5org";

            @Test
            @DisplayName("연결에 성공한다.")
            void successToConnection() throws Exception {

                WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
                headers.add("Authorization", accessToken);

                final CompletableFuture<StompSession> stompSessionFuture =
                        webSocketStompClient.connectAsync(
                                "ws://localhost:" + port + "/api/battle/ws",
                                headers,
                                new StompSessionHandlerAdapter() {});

                assertThat(stompSessionFuture.get()).isNotNull();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 비정상적인_러너가_요청한다면 {
            String invalidAccessToken = "invalid.access.token";

            @Test
            @DisplayName("연결에 실패한다.")
            void failToConnection() {

                WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
                headers.add("Authorization", invalidAccessToken);

                final CompletableFuture<StompSession> stompSessionFuture =
                        webSocketStompClient.connectAsync(
                                "ws://localhost:" + port + "/api/battle/ws",
                                headers,
                                new StompSessionHandlerAdapter() {});

                assertThatThrownBy(stompSessionFuture::get).isInstanceOf(Exception.class);
            }
        }
    }
}
