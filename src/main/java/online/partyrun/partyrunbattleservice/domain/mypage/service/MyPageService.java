package online.partyrun.partyrunbattleservice.domain.mypage.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.mypage.dto.MyPageTotalResponse;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;
import online.partyrun.partyrunbattleservice.domain.single.repository.SingleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyPageService {

    BattleRepository battleRepository;
    SingleRepository singleRepository;

    public MyPageTotalResponse getMyPageTotal(String memberId) {
        final List<Battle> battles = battleRepository.findAllByRunnersIdExceptRunnerRecords(memberId);
        final List<Single> singles = singleRepository.findAllByRunnerId(memberId);

        return MyPageTotalResponse.of(memberId, battles, singles);
    }
}
