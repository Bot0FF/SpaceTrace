package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.model.InfoResponse;
import org.bot0ff.model.MainResponse;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.ThingRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.fight.FightService;
import org.bot0ff.service.generate.EntityGenerator;
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
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", optionalPlayer.get().getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        //если у unit статус WIN или LOSS, показываем результат сражения и меняем статус на ACTIVE
        if(player.getStatus().equals(Status.WIN)) {
            var response = jsonProcessor
                    .toJsonMain(new MainResponse(player, location, "Победа!"));
            unitRepository.setStatus(Status.ACTIVE.name(), player.getId());
            return response;
        }
        else if(player.getStatus().equals(Status.LOSS)) {
            var response = jsonProcessor
                    .toJsonMain(new MainResponse(player, location, "Поражение..."));
            unitRepository.setStatus(Status.ACTIVE.name(), player.getId());
            return response;
        }

        return jsonProcessor
                .toJsonMain(new MainResponse(player, location, null));
    }

    //смена локации unit
    @Transactional
    public String moveUnit(String name, String direction) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", optionalPlayer.get().getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        int newPosX = 0;
        int newPosY = 0;
        switch (direction) {
            case "up" -> {
                newPosX = location.getX();
                newPosY = location.getY() + 1;
            }
            case "left" -> {
                newPosX = location.getX() - 1;
                newPosY = location.getY();
            }
            case "right" -> {
                newPosX = location.getX() + 1;
                newPosY = location.getY();
            }
            case "down" -> {
                newPosX = location.getX();
                newPosY = location.getY() - 1;
            }
        }

        //поиск локации для перехода
        Optional<Location> optionalNewLocation = locationRepository.findById(Long.valueOf("" + newPosX + newPosY));
        if(optionalNewLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Туда нельзя перейти"));
            log.info("Не найдена location в БД по запросу locationId: {}", newPosX + "/" + newPosY);
            return response;
        }
        Location newLocation = optionalNewLocation.get();


        //шанс появления enemy на локации
        if(randomUtil.getChanceCreateEnemy()) {
            Long newOpponent = entityGenerator.getNewAiUnitId(newLocation);
            if(!newOpponent.equals(0L)) {
                newLocation.getAis().add(newOpponent);
                //шанс нападения enemy на unit
                if (randomUtil.getRandom1or2() == 1) {
                    fightService.setStartFight(null, newOpponent, player.getId());
                }
            }
        }

        //сохранение новой локации у unit
        location.getUnits().removeIf(u -> u.equals(player.getId()));
        locationRepository.save(location);
        optionalNewLocation.get().getUnits().add(player.getId());
        locationRepository.save(newLocation);
        player.setLocationId(newLocation.getId());
        unitRepository.save(player);

        return jsonProcessor
                .toJsonMain(new MainResponse(player, newLocation, "Ты перешел на локацию: " + optionalNewLocation.get().getName()));
    }

    //добавляет вещь в инвентарь с локации
    @Transactional
    public String takeLocationThing(String name, Long thingId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", player.getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        var optionalThing = thingRepository.findById(thingId);
        if(optionalThing.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Вещь не найдена"));
            log.info("Не найдена вещь в БД по запросу thingId: {}", thingId);
            return response;
        }
        Thing thing = optionalThing.get();

        //если у вещи есть владелец, отправляем уведомление
        if(thing.getOwnerId() != null) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("У вещи есть владелец"));
            log.info("Попытка забрать вещь, у которой есть владелец, thingId: {}", thingId);
            return response;
        }

        //сохраняем нового владельца и удаляем id вещи с локации, если привязана
        thing.setOwnerId(player.getId());
        thingRepository.save(thing);
        location.getThings().removeIf(th -> th.equals(thingId));
        locationRepository.save(location);

        return jsonProcessor
                .toJsonMain(new MainResponse(player, location, "Вещь (" + thing.getName() + ") добавлена в инвентарь"));
    }

    //список ais на локации
    public String getLocationAis(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", optionalPlayer.get().getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        List<Unit> ais = unitRepository.findAllById(location.getAis());

        return jsonProcessor.toJson(ais);
    }

    //список units на локации
    public String getLocationUnits(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", optionalPlayer.get().getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        location.getUnits().removeIf(u -> u.equals(player.getId()));
        List<Unit> units = unitRepository.findAllById(location.getUnits());

        return jsonProcessor.toJson(units);
    }

    //список вещей на локации
    public String getLocationThings(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", optionalPlayer.get().getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        //получаем список вещей на локации
        List<Thing> things = thingRepository.findAllById(location.getThings());

        return jsonProcessor.toJson(things);
    }
}
