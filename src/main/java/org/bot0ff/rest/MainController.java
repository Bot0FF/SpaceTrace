package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;

import org.bot0ff.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/im")
@RequiredArgsConstructor

public class MainController {
    private final MainService mainService;

    @GetMapping("/main")
    public ResponseEntity<?> mainPage() {
        var response = mainService.getPlayerState("admin");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/move/{direction}")
    public ResponseEntity<?> moveUser(@PathVariable String direction) {
        var response = mainService.movePlayer("admin", direction);
        return ResponseEntity.ok(response);
    }

    //@AuthenticationPrincipal(expression = "username") String username
}
