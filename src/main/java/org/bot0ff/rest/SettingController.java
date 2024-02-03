package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.MistakeResponse;
import org.bot0ff.dto.auth.SettingRequest;
import org.bot0ff.entity.Unit;
import org.bot0ff.dto.unit.UnitEffect;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.service.MainService;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SettingController {
    private final MainService mainService;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final JsonProcessor jsonProcessor;

    //настройка нового пользователя
    @PostMapping("/start/setting")
    public ResponseEntity<?> settingUser(@RequestBody SettingRequest settingRequest) {
        String username = "user";
        var user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return ResponseEntity.ok(response);
        }
        var locationId = Long.parseLong("" + Constants.START_POS_X + Constants.START_POS_Y);
        var location = locationRepository.findById(locationId).orElse(null);
        if(location == null) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", locationId);
            return ResponseEntity.ok(response);
        }
        var unit = new Unit();
        unit.setId(null);
        unit.setName(user.getUsername());
        unit.setStatus(Status.ACTIVE);
        unit.setActionEnd(false);
        unit.setLocation(location);
        unit.setHp(Constants.START_HP);
        unit.setMana(Constants.START_MANA);
        unit.setDamage(Constants.START_DAMAGE);
        unit.setDefense(Constants.START_DEFENSE);
        unit.setFight(null);
        unit.setUnitEffect(new UnitEffect());
        unitRepository.save(unit);
        var response = mainService.getUnitState(username);
        return ResponseEntity.ok(response);
    }
}
