package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.Enemy;
import org.bot0ff.repository.EnemyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {
    private final EnemyRepository enemyRepository;

    @GetMapping("/all")
    public ResponseEntity<?> libraryPage() {
        String response =
                "{" +
                        "\"entities\": [ {" +
                            "\"id\": 1," +
                            "\"uncover\": false," +
                            "\"name\": \"Enemy\"," +
                            "\"description\": \"About Enemy\" }," +
                            "{\"id\": 2," +
                            "\"uncover\": false," +
                            "\"name\": \"Resource\"," +
                            "\"description\": \"About Resource\" }," +
                            "{\"id\": 3," +
                            "\"uncover\": false," +
                            "\"name\": \"Fight\"," +
                            "\"description\": \"About Fight\" } ] }";

        return ResponseEntity.ok(response);
    }

    @GetMapping("/enemy")
    public ResponseEntity<?> enemyPage() {
        Map<String, String> response = new HashMap<>();
        List<Enemy> enemyList = enemyRepository.findAll();

        return ResponseEntity.ok(enemyList);
    }
}
