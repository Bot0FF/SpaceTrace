package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundHandler {
    private final UnitRepository unitRepository;
    private final FightRepository fightRepository;

    public static Map<Long, Integer> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //заглушка на очистку статуса боя user, если в FIGHT_MAP нет сражений
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void clearFightDB() {
        Unit player = unitRepository.findByName("user").orElse(null);
        if(RoundHandler.FIGHT_MAP.isEmpty() && player.getFight() != null) {
            Fight fight = fightRepository.findById(player.getFight().getId()).orElse(null);
            List<Unit> oneTeam = fight.getUnits();
            for(Unit unit: oneTeam) {
                unitRepository.deleteFight(
                        false,
                        Status.ACTIVE.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        unit.getId());
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
                    resultRoundHandler(entry.getKey());
                }
                else {
                    FIGHT_MAP.put(entry.getKey(), FIGHT_MAP.get(entry.getKey()) - 1);
                }
            }
        }
    }

    @Async
    private void resultRoundHandler(Long fightId) {
        Optional<Fight> fight = fightRepository.findById(fightId);

        //если сражение не найдено в бд, удаляем fight из map
        if(fight.isEmpty()) {
            FIGHT_MAP.remove(fightId);
            return;
        }

    }
}
