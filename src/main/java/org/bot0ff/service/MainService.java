package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.Response;
import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Player;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.EnemyRepository;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final PlayerRepository playerRepository;
    private final EnemyRepository enemyRepository;
    private final LocationRepository locationRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    //состояние user после обновления страницы
    @Transactional
    public String getPlayerState(String username) {
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        var location = locationRepository.findById(player.get().getLocationId());
        if(location.isEmpty()) {
            var response = Response.builder()
                    .info("Локация не найдена")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена location в БД по запросу locationId: {}", player.get().getLocationId());
            return jsonProcessor.toJson(response);
        }
        var response = Response.builder()
                .player(player.get())
                .location(location.get())
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //смена локации user
    @Transactional
    public String movePlayer(String username, String direction) {
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        switch (direction) {
            case "up" -> {
                if(player.get().getY() + 1 <= Constants.MAX_MAP_LENGTH) {
                    player.get().setY(player.get().getY() + 1);
                }
            }
            case "left" -> {
                if(player.get().getX() - 1 > 0) {
                    player.get().setX(player.get().getX() - 1);
                }
            }
            case "right" -> {
                if(player.get().getX() + 1 <= Constants.MAX_MAP_LENGTH) {
                     player.get().setX(player.get().getX() + 1);
                }
            }
            case "down" -> {
                if(player.get().getY() - 1 > 0) {
                     player.get().setY(player.get().getY() - 1);
                }
            }
        }
        var newLocationId = Long.parseLong("" + player.get().getX() + player.get().getY());
        playerRepository.saveNewPlayerPosition(player.get().getX(), player.get().getY(), newLocationId, player.get().getId());

        //шанс появления enemy на локации
        if(randomUtil.getChanceCreateEnemy()) {
            var location = locationRepository.findById(newLocationId);
            if(location.isPresent()) {
                Enemy newEnemy = getRandomEnemy(location.get());
                enemyRepository.save(newEnemy);
            }
        }

        var location = locationRepository.findById(newLocationId);
        if(location.isEmpty()) {
            var response = Response.builder()
                    .info("Локация не найдена")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена newLocation в БД по запросу newLocationId: {}", newLocationId);
            return jsonProcessor.toJson(response);
        }

        var response = Response.builder()
                .player(player.get())
                .location(location.get())
                .status(HttpStatus.OK)
                .build();
        return jsonProcessor.toJson(response);
    }

    //возвращает случайного enemy в зависимости от локации
    private Enemy getRandomEnemy(Location location) {
        return new Enemy(randomUtil.getRandomId(), location.getX(), location.getY(), location.getLocationType(),
                location, null, "Паук", Status.ACTIVE, 10, 10, 10, false,
                null, null, null);
    }
}
