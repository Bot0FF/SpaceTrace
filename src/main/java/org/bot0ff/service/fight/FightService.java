package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.ErrorResponse;
import org.bot0ff.dto.FightResponse;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.SubjectRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final UnitRepository unitRepository;
    private final FightRepository fightRepository;
    private final SubjectRepository subjectRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    public static Map<Long, FightHandler> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //начать сражение с выбранным enemy
    @Transactional
    public String setStartFight(String name, Long targetId) {
        //поиск player в бд
        var initiator = unitRepository.findByName(name);
        if(initiator.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Unit не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }
        //поиск opponent в бд
        var opponent = unitRepository.findById(targetId);
        if(opponent.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Противник не найден"));
            log.info("Не найден opponent в БД по запросу username: {}", opponent);
            return response;
        }

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = getNewFight(initiator.get(), opponent.get());
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        initiator.get().setActionEnd(false);
        initiator.get().setStatus(Status.FIGHT);
        initiator.get().setFight(newFight);
        initiator.get().setUnitJson(getUnitJson(initiator.get(), 1));
        unitRepository.save(initiator.get());

        //сохранение статуса FIGHT у enemy
        opponent.get().setActionEnd(true);
        opponent.get().setStatus(Status.FIGHT);
        opponent.get().setFight(newFight);
        initiator.get().setUnitJson(getUnitJson(opponent.get(), 2));
        unitRepository.save(initiator.get());

        newFight.setUnits(List.of(initiator.get(), opponent.get()));

        FIGHT_MAP.put(newFightId, new FightHandler(
                newFightId, unitRepository, fightRepository, subjectRepository, randomUtil
        ));

        return jsonProcessor
                .toJsonFight(new FightResponse(initiator.get(), newFight, "Загрузка сражения..."));
    }

    //текущее состояние сражения
    public String getRefreshCurrentRound(String username) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return response;
        }

        Optional<Fight> fight = Optional.ofNullable(player.get().getFight());
        if(fight.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Сражение завершено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.get().getName());
            return response;
        }

        //если сражение завершено и статус unit НЕ ACTIVE, сбрасываем настройки сражения unit
        if(fight.get().isFightEnd()) {
            player.get().setActionEnd(false);
            player.get().setStatus(Status.ACTIVE);
            player.get().setFight(null);
            player.get().setUnitJson(null);
            unitRepository.save(player.get());
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Сражение завершено"));
            log.info("Попытка обращения к завершенному сражению от player: {}", player.get().getName());
            return response;
        }

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), fight.get(), null));
    }

    //возвращает умения unit
    public String getUnitAbility(String username) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return response;
        }

        //TODO привязать расчет к unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.get().getAbility());

        return jsonProcessor
                .toJson(unitAbility);
    }

    //атака по выбранному противнику
    //TODO добавить id примененного умения
    @Transactional
    public String setAttack(String username, Long abilityId, Long targetId) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return response;
        }

        Optional<Fight> fight = Optional.ofNullable(player.get().getFight());
        if(fight.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Сражение не найдено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.get().getName());
            return response;
        }

        if(player.get().isActionEnd()) {
            return jsonProcessor
                    .toJsonFight(new FightResponse(player.get(), fight.get(), "Ход уже сделан..."));
        }

        //сохранение умения и цели, по которой произведено действие
        UnitJson unitJson = player.get().getUnitJson();
        unitJson.setAbilityId(abilityId);
        unitJson.setTargetId(targetId);
        player.get().setUnitJson(unitJson);
        player.get().setActionEnd(true);
        unitRepository.save(player.get());

        if(fight.get().getUnits().stream().allMatch(Unit::isActionEnd)) {
            FIGHT_MAP.get(player.get().getFight().getId()).setEndRoundTimer(Instant.now().plusSeconds(1));
        }

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), fight.get(), ""));
    }

    //создание объекта UnitJson для сражения
    private UnitJson getUnitJson(Unit unit, int teamNumber) {
        return new UnitJson(
                unit.getId(),
                unit.getHp(), unit.getHp(), 0,
                unit.getMana(), unit.getMana(), 0,
                unit.getDamage(), unit.getDamage(), 0,
                unit.getDefense(), unit.getDefense(), 0,
                teamNumber, 0L, 0L
        );
    }

    //создание нового сражения
    private Fight getNewFight(Unit initiator, Unit opponent) {
        return new Fight(null,
                new ArrayList<>(List.of(initiator, opponent)),
                1, new ArrayList<>(), false,
                Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME).toEpochMilli());
    }



    //заглушка на очистку статуса боя units
    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearFightDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {
            unit.setHp(unit.getHp());
            unit.setActionEnd(false);
            unit.setStatus(Status.ACTIVE);
            unit.setFight(null);
            unitRepository.save(unit);
        }
    }
}
