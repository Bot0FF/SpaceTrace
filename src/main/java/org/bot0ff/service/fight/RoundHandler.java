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

        //добавляем unit в общий лист и по командам
        List<Unit> units = new ArrayList<>(fight.get().getUnits());

        //Сортируем unit в порядке убывания инициативы
        units.sort(Comparator.comparingLong(Unit::getId));
        System.out.println("Сортировка по убыванию инициативы " + units.get(0).getName() + "->" + units.get(1).getName());

        int count = 0;
        while (count < units.size()) {
            Optional<Unit> optionalUnit = units.stream().filter(Unit::isActionEnd).findFirst();
            //если unit, которые ходили не найдены, выходим из цикла
            if(optionalUnit.isEmpty()) break;
            System.out.println("Ходит юнит " + optionalUnit.get().getName());
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

                    //рассчитываем урон, который нанес текущий unit противнику
                    Unit unit = optionalUnit.get();
                    Unit target = optionalTarget.get();
                    System.out.println("Юнит " + unit.getName() + " атакует юнита " + target.getName());
                    int unitHit = Math.toIntExact(unit.get_damage());
                    //TODO добавить защиту
                    int targetDefense = 1;
                    double unitHitDouble = unitHit * 1.0;
                    double targetDefenseDouble = targetDefense * 1.0;
                    if(unitHit <= 1) unitHit = 1;
                    if(targetDefense <= 1) targetDefense = 1;
                    int result = (int) ((int) (((unitHit - Math.random() * 10) * ((double) unitHit / (unitHit + targetDefense)))) * (unitHitDouble / targetDefenseDouble));
                    if(result <= 0) result = 0;
                    if(result > unit.get_damage()) result = randomUtil.getRNum30(Math.toIntExact(unit.get_damage()));
                    System.out.println("unit " + unit.getName() + " нанес урон " + result);
                    target.setHp(target.getHp() - result);
                    if(target.getHp() <= 0) {
                        target.setActionEnd(false);
                        target.setStatus(Status.DIE);
                        System.out.println(target.getName() + " ход отменен. HP <= 0");
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
            System.out.println("Переход хода к следующему юниту в раунде");
        }

        //сохраняем состояние всех unit,
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
                System.out.println(unit.getName() + " удален из сражения");
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
                System.out.println("Настройки " + unit.getName() + " сброшены для следующего раунда");
            }
        }

        //если в обеих командах есть кто-то со статусом ACTIVE, сражение продолжается
        if(units.stream().anyMatch(unit -> unit.get_teamType().equals(TeamType.ONE)
                && unit.getStatus().equals(Status.FIGHT))
                & units.stream().anyMatch(unit -> unit.get_teamType().equals(TeamType.TWO)
                && unit.getStatus().equals(Status.FIGHT))) {
            //запускаем следующий раунд
            FIGHT_MAP.put(fightId, 10);
            fightRepository.setNewRound(fight.get().getCountRound() + 1, fightId);
            System.out.println("-->Запуск следующего раунда");
        }
        //если в первой команде нет игроков со статусом ACTIVE, завершаем бой,
        //сохраняем результаты победы у unit из второй команды
        else if(units.stream().noneMatch(unit -> unit.get_teamType().equals(TeamType.ONE)
                && unit.getStatus().equals(Status.FIGHT))) {
            List<Unit> teamTwo = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.get_teamType().equals(TeamType.TWO)
                    && unit.getStatus().equals(Status.FIGHT)).toList());
            for(Unit unit: teamTwo) {
                unitRepository.saveVictoryFight(
                        unit.getHp(),
                        false,
                        Status.ACTIVE.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        unit.getId()
                );
                System.out.println(unit.getName() + " победил в сражении");
            }
            FIGHT_MAP.remove(fightId);
            fightRepository.setStatusFight(true, fightId);
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
        //если во второй команде нет игроков со статусом ACTIVE, завершаем бой,
        //сохраняем результаты победы у unit из первой команды
        else {
            List<Unit> teamOne = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.get_teamType().equals(TeamType.ONE)
                    && unit.getStatus().equals(Status.FIGHT)).toList());
            for(Unit unit: teamOne) {
                unitRepository.saveVictoryFight(
                        unit.getHp(),
                        false,
                        Status.ACTIVE.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        unit.getId()
                );
                System.out.println(unit.getName() + " победил в сражении");
            }
            FIGHT_MAP.remove(fightId);
            fightRepository.setStatusFight(true, fightId);
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
    }
}
