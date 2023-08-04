package online.partyrun.partyrunbattleservice.domain.battle.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("battles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleController {

    BattleService battleService;
    Clock clock;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BattleResponse createBattle(@RequestBody @Valid BattleCreateRequest request) {

        return battleService.createBattle(request);
    }

    @GetMapping("join")
    @ResponseStatus(HttpStatus.OK)
    public BattleResponse getReadyBattle(Authentication auth) {
        return battleService.getReadyBattle(auth.getName());
    }

    @GetMapping("clock")
    @ResponseStatus(HttpStatus.OK)
    public LocalDateTime returnClock() {
        return LocalDateTime.now(clock);
    }
}
