package online.partyrun.partyrunbattleservice.domain.single.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleIdResponse;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleResponse;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleRunnerRecordsRequest;
import online.partyrun.partyrunbattleservice.domain.single.service.SingleService;
import online.partyrun.partyrunbattleservice.global.logging.Logging;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Logging
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("singles")
public class SingleController {

    SingleService singleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleIdResponse createSingle(Authentication auth, @RequestBody @Valid SingleRunnerRecordsRequest request) {
        final String runnerId = auth.getName();
        return singleService.create(runnerId, request);
    }

    @GetMapping("{singleId}")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponse getSingle(Authentication auth, @PathVariable String singleId) {
        final String runnerId = auth.getName();
        return singleService.getSingle(singleId, runnerId);
    }
}
