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

@RestController
@RequiredArgsConstructor
@RequestMapping("battle")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleController {

    BattleService battleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BattleResponse createBattle(@RequestBody @Valid BattleCreateRequest request) {

        return battleService.createBattle(request);
    }

    @GetMapping("running")
    @ResponseStatus(HttpStatus.OK)
    public BattleResponse getRunningBattle(Authentication auth) {
        return battleService.getRunningBattle(auth.getName());
    }
}
