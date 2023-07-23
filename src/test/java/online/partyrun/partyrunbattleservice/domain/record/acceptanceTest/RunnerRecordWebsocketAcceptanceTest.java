package online.partyrun.partyrunbattleservice.domain.record.acceptanceTest;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest.StompFrameHandlerImpl;
import online.partyrun.partyrunbattleservice.domain.battle.config.WebSocketTestConfiguration;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.member.entity.Member;
import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;
import online.partyrun.partyrunbattleservice.domain.record.dto.RunnerDistanceResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static online.partyrun.partyrunbattleservice.fixture.record.RequestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RunnerRecordAcceptance")
@Import(WebSocketTestConfiguration.class)
public class RunnerRecordWebsocketAcceptanceTest extends AcceptanceTest {
    private static final String TOPIC_BATTLE_PREFIX = "/topic/battle";
    private static final String PUB_BATTLE_PREFIX = "/pub/battle";
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BattleRepository battleRepository;

    @Autowired
    WebSocketStompClient webSocketStompClient;

    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    MongoTemplate mongoTemplate;

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
        String 박성우_accessToken = jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();
        String 노준혁_accessToken = jwtGenerator.generate(노준혁.getId(), Set.of("ROLE_USER")).accessToken();
        String 박현준_accessToken = jwtGenerator.generate(박현준.getId(), Set.of("ROLE_USER")).accessToken();

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
                new StompFrameHandlerImpl<>(RunnerDistanceResponse.class, 박성우_Queue)
        );

        박현준_Session.subscribe(
                String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                new StompFrameHandlerImpl<>(RunnerDistanceResponse.class, 박현준_Queue)
        );

        노준혁_Session.subscribe(
                String.format("%s/%s", TOPIC_BATTLE_PREFIX, 배틀.getId()),
                new StompFrameHandlerImpl<>(RunnerDistanceResponse.class, 노준혁_Queue)
        );

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
        mongoTemplate.updateFirst(query, Update.update("startTime", LocalDateTime.now()), Battle.class);

    }

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
    class 배틀을_진행할_떄 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 좌표를_받으면 {

            @Test
            @DisplayName("배틀에 참여한 러너들에게 거리를 응답한다.")
            void returnRunnersDistance() throws InterruptedException {
                박성우_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()), RECORD_REQUEST1);

                노준혁_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()), RECORD_REQUEST2);

                박현준_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()), RECORD_REQUEST3);


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

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 좌표를_여러번_받으면 {

            @Test
            @DisplayName("추가된 거리를 계산하여 응답한다.")
            void returnRunnersDistance() throws InterruptedException {
                박성우_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()), RECORD_REQUEST1);

                // TODO: 2023/07/23 현재 record만 조회하고 record에만 push 하는 방식이라서, 동시성 이슈가 발생하므로 Thread 잠시 멈춤
                Thread.sleep(100);
                박성우_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()), RECORD_REQUEST2);

                박성우_Session.send(
                        String.format("%s/%s/record", PUB_BATTLE_PREFIX, 배틀.getId()), RECORD_REQUEST3);

                RunnerDistanceResponse response1 = 박성우_Queue.poll(1, TimeUnit.SECONDS);
                RunnerDistanceResponse response2 = 박성우_Queue.poll(1, TimeUnit.SECONDS);

                assertThat(response2.getDistance()).isGreaterThan(response1.getDistance());
            }
        }
    }
}
