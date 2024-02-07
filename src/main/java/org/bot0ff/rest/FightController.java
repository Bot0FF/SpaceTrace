package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.service.fight.FightService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/fight")
@RequiredArgsConstructor
public class FightController {
    private final FightService fightService;

    //начать сражение с выбранным противником
    @GetMapping("/attack")
    public ResponseEntity<?> actionAttack(@RequestParam Long targetId) {
        var response = fightService.setStartFight("user", null, targetId);
        return ResponseEntity.ok(response);
    }

    //текущее состояние сражения
    @GetMapping("/refresh")
    public ResponseEntity<?> actionFight() {
        var response = fightService.getRefreshCurrentRound("user");
        return ResponseEntity.ok(response);
    }

    //умения unit
    @GetMapping("/ability")
    public ResponseEntity<?> getAbility() {
        var response = fightService.getUnitAbility("user");
        return ResponseEntity.ok(response);
    }

    //перемещение по полю сражения
    @GetMapping("/move")
    public ResponseEntity<?> moveOnFightFiled(@RequestParam String direction) {
        var response = fightService.moveOnFightFiled("user", direction);
        return ResponseEntity.ok(response);
    }

    //атака по выбранному противнику оружием
    @GetMapping("/hit/weapon")
    public ResponseEntity<?> hitWeapon(@RequestParam Long targetId) {
        var response = fightService.setApplyWeapon("user", targetId);
        return ResponseEntity.ok(response);
    }

    //атака по выбранному противнику умением
    @GetMapping("/hit/ability")
    public ResponseEntity<?> hitAbility(@RequestParam Long abilityId,
                                         @RequestParam Long targetId) {
        var response = fightService.setApplyAbility("user", abilityId, targetId);
        return ResponseEntity.ok(response);
    }
}
