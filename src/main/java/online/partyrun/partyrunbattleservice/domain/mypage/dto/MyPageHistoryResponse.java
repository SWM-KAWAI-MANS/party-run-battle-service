package online.partyrun.partyrunbattleservice.domain.mypage.dto;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;

import java.util.Collections;
import java.util.List;

public record MyPageHistoryResponse(List<MyPageRunningResponse> history) {
    private static final MyPageHistoryResponse EMPTY_HISTORY = new MyPageHistoryResponse(Collections.emptyList());

    public static MyPageHistoryResponse ofBattles(List<Battle> battles, String memberId) {
        if (battles.isEmpty()) {
            return EMPTY_HISTORY;
        }

        return new MyPageHistoryResponse(
                battles.stream()
                        .map(battle -> MyPageRunningResponse.of(battle, memberId))
                        .toList()
        );
    }

    public static MyPageHistoryResponse ofSingle(List<Single> singles) {
        if (singles.isEmpty()) {
            return EMPTY_HISTORY;
        }
        return new MyPageHistoryResponse(
                singles.stream()
                        .map(MyPageRunningResponse::from)
                        .toList()
        );
    }
}
