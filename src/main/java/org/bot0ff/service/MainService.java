package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.ErrorResponse;
import org.bot0ff.dto.MainResponse;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    //состояние user после обновления страницы
    @Transactional
    public String getUserState(String username) {
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return response;
        }
        var location = locationRepository.findById(player.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", player.get().getLocationId());
            return response;
        }
        if(player.get().getStatus().equals(Status.WIN)) {
            var response = jsonProcessor
                    .toJsonMain(new MainResponse(player.get(), location.get(), "Победа!"));
            unitRepository.setStatus(Status.ACTIVE.name(), player.get().getId());
            return response;
        }
        if(player.get().getStatus().equals(Status.LOSS)) {
            var response = jsonProcessor
                    .toJsonMain(new MainResponse(player.get(), location.get(), "Поражение..."));
            unitRepository.setStatus(Status.ACTIVE.name(), player.get().getId());
            return response;
        }

        return jsonProcessor
                .toJsonMain(new MainResponse(player.get(), location.get(), null));
    }

    //смена локации user
    @Transactional
    public String moveUser(String username, String direction) {
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return response;
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
        unitRepository.saveNewPosition(player.get().getX(), player.get().getY(), newLocationId, player.get().getId());

        //шанс появления enemy на локации
        if(randomUtil.getChanceCreateEnemy()) {
            var location = locationRepository.findById(newLocationId);
            if(location.isPresent()) {
                Unit newEnemy = getRandomEnemy(location.get());
                unitRepository.save(newEnemy);
            }
        }

        var location = locationRepository.findById(newLocationId);
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", player.get().getLocationId());
            return response;
        }

        return jsonProcessor
                .toJsonMain(new MainResponse(player.get(), location.get(), "Ты перешел на локацию: " + location.get().getName()));
    }

    //возвращает случайного enemy в зависимости от локации
    private Unit getRandomEnemy(Location location) {
        return new Unit(
                null,
                "*Паук*",
                UnitType.AI,
                Status.ACTIVE,
                false,
                location.getX(),
                location.getY(),
                location,
                1,
                10,
                10,
                4,
                List.of(1L),
                null,
                null);
    }
}
