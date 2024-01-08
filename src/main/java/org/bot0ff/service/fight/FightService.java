package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.response.FightBuilder;
import org.bot0ff.entity.*;
import org.bot0ff.repository.AbilityRepository;
import org.bot0ff.repository.EnemyRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.dto.response.MainBuilder;
import org.bot0ff.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final PlayerRepository playerRepository;
    private final EnemyRepository enemyRepository;
    private final AbilityRepository abilityRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    //начать сражение с выбранным enemy
    public String getStartFightUserVsEnemy(String username, Long enemyId) {
        //поиск player в бд
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = MainBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJsonMainResponse(response);
        }
        //поиск enemy в бд
        var enemy = enemyRepository.findById(enemyId);
        if(enemy.isEmpty()) {
            var response = MainBuilder.builder()
                    .player(player.get())
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден enemy в БД по запросу enemyId: {}", enemyId);
            return jsonProcessor.toJsonMainResponse(response);
        }

        //создание нового сражения и добавление в map
        long newFightId = randomUtil.getRandomId();
        RoundHandler.FIGHT_MAP.put(newFightId,
                new Fight(List.of(player.get().getId()), List.of(enemy.get().getId()),
                        1, 60, false));

        //сохранение статуса FIGHT и id сражения у player
        player.get().setStatus(Status.FIGHT);
        player.get().setFightId(newFightId);
        playerRepository.save(player.get());

        //сохранение статуса FIGHT и id сражения у enemy
        enemy.get().setStatus(Status.FIGHT);
        enemy.get().setFightId(newFightId);
        enemyRepository.save(enemy.get());

        var response = FightBuilder.builder()
                .player(player.get())
                .enemy(enemy.get())
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJsonFightResponse(response);
    }

    //физическая атака по выбранному enemy
    public String getPhysAttackUserVsEnemy(String username, Long abilityId, Long targetId) {
        //поиск player в бд
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = MainBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJsonMainResponse(response);
        }

        //расчет и сохранение умения и цели, по которой произведено действие
        player.get().setRoundDamage(1);
        player.get().setAttackToId(targetId);
        playerRepository.save(player.get());

        var response = FightBuilder.builder()
                .player(player.get())
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJsonFightResponse(response);
    }
}
