package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.bot0ff.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        var response = mainService.getPlayerState("admin");
        return ResponseEntity.ok(response);
    }

    //смена локации
    @GetMapping("/move/{direction}")
    public ResponseEntity<?> movePlayer(@PathVariable String direction) {
        var response = mainService.movePlayer("admin", direction);
        return ResponseEntity.ok(response);
    }

}
