package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MoveRequest;

import org.bot0ff.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class MainController {
    private final MainService mainServiceImpl;

    @GetMapping("/main")
    public ResponseEntity<?> mainPage(@AuthenticationPrincipal(expression = "username") String username) {
        var response = mainServiceImpl.getPlayerState(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/move")
    public ResponseEntity<?> moveUser(@AuthenticationPrincipal(expression = "username") String username,
                                                @RequestBody MoveRequest moveRequest) {
        var response = mainServiceImpl.setPlayerPosition(username, moveRequest.getDirection());
        return ResponseEntity.ok(response);
    }
}
