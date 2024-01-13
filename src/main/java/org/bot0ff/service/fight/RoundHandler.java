package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.AttackType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.TeamType;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.RandomUtil;
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
    private final RandomUtil randomUtil;

    public static Map<Long, Integer> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //заглушка на очистку статуса боя user, если в FIGHT_MAP нет сражений
    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearFightDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {
            unitRepository.deleteFight(
                    unit.getHp(),
                    false,
                    Status.ACTIVE.name(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    unit.getId());
        }
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void mapRoundHandler() {
        if(!FIGHT_MAP.isEmpty()) {
            Iterator<Map.Entry<Long, Integer>> entries = FIGHT_MAP.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Long, Integer> entry = entries.next();
                if(entry.getValue() == 0) {
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

        //1. Разделить по командам
        //1. расположить в порядке максимальной инициативы вне зависимости от массовости атаки
        //2. если после нанесения урона hp 0 или меньше, выпадает из сражения
        //3. если инициатива одинаковая, выбор случайно
        //4. после каждого расчета обновляем лист с состояниями

        //добавляем unit со статусом НЕ DIE
        List<Unit> units = new ArrayList<>(fight.get().getUnits());
        List<Unit> teamOne = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.get_teamType().equals(TeamType.ONE)).toList());
        List<Unit> teamTwo = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.get_teamType().equals(TeamType.TWO)).toList());

        //Сортируем unit в порядке убывания инициативы
        units.sort(Comparator.comparingLong(Unit::getId));

        int count = 0;
        while (count < teamOne.size() + teamTwo.size()) {
            Optional<Unit> optionalUnit = units.stream().filter(Unit::isActionEnd).findFirst();
            //если unit, которые ходили не найдены, выходим из цикла
            if(optionalUnit.isEmpty()) break;
            //если unit со статусом DIE, запускаем новую итерацию цикла
            if(optionalUnit.get().getStatus().name().equals(Status.DIE.name())) continue;

            //проверяем на кого направлено умение и применяем его
            String attackType = optionalUnit.get().get_attackType().name();
            switch (attackType) {
                case "MYSELF" -> {
                    System.out.println("Применено умение на себя");
                }
                case "OPPONENT" -> {
                    Optional<Unit> optionalTarget = units.stream().filter(u -> u.getId().equals(optionalUnit.get().get_targetId())).findFirst();
                    //если текущий unit не найден или hp <= 0, начинаем новую итерацию цикла
                    if(optionalTarget.isEmpty()) continue;
                    if(optionalTarget.get().getHp() <= 0) continue;
                    //рассчитываем урон, который нанес текущий unit противнику
                    Unit unit = optionalUnit.get();
                    Unit target = optionalTarget.get();
                    int unitHit = Math.toIntExact(unit.get_damage());
                    //TODO добавить защиту
                    int targetDefense = 0;
                    double unitHitDouble = unitHit * 1.0;
                    double targetDefenseDouble = targetDefense * 1.0;
                    if(unitHit < 0) unitHit = 0;
                    if(targetDefense < 0) targetDefense = 0;
                    int result = (int) ((int) (((unitHit - Math.random() * 10) * ((double) unitHit / (unitHit + targetDefense)))) * (unitHitDouble / targetDefenseDouble));
                    if(result <= 0) result = 0;
                    if(result > unit.get_damage() * 2) result = randomUtil.getRNum30((int) (unit.get_damage() * 2));
                    System.out.println("unit нанес урон " + result);
                    target.setHp(target.getHp() - result);
                    if(target.getHp() <= 0) {
                        target.setStatus(Status.DIE);
                    }
                    unit.setActionEnd(false);
                }
                case "ALL_OPPONENT" -> {
                    System.out.println("Применено умение на всех противников");
                }
                case "ALL_ALLIES" -> {
                    System.out.println("Применено умение на всех союзников");
                }
                default -> {
                    System.out.println("Умение не выбрано");
                }
            }
            count++;
        }

        //запускаем следующий раунд и сохраняем результаты предыдущего в бд
        //если unit DIE, удаляем его настройки сражения
        for(Unit unit: units) {
            if(unit.getStatus().equals(Status.DIE)) {
                unitRepository.deleteFight(
                        1,
                        false,
                        Status.ACTIVE.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        unit.getId()
                );
            }
            else {
                unitRepository.clearRound(
                        unit.getHp(),
                        false,
                        0L,
                        AttackType.NONE.name(),
                        0L,
                        unit.getId()
                );
            }
        }
        FIGHT_MAP.put(fightId, 10);
    }
}
