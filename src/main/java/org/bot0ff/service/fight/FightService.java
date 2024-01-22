package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.Response;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.AttackType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public String getStartFight(String username, Long targetId) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(0)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        //поиск enemy в бд
        var opponent = unitRepository.findById(targetId);
        if(opponent.isEmpty()) {
            var response = Response.builder()
                    .info("Противник не найден")
                    .status(0)
                    .build();
            log.info("Не найден enemy в БД по запросу enemyId: {}", targetId);
            return jsonProcessor.toJson(response);
        }

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = getNewFight(player.get(), opponent.get());
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        unitRepository.saveNewFight(
                false,
                Status.FIGHT.name(),
                newFightId,
                1L,
                0L,
                AttackType.NONE.name(),
                0L,
                player.get().getId()
        );

        //сохранение статуса FIGHT у enemy
        unitRepository.saveNewFight(
                false,
                Status.FIGHT.name(),
                newFightId,
                2L,
                0L,
                AttackType.NONE.name(),
                0L,
                opponent.get().getId()
        );

        FIGHT_MAP.put(newFightId, new FightHandler(newFightId, unitRepository, fightRepository, randomUtil));

        var response = Response.builder()
                .player(player.get())
                .fight(newFight)
                .info(username + " напал на противника " + opponent.get().getName())
                .status(1)
                .build();

        return jsonProcessor.toJson(response);
    }

    //текущее состояние сражения
    public String getRefreshCurrentRound(String username) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(0)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }

        Fight fight = player.get().getFight();
        if(fight == null) {
            var response = Response.builder()
                    .info("Сражение не найдено")
                    .status(0)
                    .build();
            return jsonProcessor.toJson(response);
        }

        if(fight.isFightEnd()) {
            var response = Response.builder()
                    .info("Сражение не найдено")
                    .status(0)
                    .build();
            return jsonProcessor.toJson(response);
        }

        //устанавливает в ответе текущее время раунда
        fight.setTimeToEndRound(FIGHT_MAP.get(fight.getId()).getRoundTimer());

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .status(1)
                .build();

        return jsonProcessor.toJson(response);
    }

    //атака по выбранному противнику
    //TODO добавить id примененного умения
    @Transactional
    public String setAttack(String username, Long ability, Long targetId) {
        //поиск player в бд
        var player = unitRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(0)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }

        Fight fight = player.get().getFight();
        if(fight == null) {
            var response = Response.builder()
                    .info("Сражение не найдено")
                    .status(0)
                    .build();
            return jsonProcessor.toJson(response);
        }

        //устанавливает в ответе текущее время раунда
        fight.setTimeToEndRound(FIGHT_MAP.get(fight.getId()).getRoundTimer());

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

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .info(info)
                .status(1)
                .build();

        return jsonProcessor.toJson(response);
    }

    //создание нового сражения
    private Fight getNewFight(Unit player, Unit opponent) {
        return new Fight(null,
                new ArrayList<>(List.of(player, opponent)),
                1, "", false, 10);
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
