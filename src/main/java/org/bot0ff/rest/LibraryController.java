package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.Enemy;
import org.bot0ff.repository.EnemyRepository;
import org.bot0ff.service.LibraryService;
import org.bot0ff.util.JsonProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping("/all")
    public ResponseEntity<?> libraryPage() {
        var response = libraryService.getAllTypes();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/type")
    public ResponseEntity<?> enemyPage(@RequestBody String type) {
        var response = libraryService.getTypeInfo(type);
        return ResponseEntity.ok(response);
    }
}
