package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Обрабатывает запросы для мест, которые можно посетить:
 * - Дом, Магазин, Аптека...
 */

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    /** Взаимодействие с местом */
    @GetMapping("/")
    public ResponseEntity<?> getPlace(@RequestParam Long doorId) {
        String response = "";
        if (doorId.equals(0L)) {
            response = placeService.getPlaceNotFound("user");
        }
        else if(doorId.equals(1L)) {
            response = placeService.getPlaceHome("user");
        }
        else if(doorId.equals(2L)) {
            response = placeService.getPlaceChurch("user");
        }
        return ResponseEntity.ok(response);
    }
}
