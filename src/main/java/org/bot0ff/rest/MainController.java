package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MoveRequest;
import org.bot0ff.dto.main.MoveResponse;
import org.bot0ff.service.ActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ActionService actionService;

    @PostMapping("/move")
    public ResponseEntity<MoveResponse> moveUser(@RequestBody MoveRequest moveRequest) {
        var userPosition = actionService.getUserPosition(moveRequest.getUsername(), moveRequest.getDirection());
        return ResponseEntity.ok(userPosition);
    }
}
