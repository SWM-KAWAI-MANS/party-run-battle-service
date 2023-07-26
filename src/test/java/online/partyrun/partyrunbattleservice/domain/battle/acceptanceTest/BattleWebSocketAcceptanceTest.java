package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.config.TestTimeConfig;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartTimeResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.GpsRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.RunnerDistanceResponse;
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
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
@Import({WebSocketTestConfiguration.class, TestTimeConfig.class})
@DisplayName("BattleWebSocketAcceptance")
public class BattleWebSocketAcceptanceTest extends AcceptanceTest {
    private static final String TOPIC_BATTLE_PREFIX = "/topic/battle";
    private static final String PUB_BATTLE_PREFIX = "/pub/battle";

    @Autowired WebSocketStompClient webSocketStompClient;
    @Autowired BattleRepository battleRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired MongoTemplate mongoTemplate;
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
        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
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
    class 러너들이_구독_성공_요청을_보냈을_때 {

        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Runner 박현준 = new Runner(memberRepository.save(멤버_박현준).getId());
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
        class 모두_보냈다면 {

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
                                        LocalDateTime.now(clock).plusSeconds(5)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 모두_보내지_않았다면 {

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

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 러너들의_기록을_저장할_때 {

        Battle 배틀;
        StompSession 박성우_Session;
        StompSession 박현준_Session;
        StompSession 노준혁_Session;
        BlockingQueue<RunnerDistanceResponse> 박성우_Queue;
        BlockingQueue<RunnerDistanceResponse> 박현준_Queue;
        BlockingQueue<RunnerDistanceResponse> 노준혁_Queue;

        @BeforeEach
        void setUpData() throws InterruptedException {
            // 러너 저장
            Runner 박성우 = new Runner(memberRepository.save(new Member("박성우")).getId());
            Runner 노준혁 = new Runner(memberRepository.save(new Member("노준혁")).getId());
            Runner 박현준 = new Runner(memberRepository.save(new Member("박현준")).getId());
            String 박성우_accessToken =
                    jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();
            String 노준혁_accessToken =
                    jwtGenerator.generate(노준혁.getId(), Set.of("ROLE_USER")).accessToken();
            String 박현준_accessToken =
                    jwtGenerator.generate(박현준.getId(), Set.of("ROLE_USER")).accessToken();

            // 배틀 생성
            배틀 = battleRepository.save(new Battle(1000, List.of(박성우, 박현준, 노준혁)));

            // 웹소켓 연결
            박성우_Session = 웹소켓_연결(박성우_accessToken);
            박현준_Session = 웹소켓_연결(박현준_accessToken);
            노준혁_Session = 웹소켓_연결(노준혁_accessToken);

            박성우_Queue = new LinkedBlockingDeque<>();
            박현준_Queue = new LinkedBlockingDeque<>();
            노준혁_Queue = new LinkedBlockingDeque<>();

            // 구독 요청
            박성우_Session.subscribe(
                    String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                    new StompFrameHandlerImpl<>(RunnerDistanceResponse.class, 박성우_Queue));

            박현준_Session.subscribe(
                    String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                    new StompFrameHandlerImpl<>(RunnerDistanceResponse.class, 박현준_Queue));

            노준혁_Session.subscribe(
                    String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                    new StompFrameHandlerImpl<>(RunnerDistanceResponse.class, 노준혁_Queue));

            // 러너 준비 요창
            박성우_Session.send(String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");
            박현준_Session.send(String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");
            노준혁_Session.send(String.format("%s/%s/ready", PUB_BATTLE_PREFIX, 배틀.getId()), "준비 완료");

            // 시작 시간 응답
            박성우_Queue.poll(1, TimeUnit.SECONDS);
            박현준_Queue.poll(1, TimeUnit.SECONDS);
            노준혁_Queue.poll(1, TimeUnit.SECONDS);

            // 배틀 시간 변경
            Query query = Query.query(Criteria.where("id").is(배틀.getId()));
            mongoTemplate.updateFirst(
                    query, Update.update("startTime", LocalDateTime.now()), Battle.class);
        }

        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        GpsRequest GPS_REQUEST1 = new GpsRequest(1, 1, 1, now);
        GpsRequest GPS_REQUEST2 = new GpsRequest(2, 2, 2, now.plusSeconds(1));
        GpsRequest GPS_REQUEST3 = new GpsRequest(3, 3, 3, now.plusSeconds(2));
        GpsRequest GPS_REQUEST4 = new GpsRequest(1, 1, 1, now.plusSeconds(3));
        GpsRequest GPS_REQUEST5 = new GpsRequest(2, 2, 2, now.plusSeconds(4));
        GpsRequest GPS_REQUEST6 = new GpsRequest(3, 3, 3, now.plusSeconds(5));
        GpsRequest GPS_REQUEST7 = new GpsRequest(4, 4, 4, now.plusSeconds(6));
        GpsRequest GPS_REQUEST8 = new GpsRequest(5, 5, 5, now.plusSeconds(7));
        GpsRequest GPS_REQUEST9 = new GpsRequest(6, 6, 6, now.plusSeconds(8));
        RunnerRecordRequest RECORD_REQUEST1 =
                new RunnerRecordRequest(List.of(GPS_REQUEST1, GPS_REQUEST2, GPS_REQUEST3));
        RunnerRecordRequest RECORD_REQUEST2 =
                new RunnerRecordRequest(List.of(GPS_REQUEST4, GPS_REQUEST5, GPS_REQUEST6));
        RunnerRecordRequest RECORD_REQUEST3 =
                new RunnerRecordRequest(List.of(GPS_REQUEST7, GPS_REQUEST8, GPS_REQUEST9));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 좌표를_받으면 {

            @Test
            @DisplayName("배틀에 참여한 러너들에게 거리를 응답한다.")
            void returnRunnersDistance() throws InterruptedException {
                박성우_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()),
                        RECORD_REQUEST1);

                노준혁_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()),
                        RECORD_REQUEST2);

                박현준_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()),
                        RECORD_REQUEST3);

                RunnerDistanceResponse 박성우_response1 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse 박성우_response2 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse 박성우_response3 = 박성우_Queue.poll(1, TimeUnit.SECONDS);

                RunnerDistanceResponse 박현준_response1 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse 박현준_response2 = 박현준_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse 박현준_response3 = 박현준_Queue.poll(1, TimeUnit.SECONDS);

                RunnerDistanceResponse 노준혁_response1 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse 노준혁_response2 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse 노준혁_response3 = 노준혁_Queue.poll(1, TimeUnit.SECONDS);

                assertThat(List.of(박성우_response1, 박성우_response2, 박성우_response3))
                        .containsAll(List.of(노준혁_response1, 노준혁_response2, 노준혁_response3))
                        .containsAll(List.of(박현준_response1, 박현준_response2, 박현준_response3));
            }
        }
    }
}
