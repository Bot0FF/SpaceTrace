package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.auth.SettingRequest;
import org.bot0ff.entity.Player;
import org.bot0ff.entity.Status;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.service.MainService;
import org.bot0ff.util.Constants;
import org.bot0ff.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    protected final LocationRepository locationRepository;

    //настройка нового пользователя
    @PostMapping("/setting")
    public ResponseEntity<?> settingUser(@RequestBody SettingRequest settingRequest) {
        String username = "admin";
        var user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            var response = ResponseBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            return ResponseEntity.ok(response);
        }
        if(playerRepository.existsByName(user.getUsername())) {
            var response = ResponseBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        var locationId = Long.parseLong("" + Constants.START_POS_X + Constants.START_POS_Y);
        var location = locationRepository.findById(locationId).orElse(null);
        if(location == null) {
            var response = ResponseBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена location в БД по запросу locationId: {}", locationId);
            return ResponseEntity.ok(response);
        }
        var player = new Player();
        player.setName(user.getUsername());
        player.setX(Constants.START_POS_X);
        player.setY(Constants.START_POS_Y);
        player.setHp(Constants.START_HP);
        player.setMana(Constants.START_MANA);
        player.setLocation(location);
        player.setStatus(Status.ACTIVE);
        playerRepository.save(player);
        var response = mainService.getPlayerState(username);
        return ResponseEntity.ok(response);
    }

    //главная страница
    @GetMapping("/im")
    public ResponseEntity<?> mainPage() {
        var response = mainService.getPlayerState("admin");
        return ResponseEntity.ok(response);
    }

    //смена локации
    @GetMapping("/move/{direction}")
    public ResponseEntity<?> movePlayer(@PathVariable String direction) {
        var response = mainService.movePlayer("admin", direction);
        return ResponseEntity.ok(response);
    }

    //все игроки
    @GetMapping("/players")
    public ResponseEntity<?> allPlayers() {
        var response = mainService.getAllPlayers();
        return ResponseEntity.ok(response);
    }

    //профиль игрока
    @GetMapping("/profile/{name}")
    public ResponseEntity<?> playerProfile(@PathVariable String name) {
        var response = mainService.getPlayerProfile(name);
        return ResponseEntity.ok(response);
    }

    //@AuthenticationPrincipal(expression = "username") String username
}
