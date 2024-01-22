package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.AttackType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.RandomUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class FightHandler {
    private Long fightId;
    private UnitRepository unitRepository;
    private FightRepository fightRepository;
    private RandomUtil randomUtil;
    private Integer roundTimer;
    private boolean endFight;
    private ExecutorService EXECUTOR_SERVICE;

    public FightHandler(Long fightId, UnitRepository unitRepository, FightRepository fightRepository, RandomUtil randomUtil) {
        this.fightId = fightId;
        this.unitRepository = unitRepository;
        this.fightRepository = fightRepository;
        this.randomUtil = randomUtil;
        roundTimer = Constants.ROUND_LENGTH_TIME;
        endFight = false;
        EXECUTOR_SERVICE = Executors.newCachedThreadPool();

        try {
            System.out.println("Запуск нового сражения");
            fightInitializer();
        } catch (InterruptedException e) {
            log.info("Ошибка выполнения обработчика раундов: " + e.getMessage());
            FightService.FIGHT_MAP.remove(fightId);
        }
    }

    private void fightInitializer() throws InterruptedException {
        EXECUTOR_SERVICE.execute(() -> {
            do {
                if (roundTimer <= 0) {
                    resultRoundHandler(fightId);
                    this.roundTimer = Constants.ROUND_LENGTH_TIME;
                } else {
                    System.out.println(roundTimer);
                    this.roundTimer = roundTimer - 1;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (!endFight);
            FightService.FIGHT_MAP.remove(fightId);
        });
    }

    //TODO сделать отображение результатов сражения для победивших unit
    @Transactional
    private void resultRoundHandler(Long fightId) {
        StringBuilder resultRound = new StringBuilder();
        Optional<Fight> fight = fightRepository.findById(fightId);

        //если сражение не найдено в бд, удаляем fight из map
        if(fight.isEmpty()) {
            endFight = true;
            return;
        }

        //добавляем unit в общий лист
        List<Unit> units = new ArrayList<>(fight.get().getUnits());

        //Сортируем unit в порядке убывания инициативы
        units.sort(Comparator.comparingLong(Unit::getId));
        List<String> unitName = units.stream().map(Unit::getName).toList();
        System.out.println("Сортировка по убыванию инициативы " + unitName);

        for(Unit unit: units) {
            if(unit.isActionEnd() & unit.getStatus().equals(Status.FIGHT)) {
                System.out.println("Обработка примененного умения " + unit.getName());
                String attackType = unit.get_attackType().name();
                switch (attackType) {
                    case "MYSELF" -> {
                        System.out.println("Применено умение на себя");
                    }
                    case "OPPONENT" -> {
                        Optional<Unit> optionalTarget = units.stream().filter(u -> u.getId().equals(unit.get_targetId())).findFirst();
                        //если цель-unit не найден, переходим к следующей итерации цикла
                        if(optionalTarget.isEmpty()) continue;
                        //рассчитываем урон, который нанес текущий unit противнику
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
                            target.setActionEnd(true);
                            target.setStatus(Status.DIE);
                            System.out.println(target.getName() + " ход отменен. HP <= 0");
                        }
                        unit.setActionEnd(true);
                        resultRound
                                .append(unit.getName())
                                .append(" нанес ")
                                .append(result)
                                .append(" урона противнику ")
                                .append(target.getName())
                                .append(" умением обычная атака /");
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
                System.out.println("Переход хода к следующему юниту в раунде");
            }
        }

        //сохраняем состояние всех unit,
        //если unit DIE, удаляем его из сражения со сбросом статуса сражения
        for(Unit unit: units) {
            if(unit.getStatus().equals(Status.DIE)) {
                unit.setHp(1);
                unit.setActionEnd(false);
                unit.setStatus(Status.ACTIVE);
                unit.setFight(null);
                unit.set_teamType(null);
                unit.set_damage(null);
                unit.set_attackType(null);
                unit.set_targetId(null);
                unitRepository.save(unit);
                System.out.println(unit.getName() + " удален из сражения");
            }
            else {
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.set_damage(0L);
                unit.set_attackType(AttackType.NONE);
                unit.set_targetId(0L);
                unitRepository.save(unit);
                System.out.println("Настройки " + unit.getName() + " сброшены для следующего раунда");
            }
        }

        //удаляем из списка unit со статусом DIE
        units.removeIf(unit -> unit.getStatus().equals(Status.ACTIVE));

        //если список units пуст, завершаем сражение
        if(units.isEmpty()) {
            endFight = true;
            System.out.println(fightId + " сражение завершено и удалено из map. Ничья");
            return;
        }

        //делим на команды всех unit
        List<Unit> teamOne = new ArrayList<>();
        List<Unit> teamTwo = new ArrayList<>();
        for(Unit unit: units) {
            if(unit.get_teamType() == 1) {
                teamOne.add(unit);
            }
            else {
                teamTwo.add(unit);
            }
        }

        //если в обеих командах есть кто-то есть, сражение продолжается
        if(!teamOne.isEmpty() & !teamTwo.isEmpty()) {
            //запускаем следующий раунд
            fight.get().setCountRound(fight.get().getCountRound() + 1);
            fight.get().setResultRound(String.valueOf(resultRound));
            fightRepository.save(fight.get());
            System.out.println("-->Запуск следующего раунда");
        }
        //если в первой команде нет игроков со статусом FIGHT, а во второй есть, завершаем бой,
        //сохраняем результаты победы у unit из второй команды
        else if(!teamOne.isEmpty() & teamTwo.isEmpty()) {
            for(Unit unit: teamOne) {
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setStatus(Status.ACTIVE);
                unit.set_teamType(null);
                unit.set_damage(null);
                unit.set_attackType(null);
                unit.set_targetId(null);
                unitRepository.save(unit);
                System.out.println(unit.getName() + " победил в сражении");
            }
            endFight = true;
            fight.get().setFightEnd(true);
            fight.get().setResultRound(String.valueOf(resultRound));
            fightRepository.save(fight.get());
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
        //если во второй команде нет игроков со статусом FIGHT, а в первой есть, завершаем бой,
        //сохраняем результаты победы у unit из первой команды
        else {
            for(Unit unit: teamTwo) {
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setStatus(Status.ACTIVE);
                unit.set_teamType(null);
                unit.set_damage(null);
                unit.set_attackType(null);
                unit.set_targetId(null);
                unitRepository.save(unit);
                System.out.println(unit.getName() + " победил в сражении");
            }
            endFight = true;
            fight.get().setFightEnd(true);
            fight.get().setResultRound(String.valueOf(resultRound));
            fightRepository.save(fight.get());
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
    }
}
