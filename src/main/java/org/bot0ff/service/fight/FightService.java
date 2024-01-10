package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.Response;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.EnemyRepository;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final PlayerRepository playerRepository;
    private final EnemyRepository enemyRepository;
    private final FightRepository fightRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    //начать сражение с выбранным enemy
    @Transactional
    public String getStartFightUserVsEnemy(String username, Long targetId) {
        //поиск player в бд
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        //поиск enemy в бд
        var enemy = enemyRepository.findById(targetId);
        if(enemy.isEmpty()) {
            var response = Response.builder()
                    .info("Противник не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден enemy в БД по запросу enemyId: {}", targetId);
            return jsonProcessor.toJson(response);
        }

        //создание id нового сражения
        long newFightId = randomUtil.getRandomId();

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = getNewFight(newFightId, player.get(), null, enemy.get());
        RoundHandler.FIGHT_MAP.put(newFightId, 60);
        fightRepository.save(newFight);

        //сохранение статуса FIGHT у player
        playerRepository.saveNewPlayerFightId(Status.FIGHT.name(), newFightId, player.get().getId());

        //сохранение статуса FIGHT у enemy
        enemyRepository.saveNewEnemyFightId(Status.FIGHT.name(), newFightId, enemy.get().getId());

        var response = Response.builder()
                .player(player.get())
                .fight(newFight)
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //начать сражение с выбранным player
    @Transactional
    public String getStartFightUserVsPlayer(String username, Long targetId) {
        //поиск player в бд
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }
        //поиск opponent в бд
        var opponent = playerRepository.findById(targetId);
        if(opponent.isEmpty()) {
            var response = Response.builder()
                    .info("Противник не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу playerId: {}", targetId);
            return jsonProcessor.toJson(response);
        }

        //создание id нового сражения
        long newFightId = randomUtil.getRandomId();

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = getNewFight(newFightId, player.get(), opponent.get(), null);
        RoundHandler.FIGHT_MAP.put(newFightId, 60);
        fightRepository.save(newFight);

        //сохранение статуса FIGHT у player
        playerRepository.saveNewPlayerFightId(Status.FIGHT.name(), newFightId, player.get().getId());

        //сохранение статуса FIGHT у opponent
        playerRepository.saveNewPlayerFightId(Status.FIGHT.name(), newFightId, opponent.get().getId());

        var response = Response.builder()
                .player(player.get())
                .fight(newFight)
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //текущее состояние сражения
    public String getRefreshCurrentRound(String username) {
        //поиск player в бд
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }

        Fight fight = player.get().getFight();
        //начинает текущий раунд заново и добавляет в map, если отсутствует
        RoundHandler.FIGHT_MAP.putIfAbsent(fight.getId(), 60);
        //устанавливает в ответе текущее время раунда
        fight.setTimeToEndRound(RoundHandler.FIGHT_MAP.get(fight.getId()));

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //атака по выбранному противнику
    //TODO добавить id примененного умения
    public String setAttackPlayer(String username, String targetType, Long targetId) {
        //поиск player в бд
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = Response.builder()
                    .info("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
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
        if(player.get().isRoundActionEnd() | player.get().getStatus().equals(Status.DIE)) {
            var response = Response.builder()
                    .player(player.get())
                    .fight(fight)
                    .status(HttpStatus.OK)
                    .build();
            return jsonProcessor.toJson(response);
        }

        //расчет и сохранение умения и цели, по которой произведено действие
        playerRepository.saveNewPlayerAttack(true, 0L, targetType, targetId, player.get().getId());

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .info("Ход сделан")
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //создание нового сражения
    private Fight getNewFight(Long newFightId, Player player, Player opponent, Enemy enemy) {
        if(opponent == null) {
            return new Fight(newFightId,
                    new ArrayList<>(List.of(player)),
                    new ArrayList<>(List.of(enemy)),
                    1, false, 60);
        }
        else return new Fight(newFightId,
                new ArrayList<>(List.of(player, opponent)),
                new ArrayList<>(List.of()),
                1, false, 60);
    }
}
