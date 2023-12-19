package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.FightRequest;
import org.bot0ff.service.FightService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/fight")
@RequiredArgsConstructor
public class FightController {
    private final FightService fightService;

    @PostMapping("/attack")
    public ResponseEntity<?> actionAttack(@RequestBody FightRequest fightRequest) {
        var response = fightService.getStartState("admin", fightRequest);
        return ResponseEntity.ok(response);
    }
}
