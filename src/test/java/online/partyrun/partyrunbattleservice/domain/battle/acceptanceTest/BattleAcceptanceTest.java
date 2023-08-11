package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import static online.partyrun.partyrunbattleservice.acceptance.SimpleRestAssured.toObject;
import static online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest.BattleRestAssuredRequest.배틀_생성_요청;
import static online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest.BattleRestAssuredRequest.참여_중인_배틀_조회_요청;
import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import online.partyrun.jwtmanager.JwtGenerator;
import online.partyrun.partyrunbattleservice.acceptance.AcceptanceTest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleIdResponse;
import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

@DisplayName("BattleAcceptanceTest")
public class BattleAcceptanceTest extends AcceptanceTest {

    @Autowired MemberRepository memberRepository;

    @Autowired JwtGenerator jwtGenerator;
    private static final String SYSTEM_TOKEN =
            "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6Im1hdGNoaW5nIiwicm9sZSI6WyJST0xFX1NZU1RFTSJdLCJleHAiOjMxNTU3MzEyNjF9.IGEtuEEaRKD9k-1EcvG3GWfs9nFgkz2UScHphRB-EUmufwPEtPrLF26T4CNoYeD9cfArKqz1km2m0pXYzo-9UA";

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀_생성을_할_때 {
        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Runner 박현준 = new Runner(memberRepository.save(멤버_박현준).getId());

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 정상적인_러너들로_요청하면 {

            @Test
            @DisplayName("Create와 방 생성 정보를 응답한다.")
            void returnCreated() {
                final ExtractableResponse<Response> response =
                        배틀_생성_요청(
                                SYSTEM_TOKEN,
                                new BattleCreateRequest(
                                        1000, List.of(박성우.getId(), 노준혁.getId(), 박현준.getId())));
                assertAll(
                        () ->
                                assertThat(response.statusCode())
                                        .isEqualTo(HttpStatus.CREATED.value()),
                        () ->
                                assertThat(toObject(response, BattleIdResponse.class).id())
                                        .isNotNull());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배틀을_조회_할_때 {
        Runner 박성우 = new Runner(memberRepository.save(멤버_박성우).getId());
        Runner 노준혁 = new Runner(memberRepository.save(멤버_노준혁).getId());
        Runner 박현준 = new Runner(memberRepository.save(멤버_박현준).getId());
        String 박성우_accessToken =
                jwtGenerator.generate(박성우.getId(), Set.of("ROLE_USER")).accessToken();

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 배틀에_참여하고_있는_러너가_요청하면 {

            @Test
            @DisplayName("OK와 배틀 정보를 응답한다.")
            void returnOK() {
                배틀_생성_요청(
                        SYSTEM_TOKEN,
                        new BattleCreateRequest(
                                1000, List.of(박성우.getId(), 노준혁.getId(), 박현준.getId())));
                final ExtractableResponse<Response> response = 참여_중인_배틀_조회_요청(박성우_accessToken);

                assertAll(
                        () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                        () ->
                                assertThat(toObject(response, BattleIdResponse.class).id())
                                        .isNotNull());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 요청에_잘못된_토큰을_요청하면 {

            @Test
            @DisplayName("Forbidden을 반환한다.")
            void returnBadRequest() {
                final ExtractableResponse<Response> response = 참여_중인_배틀_조회_요청("invalidToken");

                assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
        }
    }
}
