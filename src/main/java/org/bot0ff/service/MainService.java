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
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public String getUnitState(String name) {
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        var location = locationRepository.findById(unit.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", unit.get().getLocationId());
            return response;
        }

        //если у unit статус WIN или LOSS, показываем результат сражения и меняем статус на ACTIVE
        if(unit.get().getStatus().equals(Status.WIN)) {
            var response = jsonProcessor
                    .toJsonMain(new MainResponse(unit.get(), location.get(), "Победа!"));
            unitRepository.setStatus(Status.ACTIVE.name(), unit.get().getId());
            return response;
        }
        if(unit.get().getStatus().equals(Status.LOSS)) {
            var response = jsonProcessor
                    .toJsonMain(new MainResponse(unit.get(), location.get(), "Поражение..."));
            unitRepository.setStatus(Status.ACTIVE.name(), unit.get().getId());
            return response;
        }

        return jsonProcessor
                .toJsonMain(new MainResponse(unit.get(), location.get(), null));
    }

    //смена локации unit
    @Transactional
    public String moveUnit(String name, String direction) {
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }

        switch (direction) {
            case "up" -> unit.get().setY(unit.get().getY() + 1);
            case "left" -> unit.get().setX(unit.get().getX() - 1);
            case "right" -> unit.get().setX(unit.get().getX() + 1);
            case "down" -> unit.get().setY(unit.get().getY() - 1);
        }

        //поиск локации для перехода
        Optional<Location> newLocation = locationRepository.findById(Long.valueOf("" + unit.get().getX() + unit.get().getY()));
        if(newLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Туда нельзя перейти"));
            log.info("Не найдена location в БД по запросу locationId: {}", unit.get().getLocationId());
            return response;
        }

        //сохранение новой локации у unit
        unit.get().setX(newLocation.get().getX());
        unit.get().setY(newLocation.get().getY());
        unit.get().setLocation(newLocation.get());
        unitRepository.save(unit.get());

        //шанс появления enemy на локации
        if(randomUtil.getChanceCreateEnemy()) {
            Unit newEnemy = getRandomEnemy(newLocation.get());
            unitRepository.save(newEnemy);
        }

        return jsonProcessor
                .toJsonMain(new MainResponse(unit.get(), newLocation.get(), "Ты перешел на локацию: " + newLocation.get().getName()));
    }

    //TODO сделать генерацию случайного противника в зависимости от локации
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
                10,
                10,
                10,
                10,
                10,
                4,
                List.of(1L),
                null,
                null,
                null,
                null,
                null);
    }
}
