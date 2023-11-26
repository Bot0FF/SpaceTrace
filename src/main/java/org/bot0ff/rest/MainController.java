package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MoveResponse;
import org.bot0ff.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class MainController {
    private final MainService mainServiceImpl;

    @GetMapping("/main")
    public ResponseEntity<?> mainPage(@AuthenticationPrincipal(expression = "username") String username) {
        var userState = mainServiceImpl.getPlayerState(username);
        return ResponseEntity.ok(userState);
    }

    @GetMapping("/move/{direction}")
    public ResponseEntity<MoveResponse> moveUser(@AuthenticationPrincipal(expression = "username") String username, @PathVariable String direction) {
        var userPosition = mainServiceImpl.setPlayerPosition(username, direction);
        return ResponseEntity.ok(userPosition);
    }
}
