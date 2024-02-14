package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.service.SpecialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/special")
@RequiredArgsConstructor
public class SpecialController {
    private final SpecialService specialService;

    /** для админа */
    //добавить предмет в инвентарь
    @GetMapping("/thing/add")
    public ResponseEntity<?> addThingToInventory(@RequestParam Long thingId) {
        var response = specialService.addThingToInventory("user", thingId);
        return ResponseEntity.ok(response);
    }

    //удалить предмет из БД
    @GetMapping("/thing/remove")
    public ResponseEntity<?> removeThingFromInventory(@RequestParam Long thingId) {
        var response = specialService.removeThingFromDB("user", thingId);
        return ResponseEntity.ok(response);
    }
}
