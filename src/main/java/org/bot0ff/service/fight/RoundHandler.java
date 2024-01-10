package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Player;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.EnemyRepository;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.PlayerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundHandler {
    private final PlayerRepository playerRepository;
    private final EnemyRepository enemyRepository;
    private final FightRepository fightRepository;

    public static Map<Long, Integer> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //заглушка на очистку статуса боя user, если в FIGHT_MAP нет сражений
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void clearFightDB() {
        Player player = playerRepository.findByName("user").orElse(null);
        if(RoundHandler.FIGHT_MAP.isEmpty()) {
            Fight fight = fightRepository.findById(player.getFight().getId()).orElse(null);
            List<Player> players = fight.getPlayers();
            for(Player p: players) {
                playerRepository.clearPlayerFight(Status.ACTIVE.name(), null,
                        false, null, null, null, p.getId());
            }
            List<Enemy> enemies = fight.getEnemies();
            for(Enemy e: enemies) {
                enemyRepository.clearEnemyFight(Status.ACTIVE.name(), null,
                        false, null, null, null, e.getId());
            }
            fightRepository.setStatusFight(false, player.getFight().getId());
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void mapRoundHandler() {
        if(!FIGHT_MAP.isEmpty()) {
            Iterator<Map.Entry<Long, Integer>> entries = FIGHT_MAP.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Long, Integer> entry = entries.next();
                if(entry.getValue() == 0) {
                    FIGHT_MAP.put(entry.getKey(), 60);
                    //resultRoundHandler(entry.getKey());
                }
                else {
                    FIGHT_MAP.put(entry.getKey(), FIGHT_MAP.get(entry.getKey()) - 1);
                }
            }
        }
    }

//    @Async
//    private void resultRoundHandler(Long fightId) {
//        Optional<Fight> fight = fightRepository.findById(fightId);
//        //если сражение не найдено в бд, сохраняем последнее значение из map
//        //TODO сделать обработку при ошибке
//        if(fight.isEmpty()) {
//            return;
//        }
//        List<Player> players = new ArrayList<>();
//        List<Enemy> enemies = new ArrayList<>();
//        for(String playerN: fight.get().getPlayers()) {
//            String playerName = StringUtils.substringBetween(playerN, "", ":");
//            Optional<Player> player = playerRepository.findByName(playerName);
//            player.ifPresent(players::add);
//        }
//        for(String enemyN: fight.get().getEnemies()) {
//            String enemyName = StringUtils.substringBetween(enemyN, "", ":");
//            Optional<Enemy> enemy = enemyRepository.findByName(enemyName);
//            enemy.ifPresent(enemies::add);
//        }
//        for(Player player: players) {
//            if(player.getAttackToId() != 0 && !player.getStatus().equals(Status.DIE)) {
//                Optional<Player> targetPlayer = players.stream().filter(p -> p.getId().equals(player.getAttackToId())).findFirst();
//                if(targetPlayer.isPresent()) {
//                    targetPlayer.get().setHp(targetPlayer.get().getHp() - player.getDamage());
//                    if (targetPlayer.get().getHp() <= 0) {
//                        targetPlayer.get().setRoundDamage(0);
//                        targetPlayer.get().setAttackToId(0L);
//                        targetPlayer.get().setEndRound(false);
//                        targetPlayer.get().setStatus(Status.DIE);
//                    }
//                }
//                Optional<Enemy> targetEnemy = enemies.stream().filter(e -> e.getId().equals(player.getAttackToId())).findFirst();
//                if(targetEnemy.isPresent()) {
//                    targetEnemy.get().setHp(targetEnemy.get().getHp() - player.getDamage());
//                    if (targetEnemy.get().getHp() <= 0) {
//                        targetEnemy.get().setStatus(Status.DIE);
//                    }
//                }
//            }
//        }
//        players.forEach(playerRepository::save);
//        enemies.forEach(enemyRepository::save);
//
//        fight.get().setCountRound(fight.get().getCountRound() + 1);
//        fight.get().getPlayers().clear();
//        fight.get().getEnemies().clear();
//        players.forEach(player -> fight.get().getPlayers().add(player.getName() + ":" + player.getHp()));
//        enemies.forEach(enemy -> fight.get().getEnemies().add(enemy.getName() + ":" + enemy.getHp()));
//        fightRepository.save(fight.get());
//    }
}
