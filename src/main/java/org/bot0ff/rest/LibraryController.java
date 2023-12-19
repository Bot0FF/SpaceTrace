package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.LibraryRequest;
import org.bot0ff.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @PostMapping("/name")
    public ResponseEntity<?> enemyPage(@RequestBody LibraryRequest libraryRequest) {
        var response = libraryService.getEntityInfo(libraryRequest.getName());
        return ResponseEntity.ok(response);
    }
}
