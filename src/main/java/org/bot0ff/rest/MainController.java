package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.bot0ff.service.MainService;
import org.bot0ff.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final ProfileService profileService;
    //@AuthenticationPrincipal(expression = "username") String username
    //главная страница
    @GetMapping("/im")
    public ResponseEntity<?> mainPage() {
        var response = mainService.getUnitState("user");
        return ResponseEntity.ok(response);
    }

    //страница профиля
    @GetMapping("/profile")
    public ResponseEntity<?> profilePage() {
        var response = profileService.getUnitProfileState("user");
        return ResponseEntity.ok(response);
    }

    //смена локации
    @GetMapping("/move")
    public ResponseEntity<?> movePlayer(@RequestParam String direction) {
        var response = mainService.moveUnit("user", direction);
        return ResponseEntity.ok(response);
    }

    //список ais на локации
    @GetMapping("/location/ais")
    public ResponseEntity<?> getLocationAis() {
        var response = mainService.getLocationAis("user");
        return ResponseEntity.ok(response);
    }

    //список units на локации
    @GetMapping("/location/units")
    public ResponseEntity<?> getLocationUnits() {
        var response = mainService.getLocationUnits("user");
        return ResponseEntity.ok(response);
    }

    /** Взаимодействие с вещами на локации **/

    //список вещей на локации
    @GetMapping("/location/things")
    public ResponseEntity<?> getLocationThings() {
        var response = mainService.getLocationThings("user");
        return ResponseEntity.ok(response);
    }

    //забрать вещь с локации
    @GetMapping("/location/take")
    public ResponseEntity<?> takeLocationThing(@RequestParam Long thingId) {
        var response = profileService.takeLocationThing("user", thingId);
        return ResponseEntity.ok(response);
    }

    /** Взаимодействие с вещами в инвентаре **/

    //список вещей в инвентаре
    @GetMapping("/inventory/things")
    public ResponseEntity<?> getInventoryThings() {
        var response = profileService.getInventoryThings("user");
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
}
