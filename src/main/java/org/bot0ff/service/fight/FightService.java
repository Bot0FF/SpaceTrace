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

import java.util.List;
import java.util.Optional;

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
    public String getStartFightUserVsEnemy(String username, Long enemyId) {
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
        var enemy = enemyRepository.findById(enemyId);
        if(enemy.isEmpty()) {
            var response = Response.builder()
                    .info("Противник не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден enemy в БД по запросу enemyId: {}", enemyId);
            return jsonProcessor.toJson(response);
        }

        //создание id нового сражения
        long newFightId = randomUtil.getRandomId();

        //добавление нового сражения в map и запуск обработчика раундов
        Fight newFight = new Fight(newFightId,
                List.of(player.get()),
                List.of(enemy.get()),
                1, false, 60);
        RoundHandler.FIGHT_MAP.put(newFightId, 60);
        fightRepository.save(newFight);

        //сохранение статуса FIGHT у player
        player.get().setStatus(Status.FIGHT);
        playerRepository.save(player.get());

        //сохранение статуса FIGHT у enemy
        enemy.get().setStatus(Status.FIGHT);
        enemyRepository.save(enemy.get());
        newFight.getPlayers().removeIf(p -> p.getId().equals(player.get().getId()));

        var response = Response.builder()
                .player(player.get())
                .fight(newFight)
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //текущее состояние сражения
    @Transactional
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

        String info;
        if(player.get().isEndRound()) {
            info = "Ожидание других игроков";
        }
        else {
            info = "Ход не сделан";
        }
        Fight fight = player.get().getFight();
        fight.setTimeToEndRound(RoundHandler.FIGHT_MAP.get(fight.getId()));
        fight.getPlayers().removeIf(p -> p.getId().equals(player.get().getId()));

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .info(info)
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }

    //физическая атака по выбранному enemy
    public String getPhysAttackUserVsEnemy(String username, Long targetId) {
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

        //расчет и сохранение умения и цели, по которой произведено действие
        if(!player.get().isEndRound() | !player.get().getStatus().equals(Status.DIE)) {
            player.get().setRoundDamage(1);
            player.get().setAttackToId(targetId);
            player.get().setEndRound(true);
            playerRepository.save(player.get());
        }
        Fight fight = player.get().getFight();

        var response = Response.builder()
                .player(player.get())
                .fight(fight)
                .info("Ход сделан")
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJson(response);
    }
}
