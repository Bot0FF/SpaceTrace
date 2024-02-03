package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.bot0ff.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    //@AuthenticationPrincipal(expression = "username") String username
    //главная страница
    @GetMapping("/im")
    public ResponseEntity<?> mainPage() {
        var response = mainService.getUnitState("user");
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

    //список вещей на локации
    @GetMapping("/location/things")
    public ResponseEntity<?> getLocationThings() {
        var response = mainService.getLocationThings("user");
        return ResponseEntity.ok(response);
    }
}
