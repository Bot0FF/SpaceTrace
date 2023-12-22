package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final PlayerRepository playerRepository;
    private final LocationRepository locationRepository;
    private final JsonProcessor jsonProcessor;

    //состояние user после обновления страницы
    public String getPlayerState(String username) {
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        var location = locationRepository.findById(player.get().getLocationId());
        if(location.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .player(player.get())
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена location в БД по запросу locationId: {}", player.get().getLocationId());
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .player(player.get())
                .enemies(location.get().getEnemies())
                .players(location.get().getPlayers())
                .content(location.get().getName())
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //смена локации user
    @Transactional
    public String movePlayer(String username, String direction) {
        var player = playerRepository.findByName(username).orElse(null);
        if(player == null) {
            var response = ResponseBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        switch (direction) {
            case "up" -> {
                if(player.getY() + 1 <= Constants.MAX_MAP_LENGTH) {
                    player.setY(player.getY() + 1);
                }
            }
            case "left" -> {
                if(player.getX() - 1 > 0) {
                    player.setX(player.getX() - 1);
                }
            }
            case "right" -> {
                if(player.getX() + 1 <= Constants.MAX_MAP_LENGTH) {
                     player.setX(player.getX() + 1);
                }
            }
            case "down" -> {
                if(player.getY() - 1 > 0) {
                     player.setY(player.getY() - 1);
                }
            }
        }
        var newLocationId = Long.parseLong("" + player.getX() + player.getY());
        playerRepository.saveNewPlayerPosition(player.getX(), player.getY(), newLocationId, player.getName());
        var location = locationRepository.findById(newLocationId);
        if(location.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .player(player)
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена newLocation в БД по запросу newLocationId: {}", newLocationId);
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .player(player)
                .enemies(location.get().getEnemies())
                .players(location.get().getPlayers())
                .content(location.get().getName())
                .status(HttpStatus.OK)
                .build();
        return jsonProcessor.toJson(response);
    }

    //все players
    public String getAllPlayers() {
        var players = playerRepository.findAll();
        if(players.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдены players в БД по запросу getAllPlayers");
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .players(players)
                .content(String.valueOf(players.size()))
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //профиль player
    public String getPlayerProfile(String username) {
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .content("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .player(player.get())
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }
}
