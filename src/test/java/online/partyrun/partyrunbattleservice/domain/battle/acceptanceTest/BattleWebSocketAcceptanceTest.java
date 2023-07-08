package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.config.TestTimeConfig;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartTimeResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.LocationDto;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@Import({WebSocketTestConfiguration.class, TestTimeConfig.class})
@DisplayName("BattleWebSocketAcceptance")
public class BattleWebSocketAcceptanceTest extends AcceptanceTest {
    private static final String TOPIC_BATTLE_PREFIX = "/topic/battle";
    private static final String PUB_BATTLE_PREFIX = "/pub/battle";

    @Autowired WebSocketStompClient webSocketStompClient;
    @Autowired BattleRepository battleRepository;
    @Autowired RunnerRepository runnerRepository;
    @Autowired JwtGenerator jwtGenerator;
    @Autowired Clock clock;

    private StompSession 웹소켓_연결(String accessToken) {
        try {
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add("Authorization", accessToken);

            return webSocketStompClient
                    .connectAsync(
                            "ws://localhost:" + port + "/api/battle/ws",
                            headers,
                            new StompSessionHandlerAdapter() {})
                    .get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException();
        }
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
            void successToConnection() {
                final StompSession stompSession = 웹소켓_연결(accessToken);

                assertThat(stompSession).isNotNull();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 비정상적인_러너가_요청한다면 {

            String invalidAccessToken = "invalid.access.token";

            @Test
            @DisplayName("연결에 실패한다.")
            void failToConnection() {
                assertThatThrownBy(() -> 웹소켓_연결(invalidAccessToken))
                        .isInstanceOf(RuntimeException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 웹소켓_연결에_성공했을_때 {

        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        String accessToken = jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();
        StompSession 박성우_Session = 웹소켓_연결(accessToken);
        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우)));
        BlockingQueue<LocationDto> locationQueue = new LinkedBlockingDeque<>();

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 구독_요청에_성공하면 {

            @Test
            @DisplayName("메시지를 받을 수 있다")
            void getMessage() throws InterruptedException {
                박성우_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(LocationDto.class, locationQueue));

                박성우_Session.send(
                        String.format("%s/%s", PUB_BATTLE_PREFIX, 배틀.getId()),
                        new LocationDto(1, 2));

                LocationDto result = locationQueue.poll(1, TimeUnit.SECONDS);
                assertThat(result).isEqualTo(new LocationDto(1, 2));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 구독_요청에_실패하면 {
            Runner 박현준 = runnerRepository.save(new Runner("박현준"));
            Battle 박현준_배틀 = battleRepository.save(new Battle(1000, List.of(박현준)));

            @Test
            @DisplayName("메시지를 받을 수 없다")
            void getMessage() throws InterruptedException {
                박성우_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 박현준_배틀.getId()),
                        new StompFrameHandlerImpl<>(LocationDto.class, locationQueue));

                박성우_Session.send(
                        String.format("%s/%s", PUB_BATTLE_PREFIX, 배틀.getId()),
                        new LocationDto(1, 2));

                final LocationDto location = locationQueue.poll(1, TimeUnit.SECONDS);
                assertThat(location).isNull();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들이_구독_성공_메시지를_보냈을_때 {

        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Runner 박현준 = runnerRepository.save(new Runner("박현준"));
        Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));
        String 박성우_accessToken =
                jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();
        String 박현준_accessToken =
                jwtGenerator.generate(박현준.getId(), Set.of("ROLE_USER")).accessToken();
        String 노준혁_accessToken =
                jwtGenerator.generate(노준혁.getId(), Set.of("ROLE_USER")).accessToken();
        StompSession 박성우_Session = 웹소켓_연결(박성우_accessToken);
        StompSession 박현준_Session = 웹소켓_연결(박현준_accessToken);
        StompSession 노준혁_Session = 웹소켓_연결(노준혁_accessToken);

        Battle 배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준, 노준혁)));
        BlockingQueue<BattleStartTimeResponse> 박성우_Queue = new LinkedBlockingDeque<>();
        BlockingQueue<BattleStartTimeResponse> 박현준_Queue = new LinkedBlockingDeque<>();
        BlockingQueue<BattleStartTimeResponse> 노준혁_Queue = new LinkedBlockingDeque<>();

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_메세지를_보냈다면 {

            @Test
            @DisplayName("배틀 시작 시간을 응답한다.")
            void getBattleStartTime() throws InterruptedException {
                박성우_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(BattleStartTimeResponse.class, 박성우_Queue));

                박현준_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(BattleStartTimeResponse.class, 박현준_Queue));

                노준혁_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(BattleStartTimeResponse.class, 노준혁_Queue));

                박성우_Session.send(
                        String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");

                박현준_Session.send(
                        String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");

                노준혁_Session.send(
                        String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");

                final BattleStartTimeResponse 박성우_response = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                final BattleStartTimeResponse 박현준_response = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                final BattleStartTimeResponse 노준혁_response = 노준혁_Queue.poll(1, TimeUnit.SECONDS);

                assertThat(박성우_response)
                        .isEqualTo(박현준_response)
                        .isEqualTo(노준혁_response)
                        .isEqualTo(
                                new BattleStartTimeResponse(
                                        LocalDateTime.now(clock).plusSeconds(10)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_메세지를_보내지_않았다면 {

            @Test
            @DisplayName("응답을 받지 못한다.")
            void getBattleStartTime() throws InterruptedException {
                박성우_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(BattleStartTimeResponse.class, 박성우_Queue));

                박현준_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(BattleStartTimeResponse.class, 박현준_Queue));

                노준혁_Session.subscribe(
                        String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                        new StompFrameHandlerImpl<>(BattleStartTimeResponse.class, 노준혁_Queue));

                박성우_Session.send(
                        String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");

                박현준_Session.send(
                        String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");

                final BattleStartTimeResponse 박성우_response = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                final BattleStartTimeResponse 박현준_response = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                final BattleStartTimeResponse 노준혁_response = 노준혁_Queue.poll(1, TimeUnit.SECONDS);

                assertThat(박성우_response).isEqualTo(박현준_response).isEqualTo(노준혁_response).isNull();
            }
        }
    }
}
