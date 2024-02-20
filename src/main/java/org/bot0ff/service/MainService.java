package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.entity.unit.UnitFightEffect;
import org.bot0ff.model.FightResponse;
import org.bot0ff.model.InfoResponse;
import org.bot0ff.model.MainResponse;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.*;
import org.bot0ff.service.fight.*;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.bot0ff.service.fight.FightService.FIGHT_MAP;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final ThingRepository thingRepository;
    private final FightRepository fightRepository;
    private final AbilityRepository abilityRepository;

    private final PhysActionHandler physActionHandler;
    private final MagActionHandler magActionHandler;
    private final AiActionHandler aiActionHandler;
    private final EntityGenerator entityGenerator;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    /** Взаимодействие с локацией */
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
                    setStartFight(null, newOpponent, player.getId());
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

    /** Взаимодействие с противниками на локации */
    //начать сражение с выбранным unit
    @Transactional
    public String setStartFight(String name, Long initiatorId, Long targetId) {
        //определяем инициатора сражения и противника, в зависимости от того, кто напал
        Optional<Unit> optionalInitiator;
        if(name != null) {
            optionalInitiator = unitRepository.findByName(name);
        }
        else {
            optionalInitiator = unitRepository.findById(initiatorId);
        }

        if(optionalInitiator.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }
        Unit initiator = optionalInitiator.get();

        //поиск opponent в бд
        var optionalOpponent = unitRepository.findById(targetId);
        if(optionalOpponent.isEmpty()) {
            resetUnitFight(initiator);
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник уже ушел"));
            log.info("Не найден opponent в БД по запросу targetId: {}", targetId);
            return response;
        }
        Unit opponent = optionalOpponent.get();

        //если противник уже в бою, проверяем есть ли место в бою, для участия
        if(opponent.getStatus().equals(Status.FIGHT)) {
            //ищем количество участников в противоположной противника команде
            List<Unit> initiatorTeam = opponent.getFight().getUnits().stream().filter(unit -> !unit.getTeamNumber().equals(opponent.getTeamNumber())).toList();
            if(initiatorTeam.size() >= Constants.MAX_COUNT_FIGHT_TEAM) {
                resetUnitFight(initiator);
                var response = jsonProcessor
                        .toJsonInfo(new InfoResponse("В сражении достаточно участников"));
                log.info("Нет места для сражения с противником - opponentId: {}", opponent.getId());
                return response;
            }
            //добавляем нападающего в противоположную команду в случайное место на поле сражения
            else {
                if(opponent.getTeamNumber().equals(1L)) {
                    setUnitFight(initiator, opponent.getFight(), 2L);
                }
                else {
                    setUnitFight(initiator, opponent.getFight(), 1L);
                }
                //отправляем ответ со статусом unit FIGHT
                return jsonProcessor
                        .toJsonFight(new FightResponse(initiator, opponent.getFight(), null, ""));
            }
        }

        //создание и сохранение нового сражения
        Fight newFight = getNewFight(initiator, opponent);
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        setUnitFight(initiator, newFight, 1L);

        //сохранение статуса FIGHT у enemy
        setUnitFight(opponent, newFight, 2L);

        //добавление нового сражения в map и запуск обработчика раундов
        FIGHT_MAP.put(newFightId, new FightHandler(
                newFightId,
                unitRepository,
                fightRepository,
                abilityRepository,
                aiActionHandler,
                entityGenerator,
                physActionHandler,
                magActionHandler
        ));

        //если инициатор сражения AI возвращаем null
        if(initiator.getUnitType().equals(UnitType.AI)) {
            return null;
        }

        return jsonProcessor
                .toJsonFight(new FightResponse(initiator, newFight, null, ""));
    }

    /** Взаимодействие с предметами на локации */
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

    /** Вспомогательные методы */
    //создание нового сражения
    private Fight getNewFight(Unit initiator, Unit opponent) {
        return new Fight(null,
                new ArrayList<>(List.of(initiator, opponent)),
                1, new ArrayList<>(List.of("")), false, new ArrayList<>(), new ArrayList<>());
    }

    //сохранение настроек нового сражения unit
    private void setUnitFight(Unit unit, Fight newFight, Long teamNumber) {
        unit.setActionEnd(false);
        unit.setStatus(Status.FIGHT);
        unit.setFight(newFight);
        unit.setTeamNumber(teamNumber);
        unit.setFightStep(List.of());
        unit.setPointAction(unit.getMaxPointAction());
        unit.setLinePosition((long) randomUtil.getRandomFromTo(0, Constants.FIGHT_LINE_LENGTH));
        unit.setFightEffect(new UnitFightEffect());
        unitRepository.save(unit);
    }

    //сброс настроек сражения unit при ошибке
    private void resetUnitFight(Unit unit) {
        unit.setStatus(Status.ACTIVE);
        unit.setActionEnd(false);
        unit.setFight(null);
        unit.setFightStep(null);
        unit.setLinePosition(null);
        unit.setFightEffect(null);
        unit.setTeamNumber(null);
        unitRepository.save(unit);
    }
}
