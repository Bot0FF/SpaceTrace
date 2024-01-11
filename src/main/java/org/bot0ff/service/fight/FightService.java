package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.Response;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.AttackType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.TeamType;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final UnitRepository unitRepository;
    private final FightRepository fightRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

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

        //создание id нового сражения
        long newFightId = randomUtil.getRandomId();

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = getNewFight(newFightId, player.get(), opponent.get());
        RoundHandler.FIGHT_MAP.put(newFightId, 60);
        fightRepository.save(newFight);

        //сохранение статуса FIGHT у player
        unitRepository.saveNewFight(
                false,
                Status.FIGHT.name(),
                newFightId,
                TeamType.ONE.name(),
                0L,
                AttackType.NONE.name(),
                0L,
                player.get().getId());

        //сохранение статуса FIGHT у enemy
        unitRepository.saveNewFight(
                false,
                Status.FIGHT.name(),
                newFightId,
                TeamType.TWO.name(),
                0L,
                AttackType.NONE.name(),
                0L,
                opponent.get().getId());

        var response = Response.builder()
                .player(player.get())
                .fight(newFight)
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

        //начинает текущий раунд заново и добавляет в map, если отсутствует
        RoundHandler.FIGHT_MAP.putIfAbsent(fight.getId(), 60);
        //устанавливает в ответе текущее время раунда
        fight.setTimeToEndRound(RoundHandler.FIGHT_MAP.get(fight.getId()));

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
        //начинает текущий раунд заново и добавляет в map, если отсутствует
        RoundHandler.FIGHT_MAP.putIfAbsent(fight.getId(), 60);
        //устанавливает в ответе текущее время раунда
        fight.setTimeToEndRound(RoundHandler.FIGHT_MAP.get(fight.getId()));

        //если ход уже был сделан, возвращает текущее состояние player
        if(player.get().isActionEnd()) {
            var response = Response.builder()
                    .player(player.get())
                    .fight(fight)
                    .info("Ход уже сделан")
                    .status(1)
                    .build();
            return jsonProcessor.toJson(response);
        }
        if(player.get().getStatus().equals(Status.DIE)) {
            var response = Response.builder()
                    .player(player.get())
                    .fight(fight)
                    .info("Поражение")
                    .status(1)
                    .build();
            return jsonProcessor.toJson(response);
        }

        //TODO сделать поиск выбранного умения в бд и расчет предварительного урона
        // и уменьшение характеристик, если требуется
        // если ability 0, то урон наносится простой атакой
        // добавлять тип (количество атакуемых) атаки, в зависимости от умения

        //расчет и сохранение умения и цели, по которой произведено действие
        unitRepository.saveNewAttack(
                true,
                2L,
                AttackType.ONE.name(),
                targetId,
                player.get().getId());

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .info("Ход сделан")
                .status(1)
                .build();

        return jsonProcessor.toJson(response);
    }

    //создание нового сражения
    private Fight getNewFight(Long newFightId, Unit player, Unit opponent) {
        return new Fight(newFightId,
                new ArrayList<>(List.of(player, opponent)),
                1, false, 60);
    }
}
