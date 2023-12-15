package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.Enemy;
import org.bot0ff.repository.EnemyRepository;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class LibraryController {
    private final EnemyRepository enemyRepository;

    @GetMapping("/all")
    public ResponseEntity<?> libraryPage() {
        var allItems = new JSONObject();
        allItems.put("Существа", "/api/library/enemy");
        allItems.put("Ресурсы", "/api/library/resource");
        allItems.put("Сражения", "/api/library/battle");
        allItems.put("Развитие", "/api/library/evolution");

        return ResponseEntity.ok(allItems);
    }

    @GetMapping("/enemy")
    public ResponseEntity<?> enemyPage() {
        var enemies = new JSONObject();
        List<Enemy> enemyList = enemyRepository.findAll();
        enemies.put("enemies", enemyList);

        return ResponseEntity.ok(enemies);
    }

    @GetMapping("/resource")
    public ResponseEntity<?> resourcePage() {
        var resources = new JSONObject();
        resources.put("resources", "resources");

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/battle")
    public ResponseEntity<?> battlePage() {
        var battle = new JSONObject();
        battle.put("battle", "battle");

        return ResponseEntity.ok(battle);
    }

    @GetMapping("/evolution")
    public ResponseEntity<?> evolutionPage() {
        var evolution = new JSONObject();
        evolution.put("evolution", "evolution");

        return ResponseEntity.ok(evolution);
    }
}
