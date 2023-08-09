package online.partyrun.partyrunbattleservice.domain.battle.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("battles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleController {

    BattleService battleService;

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

    @GetMapping("{battleId}")
    @ResponseStatus(HttpStatus.OK)
    public FinishedBattleResponse getFinishedBattle(
            @PathVariable("battleId") String battleId, Authentication auth) {
        return battleService.getFinishedBattle(battleId, auth.getName());
    }

    @PostMapping("/runners/finished")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse changeRunnerFinished(Authentication auth) {
        return battleService.changeRunnerFinished(auth.getName());
    }
}
