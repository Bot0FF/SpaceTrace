package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Обрабатывает запросы для страниц:
 * - Информация об игроке (profile)
 * - Список вещей в инвентаре и взаимодействие с ними (profile/inventory)
 * - Распределение аттрибутов (profile/attribute)
 * - Уровень навыков (profile/skill)
 * - Список и выбор умений (profile/ability)
 */

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    /** Взаимодействие с профилем игрока */
    //страница профиля
    @GetMapping()
    public ResponseEntity<?> profilePage() {
        var response = profileService.getUnitProfileState("user");
        return ResponseEntity.ok(response);
    }

    /** Взаимодействие с инвентарем игрока */
    //страница профиля
    @GetMapping("/inventory/all")
    public ResponseEntity<?> getUnitInventory() {
        var response = profileService.getUnitInventory("user");
        return ResponseEntity.ok(response);
    }

    //удалить вещь из инвентаря
    @GetMapping("/inventory/remove")
    public ResponseEntity<?> removeInventoryThings(@RequestParam Long thingId) {
        var response = profileService.removeInventoryThing("user", thingId);
        return ResponseEntity.ok(response);
    }

    //надеть вещь из инвентаря
    @GetMapping("/inventory/puton")
    public ResponseEntity<?> putOnInventoryThings(@RequestParam Long thingId) {
        var response = profileService.putOnInventoryThing("user", thingId);
        return ResponseEntity.ok(response);
    }

    //надеть снять надетую вещь
    @GetMapping("/inventory/takeoff")
    public ResponseEntity<?> takeOffInventoryThings(@RequestParam Long thingId) {
        var response = profileService.takeOffInventoryThing("user", thingId);
        return ResponseEntity.ok(response);
    }

    /** Взаимодействие с аттрибутами игрока */
    //повышение аттрибутов
    @GetMapping("/attribute/up")
    public ResponseEntity<?> upAttribute(@RequestParam String attribute) {
        var response = profileService.upAttribute("user", attribute);
        return ResponseEntity.ok(response);
    }

    //понижение аттрибутов
    @GetMapping("/attribute/down")
    public ResponseEntity<?> downAttribute(@RequestParam String attribute) {
        var response = profileService.downAttribute("user", attribute);
        return ResponseEntity.ok(response);
    }

    /** Взаимодействие с умениями игрока */
    //список умений
    @GetMapping("/abilities/all")
    public ResponseEntity<?> getAllUnitAbilities() {
        var response = profileService.getAllUnitAbilities("user");
        return ResponseEntity.ok(response);
    }

    //добавить в избранные умения
    @GetMapping("/abilities/current/add")
    public ResponseEntity<?> addCurrentUnitAbilities(@RequestParam Long abilityId) {
        var response = profileService.addCurrentUnitAbilities("user", abilityId);
        return ResponseEntity.ok(response);
    }

    //удалить из избранных умений
    @GetMapping("/abilities/current/remove")
    public ResponseEntity<?> removeCurrentUnitAbilities(@RequestParam Long abilityId) {
        var response = profileService.removeCurrentUnitAbilities("user", abilityId);
        return ResponseEntity.ok(response);
    }

    /** для админа */
    //добавить предмет в инвентарь
    @GetMapping("/special/thing/add")
    public ResponseEntity<?> addThingToInventory(@RequestParam Long thingId) {
        var response = profileService.addThingToInventory("user", thingId);
        return ResponseEntity.ok(response);
    }

    //удалить предмет из БД
    @GetMapping("/special/thing/remove")
    public ResponseEntity<?> removeThingFromInventory(@RequestParam Long thingId) {
        var response = profileService.removeThingFromDB("user", thingId);
        return ResponseEntity.ok(response);
    }
}
