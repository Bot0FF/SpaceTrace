package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.Enemy;
import org.bot0ff.repository.EnemyRepository;
import org.bot0ff.util.JsonProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final EnemyRepository enemyRepository;
    private final JsonProcessor jsonProcessor;

    @GetMapping("/all")
    public ResponseEntity<?> libraryPage() {
        var allItems = Map.of(
                "Существа", "/api/library/enemy",
                "Ресурсы", "/api/library/resource",
                "Сражения", "/api/library/battle",
                "Развитие", "/api/library/evolution"
                );

        return ResponseEntity.ok(allItems);
    }

    @GetMapping("/enemy")
    public ResponseEntity<?> enemyPage() {
        List<Enemy> enemyList = enemyRepository.findAll();
        var enemies = Map.of(
                "enemies", enemyList
        );

        return ResponseEntity.ok(enemies);
    }

    @GetMapping("/resource")
    public ResponseEntity<?> resourcePage() {
        var resources = Map.of(
                "resource", "resource"
        );

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/battle")
    public ResponseEntity<?> battlePage() {
        var battle = Map.of(
                "battle", "battle"
        );

        return ResponseEntity.ok(battle);
    }

    @GetMapping("/evolution")
    public ResponseEntity<?> evolutionPage() {
        var evolution = Map.of(
                "evolution", "evolution"
        );

        return ResponseEntity.ok(evolution);
    }
}
