package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.ErrorResponse;
import org.bot0ff.dto.FightResponse;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.AttackType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final UnitRepository unitRepository;
    private final FightRepository fightRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    public static final Executor EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    public static Map<Long, FightHandler> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //начать сражение с выбранным enemy
    @Transactional
    public String setStartFight(String username, Long targetId) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", username);
            return response;
        }
        //поиск enemy в бд
        var opponent = unitRepository.findById(targetId);
        if(opponent.isEmpty()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Противник не найден"));
            log.info("Не найден opponent в БД по запросу username: {}", opponent);
            return response;
        }

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = getNewFight(player.get(), opponent.get());
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        player.get().setActionEnd(false);
        player.get().setStatus(Status.FIGHT);
        player.get().setFight(newFight);
        player.get().set_teamType(1L);
        player.get().set_damage(0L);
        player.get().set_attackType(AttackType.NONE);
        player.get().set_targetId(0L);
        unitRepository.save(player.get());

        //сохранение статуса FIGHT у enemy
        opponent.get().setActionEnd(false);
        opponent.get().setStatus(Status.FIGHT);
        opponent.get().setFight(newFight);
        opponent.get().set_teamType(2L);
        opponent.get().set_damage(0L);
        opponent.get().set_attackType(AttackType.NONE);
        opponent.get().set_targetId(0L);
        unitRepository.save(player.get());

        newFight.setUnits(List.of(player.get(), opponent.get()));

        FIGHT_MAP.put(newFightId, new FightHandler(newFightId, unitRepository, fightRepository, randomUtil));

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), newFight, username + " напал на противника " + opponent.get().getName()));
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
                    .toJsonError(new ErrorResponse("Сражение не найдено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.get().getName());
            return response;
        }

        if(fight.get().isFightEnd()) {
            var response = jsonProcessor
                    .toJsonError(new ErrorResponse("Сражение уже завершено"));
            log.info("Попытка обращения к завершенному сражению от player: {}", player.get().getName());
            return response;
        }

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), fight.get(), null));
    }

    //атака по выбранному противнику
    //TODO добавить id примененного умения
    @Transactional
    public String setAttack(String username, Long ability, Long targetId) {
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

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        //TODO сделать поиск выбранного умения в бд и расчет предварительного урона
        // и уменьшение характеристик, если требуется
        // если ability 0, то урон наносится простой атакой
        // добавлять тип (количество атакуемых) атаки, в зависимости от умения

        //расчет и сохранение умения и цели, по которой произведено действие
        String info;
        if(!player.get().isActionEnd()) {
            unitRepository.saveNewAttack(
                    true,
                    6L,
                    AttackType.OPPONENT.name(),
                    targetId,
                    player.get().getId());
            info = "Использовано умение: " + "Обычная атака";
        }
        else {
            info = "Ход уже сделан";
        }

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), fight.get(), info));
    }

    //создание нового сражения
    private Fight getNewFight(Unit player, Unit opponent) {
        return new Fight(null,
                new ArrayList<>(List.of(player, opponent)),
                1, "", false, Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME).toEpochMilli());
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
            unit.set_teamType(null);
            unit.set_damage(null);
            unit.set_attackType(null);
            unit.set_targetId(null);
            unitRepository.save(unit);
        }
    }
}
