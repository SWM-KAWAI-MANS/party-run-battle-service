package online.partyrun.partyrunbattleservice.domain.mypage.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.mypage.dto.MyPageTotalResponse;
import online.partyrun.partyrunbattleservice.domain.mypage.service.MyPageService;
import online.partyrun.partyrunbattleservice.global.logging.Logging;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logging
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("mypage")
public class MyPageController {

    MyPageService myPageService;

    @GetMapping("/total")
    public MyPageTotalResponse getMyPageTotalResponse(Authentication auth) {
        return myPageService.getMyPageTotal(auth.getName());
    }
}
