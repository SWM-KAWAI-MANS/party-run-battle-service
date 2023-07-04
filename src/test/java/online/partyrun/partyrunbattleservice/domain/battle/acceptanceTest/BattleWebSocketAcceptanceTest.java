package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;
import online.partyrun.partyrunbattleservice.domain.battle.dto.LocationDto;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

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
    BlockingQueue<LocationDto> locationQueue = new LinkedBlockingDeque<>();

    private CompletableFuture<StompSession> 웹소켓_연결(String accessToken) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", accessToken);

        final CompletableFuture<StompSession> stompSessionFuture =
                webSocketStompClient.connectAsync(
                        "ws://localhost:" + port + "/api/battle/ws",
                        headers,
                        new StompSessionHandlerAdapter() {
                        });
        return stompSessionFuture;
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 웹_소켓_연결을_실행할_때 {
        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        String accessToken = jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();

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

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 웹소켓_연결에_성공했을_때 {
        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        String accessToken = jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();
        Battle 배틀 = battleRepository.save(new Battle(List.of(박성우)));
        CompletableFuture<StompSession> stompSessionFuture = 웹소켓_연결(accessToken);

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 구독_요청에_성공하면 {

            @Test
            @DisplayName("메시지를 받을 수 있다")
            void getMessage() throws ExecutionException, InterruptedException, TimeoutException {
                System.out.println(accessToken);
                StompSession stompSession = stompSessionFuture.get(5, TimeUnit.SECONDS);
                stompSession.subscribe(
                        String.format("/topic/battle/%s", 배틀.getId()),
                        new StompFrameHandlerImpl<>(new LocationDto(), locationQueue)
                );

                stompSession.send(String.format("/pub/battle/%s", 배틀.getId()), new LocationDto(1, 2));
                LocationDto result = locationQueue.poll(5, TimeUnit.SECONDS);
                assertThat(result).isEqualTo(new LocationDto(1, 2));
            }
        }
    }
}

