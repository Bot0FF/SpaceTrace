package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;

import org.bot0ff.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/im")
@RequiredArgsConstructor

public class MainController {
    private final MainService mainServiceImpl;

    @GetMapping("/main")
    public ResponseEntity<?> mainPage() {
        var response = mainServiceImpl.getPlayerState("admin");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/move/{direction}")
    public ResponseEntity<?> moveUser(@PathVariable String direction) {
        var response = mainServiceImpl.movePlayer("admin", direction);
        return ResponseEntity.ok(response);
    }

    //@AuthenticationPrincipal(expression = "username") String username
}
