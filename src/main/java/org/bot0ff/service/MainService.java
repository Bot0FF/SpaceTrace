package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.MistakeResponse;
import org.bot0ff.dto.MainResponse;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.ThingRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.fight.FightService;
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
    private final ThingRepository thingRepository;

    private final EntityGenerator entityGenerator;
    private final FightService fightService;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    //состояние user после обновления страницы
    @Transactional
    public String getUnitState(String name) {
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        var location = locationRepository.findById(unit.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Локация не найдена"));
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
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }

        var location = locationRepository.findById(unit.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", unit.get().getLocationId());
            return response;
        }

        switch (direction) {
            case "up" -> location.get().setY(location.get().getY() + 1);
            case "left" -> location.get().setX(location.get().getX() - 1);
            case "right" -> location.get().setX(location.get().getX() + 1);
            case "down" -> location.get().setY(location.get().getY() - 1);
        }

        //поиск локации для перехода
        Optional<Location> newLocation = locationRepository.findById(Long.valueOf("" + location.get().getX() + location.get().getY()));
        if(newLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Туда нельзя перейти"));
            log.info("Не найдена location в БД по запросу locationId: {}", location.get().getX() + "/" + location.get().getY());
            return response;
        }


        //шанс появления enemy на локации
        if(randomUtil.getChanceCreateEnemy()) {
            Unit newEnemy = entityGenerator.getNewAiUnit(newLocation.get());
            Long newEnemyId = unitRepository.save(newEnemy).getId();
            newLocation.get().getAis().add(newEnemy.getId());
            //шанс нападения enemy на unit
            if(randomUtil.getRandom1or2() == 1) {
                fightService.setStartFight(null, newEnemyId, unit.get().getId());
            }
        }

        //сохранение новой локации у unit
        location.get().getUnits().removeIf(u -> u.equals(unit.get().getId()));
        locationRepository.save(location.get());
        newLocation.get().getUnits().add(unit.get().getId());
        locationRepository.save(newLocation.get());
        unit.get().setLocationId(newLocation.get().getId());
        unitRepository.save(unit.get());

        return jsonProcessor
                .toJsonMain(new MainResponse(unit.get(), newLocation.get(), "Ты перешел на локацию: " + newLocation.get().getName()));
    }

    //список ais на локации
    public String getLocationAis(String name) {
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        var location = locationRepository.findById(unit.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", unit.get().getLocationId());
            return response;
        }

        List<Unit> ais = unitRepository.findAllById(location.get().getAis());

        return jsonProcessor.toJson(ais);
    }

    //список units на локации
    public String getLocationUnits(String name) {
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        var location = locationRepository.findById(unit.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", unit.get().getLocationId());
            return response;
        }

        location.get().getUnits().removeIf(u -> u.equals(unit.get().getId()));
        List<Unit> units = unitRepository.findAllById(location.get().getUnits());

        return jsonProcessor.toJson(units);
    }

    //список вещей на локации
    public String getLocationThings(String name) {
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        var location = locationRepository.findById(unit.get().getLocationId());
        if(location.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", unit.get().getLocationId());
            return response;
        }

        //получаем список вещей на локации
        List<Thing> things = thingRepository.findAllById(location.get().getThings());

        return jsonProcessor.toJson(things);
    }
}
