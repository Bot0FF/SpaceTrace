package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.service.fight.FightService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Обрабатывает запросы для страниц:
 * - Сражение
 */

@Validated
@RestController
@RequestMapping("/api/fight")
@RequiredArgsConstructor
public class FightController {
    private final FightService fightService;

    //текущее состояние сражения
    @GetMapping("/refresh")
    public ResponseEntity<?> actionFight() {
        var response = fightService.getRefreshCurrentRound("user");
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

    //завершить ход unit
    @GetMapping("/action/end")
    public ResponseEntity<?> setActionEnd() {
        var response = fightService.setActionEnd("user");
        return ResponseEntity.ok(response);
    }
}
