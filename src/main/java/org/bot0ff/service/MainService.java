package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.ResponseBuilder;
import org.bot0ff.util.ResponseStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final JsonProcessor jsonProcessor;

    //состояние user после обновления страницы
    public String getPlayerState(String username) {
        var user = userRepository.findUserByName(username).orElse(null);
        if(user == null) {
            var response = ResponseBuilder.builder()
                    .user(null)
                    .status(ResponseStatus.ERROR_USER)
                    .build();
            log.info("Не найден user в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        var locationId = Long.parseLong("" + user.getX() + user.getY());
        var location = locationRepository.findById(locationId).orElse(null);
        if(location == null) {
            var response = ResponseBuilder.builder()
                    .user(user)
                    .status(ResponseStatus.ERROR_LOCATION)
                    .build();
            log.info("Не найдена location в БД по запросу locationId: {}", locationId);
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .user(user)
                .enemies(location.getEnemies())
                .players(location.getPlayers())
                .status(ResponseStatus.SUCCESS)
                .build();

        return jsonProcessor.toJson(response);
    }

    //смена локации user
    public String movePlayer(String username, String direction) {
        var user = userRepository.findUserByName(username).orElse(null);
        if(user == null) {
            var response = ResponseBuilder.builder()
                    .user(null)
                    .status(ResponseStatus.ERROR_USER)
                    .build();
            log.info("Не найден user в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        int newPositionX = user.getX();
        int newPositionY = user.getY();
        switch (direction) {
            case "up" -> {
                if(user.getY() + 1 <= Constants.MAX_MAP_LENGTH) {
                    newPositionY = user.getY() + 1;
                }
            }
            case "left" -> {
                if(user.getX() - 1 >= 0) {
                    newPositionX = user.getX() - 1;
                }
            }
            case "right" -> {
                if(user.getX() + 1 <= Constants.MAX_MAP_LENGTH) {
                    newPositionX = user.getX() + 1;
                }
            }
            case "down" -> {
                if(user.getY() - 1 >= 0) {
                    newPositionY = user.getY() - 1;
                }
            }
        }
        var newLocationId = Long.parseLong("" + newPositionX + newPositionY);
        userRepository.saveNewUserPosition(newPositionX, newPositionY, newLocationId, username);

        var response = ResponseBuilder.builder()
                .status(ResponseStatus.SUCCESS)
                .build();

        return jsonProcessor.toJson(response);
    }
}
