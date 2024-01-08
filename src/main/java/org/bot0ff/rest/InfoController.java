package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.service.InfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    //все игроки
    @GetMapping("/players")
    public ResponseEntity<?> allPlayers() {
        var response = infoService.getAllPlayers();
        return ResponseEntity.ok(response);
    }

    //профиль игрока
    @GetMapping("/profile/{name}")
    public ResponseEntity<?> playerProfile(@PathVariable String name) {
        var response = infoService.getPlayerProfile(name);
        return ResponseEntity.ok(response);
    }
}
