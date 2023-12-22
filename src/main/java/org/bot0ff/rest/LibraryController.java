package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.main.LibraryRequest;
import org.bot0ff.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping("/{type}")
    public ResponseEntity<?> getEntitiesType(@PathVariable String type) {
        var response = libraryService.getEntityType(type);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/name")
    public ResponseEntity<?> enemyPage(@RequestBody LibraryRequest libraryRequest) {
        var response = libraryService.getEntityInfo(libraryRequest.getName());
        return ResponseEntity.ok(response);
    }
}
