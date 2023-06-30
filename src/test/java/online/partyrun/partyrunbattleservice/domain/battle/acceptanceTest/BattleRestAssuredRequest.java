package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import online.partyrun.partyrunbattleservice.acceptance.SimpleRestAssured;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;

import java.util.Map;

public class BattleRestAssuredRequest {
    private static final String PREFIX_URL = "api";
    private static final String BATTLE_URL = "battle";
    public static final ExtractableResponse<Response> 배틀_생성_요청(BattleCreateRequest request) {
        return SimpleRestAssured.post(String.format("/%s/%s", PREFIX_URL, BATTLE_URL), request);
    }

    public static final ExtractableResponse<Response> 배틀_조회_요청(String accessToken) {
        return SimpleRestAssured.get(String.format("/%s/%s/running", PREFIX_URL, BATTLE_URL), Map.of("Authorization", accessToken));
    }
}
