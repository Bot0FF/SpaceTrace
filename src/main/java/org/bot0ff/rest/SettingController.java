package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.Response;
import org.bot0ff.dto.auth.SettingRequest;
import org.bot0ff.entity.Player;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.service.MainService;
import org.bot0ff.util.Constants;
import org.springframework.http.HttpStatus;
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
    private final PlayerRepository playerRepository;
    private final LocationRepository locationRepository;

    //настройка нового пользователя
    @PostMapping("/start/setting")
    public ResponseEntity<?> settingUser(@RequestBody SettingRequest settingRequest) {
        String username = "user";
        var user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            return ResponseEntity.ok(response);
        }
        if(playerRepository.existsByName(user.getUsername())) {
            var response = Response.builder()
                    .info("Игрок с таким именем уже зарегистрирован")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        var locationId = Long.parseLong("" + Constants.START_POS_X + Constants.START_POS_Y);
        var location = locationRepository.findById(locationId).orElse(null);
        if(location == null) {
            var response = Response.builder()
                    .info("Локация не найдена")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена location в БД по запросу locationId: {}", locationId);
            return ResponseEntity.ok(response);
        }
        var player = new Player();
        player.setName(user.getUsername());
        player.setX(Constants.START_POS_X);
        player.setY(Constants.START_POS_Y);
        player.setMana(Constants.START_MANA);
        player.setLocation(location);
        player.setFight(null);
        player.setStatus(Status.ACTIVE);
        player.setHp(Constants.START_HP);
        player.setMana(Constants.START_MANA);
        player.setDamage(Constants.START_DAMAGE);
        player.setRoundActionEnd(false);
        player.setRoundChangeAbility(null);
        player.setRoundTargetType(null);
        player.setRoundTargetId(null);
        playerRepository.save(player);
        var response = mainService.getPlayerState(username);
        return ResponseEntity.ok(response);
    }
}
