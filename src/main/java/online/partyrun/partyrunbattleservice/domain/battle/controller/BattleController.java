package online.partyrun.partyrunbattleservice.domain.battle.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.domain.battle.dto.*;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("battles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleController {

    BattleService battleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BattleIdResponse createBattle(@RequestBody @Valid BattleCreateRequest request) {

        final BattleIdResponse response = battleService.createBattle(request);
        log.info(response.toString());
        return response;
    }

    @GetMapping("join")
    @ResponseStatus(HttpStatus.OK)
    public BattleIdResponse getReadyBattle(Authentication auth) {
        final BattleIdResponse response = battleService.getReadyBattle(auth.getName());
        log.info(response.toString());
        return response;
    }

    @GetMapping("{battleId}")
    @ResponseStatus(HttpStatus.OK)
    public BattleResponse getBattle(@PathVariable("battleId") String battleId, Authentication auth) {
        final BattleResponse response = battleService.getBattle(battleId, auth.getName());
        log.info(response.toString());
        return response;
    }

    @PostMapping("/runners/finished")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse changeRunnerFinished(Authentication auth) {
        return battleService.changeRunnerFinished(auth.getName());
    }
}
