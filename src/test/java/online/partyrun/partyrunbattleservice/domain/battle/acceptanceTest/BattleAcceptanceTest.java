package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.repository.RunnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

import static online.partyrun.partyrunbattleservice.acceptance.SimpleRestAssured.toObject;
import static online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest.BattleRestAssuredRequest.배틀_생성_요청;
import static online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest.BattleRestAssuredRequest.배틀_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("BattleAcceptanceTest")
public class BattleAcceptanceTest extends AcceptanceTest {

    @Autowired
    RunnerRepository runnerRepository;

    @Autowired
    JwtGenerator jwtGenerator;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_생성을_할_때 {
        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));
        Runner 박현준 = runnerRepository.save(new Runner("박현준"));

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 정상적인_러너들로_요청하면 {

            @Test
            @DisplayName("Create와 방 생성 정보를 응답한다.")
            void returnCreated() {
                final ExtractableResponse<Response> response = 배틀_생성_요청(new BattleCreateRequest(List.of(박성우.getId(), 노준혁.getId(), 박현준.getId())));
                assertAll(
                        () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                        () -> assertThat(toObject(response, BattleResponse.class).id()).isNotNull()
                );
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_조회_할_때 {
        Runner 박성우 = runnerRepository.save(new Runner("박성우"));
        Runner 노준혁 = runnerRepository.save(new Runner("노준혁"));
        Runner 박현준 = runnerRepository.save(new Runner("박현준"));
        String 박성우_accessToken = jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀에_참여하고_있는_러너가_요청하면 {

            @Test
            @DisplayName("OK와 배틀 정보를 응답한다.")
            void returnOK() {
                배틀_생성_요청(new BattleCreateRequest(List.of(박성우.getId(), 노준혁.getId(), 박현준.getId())));
                final ExtractableResponse<Response> response = 배틀_조회_요청(박성우_accessToken);

                assertAll(
                        () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                        () -> assertThat(toObject(response, BattleResponse.class).id()).isNotNull()
                );
            }
        }
    }
}