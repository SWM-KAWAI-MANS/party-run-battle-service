package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.config.TestTimeConfig;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.GpsRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerRecordRequest;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.member.entity.Member;
import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Import({WebSocketTestConfiguration.class, TestTimeConfig.class})
@DisplayName("BattleWebSocketAcceptance")
public class BattleWebSocketAcceptanceTest extends AcceptanceTest {
    private static final String TOPIC_BATTLE_PREFIX = "/topic/battles";
    private static final String PUB_BATTLE_PREFIX = "/pub/battles";

    @Autowired
    WebSocketStompClient webSocketStompClient;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    JwtGenerator jwtGenerator;
    @Autowired
    Clock clock;
    Battle 배틀;
    StompSession 박성우_Session;
    StompSession 박현준_Session;
    StompSession 노준혁_Session;
    BlockingQueue<BattleWebSocketResponse> 박성우_Queue;
    BlockingQueue<BattleWebSocketResponse> 박현준_Queue;
    BlockingQueue<BattleWebSocketResponse> 노준혁_Queue;

    private Runner 러너_생성(Member member) {
        return new Runner(memberRepository.save(member).getId());
    }

    private String 토큰_생성(Runner runner) {
        return jwtGenerator.generate(runner.getId(), Set.of("ROLE_USER")).accessToken();
    }

    private StompSession 웹소켓_연결(String accessToken) {
        try {
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add("Authorization", accessToken);

            return webSocketStompClient.connectAsync(
                            "ws://localhost:" + port + "/api/ws/battles/connection",
                            headers,
                            new StompSessionHandlerAdapter() {
                            })
                    .get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void 구독_요청(StompSession session, BlockingQueue<BattleWebSocketResponse> queue, Battle battle) {
        session.subscribe(
                String.format("%s/%s", TOPIC_BATTLE_PREFIX, battle.getId()),
                new StompFrameHandlerImpl(queue));
    }

    private void 준비완료_요청(StompSession session,Battle battle) {
        session.send(String.format("%s/%s/ready", PUB_BATTLE_PREFIX, battle.getId()), "준비 완료");
    }

    @BeforeEach
    void setUpWebSocket() {
        Runner 박성우 = 러너_생성(멤버_박성우);
        Runner 노준혁 = 러너_생성(멤버_노준혁);
        Runner 박현준 = 러너_생성(멤버_박현준);

        String 박성우_accessToken = 토큰_생성(박성우);
        String 박현준_accessToken = 토큰_생성(박현준);
        String 노준혁_accessToken = 토큰_생성(노준혁);

        박성우_Session = 웹소켓_연결(박성우_accessToken);
        박현준_Session = 웹소켓_연결(박현준_accessToken);
        노준혁_Session = 웹소켓_연결(노준혁_accessToken);

        배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준, 노준혁), LocalDateTime.now(clock)));

        박성우_Queue = new LinkedBlockingDeque<>();
        박현준_Queue = new LinkedBlockingDeque<>();
        노준혁_Queue = new LinkedBlockingDeque<>();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 웹_소켓_연결을_실행할_때 {

        Runner 박성우 = 러너_생성(멤버_박성우);
        String accessToken = 토큰_생성(박성우);

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
    class 러너들이_준비_완료_요청을_보냈을_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_보냈다면 {

            @BeforeEach
            void setUp() {
                구독_요청(박성우_Session, 박성우_Queue, 배틀);
                구독_요청(박현준_Session, 박현준_Queue, 배틀);
                구독_요청(노준혁_Session, 노준혁_Queue, 배틀);

                준비완료_요청(박성우_Session, 배틀);
                준비완료_요청(박현준_Session, 배틀);
            }

            @Test
            @DisplayName("배틀 시작 시간을 응답한다.")
            void getBattleStartTime() throws InterruptedException {
                준비완료_요청(노준혁_Session, 배틀);

                final BattleWebSocketResponse 박성우_response = 박성우_Queue.poll(3, TimeUnit.SECONDS);
                final BattleWebSocketResponse 박현준_response = 박현준_Queue.poll(3, TimeUnit.SECONDS);
                final BattleWebSocketResponse 노준혁_response = 노준혁_Queue.poll(3, TimeUnit.SECONDS);

                assertAll(() -> assertThat(박성우_response)
                                .isEqualTo(박현준_response)
                                .isEqualTo(노준혁_response)
                                .isNotNull(),
                        () -> assertThat(박성우_response.getData().get("startTime"))
                                .isEqualTo(LocalDateTime.now(clock).plusSeconds(5).toString()));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_보내지_않았다면 {

            @Test
            @DisplayName("응답을 받지 못한다.")
            void getBattleStartTime() throws InterruptedException {
                BattleWebSocketResponse 박성우_response = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response = 노준혁_Queue.poll(1, TimeUnit.SECONDS);

                assertThat(박성우_response).isEqualTo(박현준_response).isEqualTo(노준혁_response).isNull();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들의_기록을_저장할_때 {
        LocalDateTime now = LocalDateTime.now();

        @BeforeEach
        void setUpData() {
            구독_요청(박성우_Session, 박성우_Queue, 배틀);
            구독_요청(박현준_Session, 박현준_Queue, 배틀);
            구독_요청(노준혁_Session, 노준혁_Queue, 배틀);

            준비완료_요청(박성우_Session, 배틀);
            준비완료_요청(박현준_Session, 배틀);
            준비완료_요청(노준혁_Session, 배틀);

            // 시작 시간 응답
            assertAll(
                    () -> assertThat(박성우_Queue.poll(1, TimeUnit.SECONDS)).isNotNull(),
                    () -> assertThat(박현준_Queue.poll(1, TimeUnit.SECONDS)).isNotNull(),
                    () -> assertThat(노준혁_Queue.poll(1, TimeUnit.SECONDS)).isNotNull()
            );

            // 배틀 시간 변경
            Query query = Query.query(Criteria.where("id").is(배틀.getId()));
            mongoTemplate.updateFirst(query, Update.update("startTime", now), Battle.class);
        }

        LocalDateTime gpsTime = now.plusMinutes(1);
        GpsRequest GPS_REQUEST1 = new GpsRequest(0, 0, 0, gpsTime);
        GpsRequest GPS_REQUEST2 = new GpsRequest(0.001, 0.001, 0.001, gpsTime.plusSeconds(1));
        GpsRequest GPS_REQUEST3 = new GpsRequest(0.002, 0.002, 0.002, gpsTime.plusSeconds(2));
        GpsRequest GPS_REQUEST4 = new GpsRequest(0.003, 0.003, 0.003, gpsTime.plusSeconds(3));
        GpsRequest GPS_REQUEST5 = new GpsRequest(0.004, 0.004, 0.004, gpsTime.plusSeconds(4));
        GpsRequest GPS_REQUEST6 = new GpsRequest(0.005, 0.005, 0.005, gpsTime.plusSeconds(5));
        GpsRequest GPS_REQUEST7 = new GpsRequest(0.006, 0.006, 0.006, gpsTime.plusSeconds(6));
        GpsRequest GPS_REQUEST8 = new GpsRequest(0.007, 0.007, 0.007, gpsTime.plusSeconds(7));
        GpsRequest GPS_REQUEST9 = new GpsRequest(0.008, 0.008, 0.008, gpsTime.plusSeconds(8));
        RunnerRecordRequest RECORD_REQUEST1 =
                new RunnerRecordRequest(List.of(GPS_REQUEST1, GPS_REQUEST2, GPS_REQUEST3));
        RunnerRecordRequest RECORD_REQUEST2 =
                new RunnerRecordRequest(List.of(GPS_REQUEST4, GPS_REQUEST5, GPS_REQUEST6));
        RunnerRecordRequest RECORD_REQUEST3 =
                new RunnerRecordRequest(List.of(GPS_REQUEST7, GPS_REQUEST8, GPS_REQUEST9));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 좌표를_받으면 {

            private void 좌표_보내기_요청(StompSession 박성우_Session, RunnerRecordRequest RECORD_REQUEST1) {
                박성우_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()),
                        RECORD_REQUEST1);
            }

            @Test
            @DisplayName("배틀에 참여한 러너들에게 거리를 응답한다.")
            void returnRunnersDistance() throws InterruptedException {
                좌표_보내기_요청(박성우_Session, RECORD_REQUEST1);
                좌표_보내기_요청(노준혁_Session, RECORD_REQUEST1);
                좌표_보내기_요청(박현준_Session, RECORD_REQUEST1);

                BattleWebSocketResponse 박성우_response1 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박성우_response2 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박성우_response3 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response1 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response2 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response3 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response1 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response2 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response3 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);

                assertThat(List.of(박성우_response1, 박성우_response2, 박성우_response3))
                        .containsAll(List.of(노준혁_response1, 노준혁_response2, 노준혁_response3))
                        .containsAll(List.of(박현준_response1, 박현준_response2, 박현준_response3));
            }

            @Test
            @DisplayName("러너가 목표거리를 달리면 러너 종료 응답을 한다.")
            void returnRunnerFinished() throws InterruptedException {
                좌표_보내기_요청(박성우_Session, RECORD_REQUEST1);
                Thread.sleep(300);
                좌표_보내기_요청(박성우_Session, RECORD_REQUEST2);
                Thread.sleep(300);
                좌표_보내기_요청(박성우_Session, RECORD_REQUEST3);

                BattleWebSocketResponse 박성우_response1 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박성우_response2 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박성우_response3 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박성우_response4 = 박성우_Queue.poll(1, TimeUnit.SECONDS);

                BattleWebSocketResponse 박현준_response1 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response2 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response3 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 박현준_response4 = 박현준_Queue.poll(1, TimeUnit.SECONDS);

                BattleWebSocketResponse 노준혁_response1 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response2 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response3 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse 노준혁_response4 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);

                final List<BattleWebSocketResponse> responses =
                        List.of(박성우_response1, 박성우_response2, 박성우_response3, 박성우_response4);
                assertAll(
                        () -> assertThat(responses)
                                .containsAll(List.of(
                                        노준혁_response1,
                                        노준혁_response2,
                                        노준혁_response3,
                                        노준혁_response4))
                                .containsAll(List.of(
                                        박현준_response1,
                                        박현준_response2,
                                        박현준_response3,
                                        박현준_response4)),
                        () ->
                                assertThat(responses.stream()
                                        .filter(battleWebSocketResponse -> battleWebSocketResponse.getType().equals("BATTLE_RUNNING")))
                                        .hasSize(3),
                        () ->
                                assertThat(responses.stream()
                                        .filter(battleWebSocketResponse -> battleWebSocketResponse.getType().equals("RUNNER_FINISHED")))
                                        .hasSize(1));
            }

            @Test
            @DisplayName("클라이언트가 중복으로 요청을 보내면 예외를 던진다.")
            void throwExceptionForDuplicatedRequest() throws InterruptedException {
                좌표_보내기_요청(박성우_Session, RECORD_REQUEST1);
                좌표_보내기_요청(박성우_Session, RECORD_REQUEST1);

                BattleWebSocketResponse response1 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                BattleWebSocketResponse response2 = 박성우_Queue.poll(1, TimeUnit.SECONDS);

                assertAll(
                        () -> assertThat(Objects.isNull(response1) && Objects.isNull(response2)).isFalse(),
                        () -> assertThat(Objects.isNull(response1) || Objects.isNull(response2)).isTrue());
            }
        }
    }
}
