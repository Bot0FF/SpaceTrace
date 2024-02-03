package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Subject;
import org.bot0ff.entity.Unit;
import org.bot0ff.dto.unit.UnitEffect;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.SubjectRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.RandomUtil;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@Slf4j
public class FightHandler {
    private UnitRepository unitRepository;
    private FightRepository fightRepository;
    private SubjectRepository subjectRepository;
    private RandomUtil randomUtil;

    private Long fightId;
    private Instant endRoundTimer;
    private boolean endAiAttack;
    private boolean endFight;

    public FightHandler(Long fightId,
                        UnitRepository unitRepository,
                        FightRepository fightRepository,
                        SubjectRepository subjectRepository,
                        RandomUtil randomUtil) {
        this.unitRepository = unitRepository;
        this.fightRepository = fightRepository;
        this.subjectRepository = subjectRepository;
        this.randomUtil = randomUtil;

        this.fightId = fightId;
        this.endAiAttack = false;
        endRoundTimer = Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME);
        endFight = false;

        try {
            System.out.println("Запуск нового сражения");
            fightInitializer();
        } catch (InterruptedException e) {
            log.info("Ошибка выполнения обработчика раундов: " + e.getMessage());
            FightService.FIGHT_MAP.remove(fightId);
        }
    }

    private void fightInitializer() throws InterruptedException {
        Executors.newSingleThreadExecutor().execute(() -> {
            do {
                if (Instant.now().isAfter(endRoundTimer)) {
                    resultRoundHandler(fightId);
                    this.endRoundTimer = Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME);
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        if (!endAiAttack) {
                            setAiAttack();
                        }
                    } catch (InterruptedException e) {
                        endFight = true;
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
        Optional<Fight> optionalFight = fightRepository.findById(fightId);

        //если сражение не найдено в бд, удаляем fight из map
        if (optionalFight.isEmpty()) {
            endFight = true;
            return;
        }

        //начинаем работу с листом units
        List<Unit> units = optionalFight.get().getUnits();

        //Сортируем unit в порядке убывания инициативы
        units.sort(Comparator.comparingLong(Unit::getId));
        List<String> unitName = units.stream().map(Unit::getName).toList();
        System.out.println("Сортировка по убыванию инициативы " + unitName);

        for (Unit unit : units) {
            if (unit.isActionEnd() & unit.getStatus().equals(Status.FIGHT)) {
                //находим примененное умение из бд
                Optional<Subject> ability = subjectRepository.findById(unit.getAbilityId());
                if (ability.isEmpty()) {
                    System.out.println("Не найдено умение " + unit.getAbilityId() + " при выборе игроком " + unit.getName() + "\nХод следующего unit");
                    continue;
                }

                //определяем тип атаки и область применения
                switch (ability.get().getApplyType()) {
                    //одиночные умения
                    case SINGLE -> {
                        //если цель-unit не найден, переходим к следующей итерации цикла
                        Optional<Unit> target = units.stream().filter(t -> t.getId().equals(unit.getTargetId())).findFirst();
                        if (target.isEmpty()) continue;
                        System.out.println("Unit " + unit.getName() + " применил умение " + ability.get().getName() + " на юнита " + target.get().getName());
                        switch (ability.get().getHitType()) {
                            case DAMAGE -> {
                                StringBuilder result = calculateDamage(unit, target.get(), ability.get());
                                resultRound.append(result);
                                System.out.println("Применено одиночное умение атаки");
                            }
                            case RECOVERY -> {
                                StringBuilder result = calculateRecovery(unit, target.get(), ability.get());
                                resultRound.append(result);
                                System.out.println("Применено одиночное умение восстановления");
                            }
                            case BOOST -> {
                                StringBuilder result = calculateBoost(unit, target.get(), ability.get());
                                resultRound.append(result);
                                System.out.println("Применено одиночное повышающее умение");
                            }
                            case LOWER -> {
                                System.out.println("Применено одиночное понижающее умение");
                            }
                        }
                    }
                    //массовые умения
                    case TOTAL -> {
                        switch (ability.get().getHitType()) {
                            case DAMAGE -> {
                                System.out.println("Применено массовое умение урона");
                            }
                            case RECOVERY -> {
                                System.out.println("Применено массовое умение восстановления");
                            }
                        }
                    }
                    default -> System.out.println("Умение не выбрано");
                }
                System.out.println("Переход хода к следующему юниту в раунде");
            }
        }

        //проверяем длительность эффектов
        units.forEach(this::setUnit);

        //проверяем состояние характеристик после проверки действия активных умений
        units.forEach(unit -> {
            if (unit.getHp() <= 0) {
                unit.setStatus(Status.DIE);
            }
            if (unit.getMana() <= 0) {
                unit.setMana(0);
            }
            if (unit.getDamage() <= 0) {
                unit.setDamage(0);
            }
            if (unit.getDefense() <= 0) {
                unit.setDefense(0);
            }
        });

        //сохраняем состояние всех unit,
        //если unit USER и DIE, удаляем его из сражения со сбросом статуса сражения, если AI удаляем из бд
        for (Unit unit : units) {
            if (unit.getStatus().equals(Status.DIE)) {
                unit.setDamage(unit.getUnitEffect().getStartDamage());
                unit.setDefense(unit.getUnitEffect().getStartDefense());
                unit.setStatus(Status.LOSS);
                unit.setFight(null);
                unit.setUnitEffect(null);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setTargetId(null);
                unit.setActionEnd(false);
                unitRepository.save(unit);
            }
            //сбрасываем настройки unit
            else {
                unit.setActionEnd(false);
                unit.setAbilityId(0L);
                unit.setTargetId(0L);
                unitRepository.save(unit);
                System.out.println("Настройки " + unit.getName() + " сброшены для следующего раунда");
            }
        }

        //удаляем из списка unit со статусом LOSS
        units.removeIf(unit -> unit.getStatus().equals(Status.LOSS));

        //если список units пуст, завершаем сражение
        if (units.isEmpty()) {
            endFight = true;
            optionalFight.get().setFightEnd(true);
            fightRepository.save(optionalFight.get());
            System.out.println(fightId + " сражение завершено и удалено из map. Ничья");
            return;
        }

        //делим на команды всех unit
        List<Unit> teamOne = new ArrayList<>(units.stream().filter(unit -> unit.getTeamNumber() == 1).toList());
        List<Unit> teamTwo = new ArrayList<>(units.stream().filter(unit -> unit.getTeamNumber() == 2).toList());

        //если в обеих командах кто-то есть, сражение продолжается
        Fight fight = optionalFight.get();
        if (!teamOne.isEmpty() & !teamTwo.isEmpty()) {
            //запускаем следующий раунд
            fight.setCountRound(fight.getCountRound() + 1);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            System.out.println("-->Запуск следующего раунда");
        }
        //если во второй команде нет игроков, а в первой есть, завершаем бой,
        //сохраняем результаты победы у unit из первой команды
        else if (!teamOne.isEmpty() & teamTwo.isEmpty()) {
            for (Unit unit : teamOne) {
                System.out.println(unit.getName() + " победил в сражении");
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setStatus(Status.WIN);
                unit.setFight(null);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setTargetId(null);
                unit.setUnitEffect(null);
                unitRepository.save(unit);
            }
            endFight = true;
            fight.setFightEnd(true);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
        //если в первой команде нет игроков, а во второй есть, завершаем бой,
        //сохраняем результаты победы у unit из второй команды
        else {
            for (Unit unit : teamTwo) {
                System.out.println(unit.getName() + " победил в сражении");
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setStatus(Status.WIN);
                unit.setFight(null);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setTargetId(null);
                unit.setUnitEffect(null);
                unitRepository.save(unit);
            }
            endFight = true;
            fight.setFightEnd(true);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
    }

    //рассчитываем нанесенный урон при типе атаки OPPONENT->DAMAGE
    private StringBuilder calculateDamage(Unit unit, Unit target, Subject ability) {
        UnitEffect unitEffect = unit.getUnitEffect();
        UnitEffect targetJson = target.getUnitEffect();
        //рассчитываем урон, который нанес текущий unit противнику
        double unitHit = (unit.getDamage() + unitEffect.getEffectDamage()) * 1.0;
        double targetDefense = (target.getDefense() + targetJson.getEffectDefense()) * 1.0;
        if (unitHit <= 1) unitHit = 1;
        if (targetDefense <= 1) targetDefense = 1;
        double damageMultiplier = unitHit / (unitHit + targetDefense);
        int totalDamage = (int) Math.round(unitHit * damageMultiplier);
        int result = randomUtil.getRNum30(totalDamage);
        if (result <= 0) result = 0;
        System.out.println("unit " + unit.getName() + " нанес урон " + result);
        target.setHp(target.getHp() - result);

        //устанавливаем target параметры по результатам расчета
        if (target.getHp() <= 0) {
            target.setActionEnd(true);
            target.setStatus(Status.DIE);
            System.out.println(target.getName() + " ход отменен. HP <= 0");
        }
        unit.setActionEnd(true);

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(" нанес ")
                .append(result)
                .append(" урона противнику ")
                .append(target.getName())
                .append(" умением ")
                .append(ability.getName())
                .append("]");
    }

    //расчет восстановления характеристик
    public StringBuilder calculateRecovery(Unit unit, Unit target, Subject ability) {
        int result = 0;
        String action = "";
        String characteristic = "";
        String duration = "";
        if (ability.getDuration() == 0) {
            action = " восстановил ";
            if (ability.getHp() != 0) {
                characteristic = " здоровья ";
                result = ability.getHp();
                target.setHp(target.getHp() + ability.getHp());
                if (target.getHp() > target.getMaxHp()) {
                    result = Math.abs(target.getMaxHp() - target.getHp());
                    target.setHp(target.getMaxHp());
                }
            }
            if (ability.getMana() != 0) {
                characteristic = " маны ";
                result = ability.getMana();
                target.setMana(target.getMana() + ability.getMana());
                if (target.getMana() > target.getMaxMana()) {
                    result = Math.abs(target.getMaxMana() - target.getMana());
                    target.setMana(target.getMaxMana());
                }
            }
        }

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(action)
                .append(result)
                .append(characteristic)
                .append(" игроку ")
                .append(target.getName())
                .append(" умением ")
                .append(ability.getName())
                .append(duration)
                .append("]");
    }

    //расчет повышения/понижения характеристик
    public StringBuilder calculateBoost(Unit unit, Unit target, Subject ability) {
        int result = 0;
        String action = "";
        String characteristic = "";
        String duration = "";

        //TODO добавить длительность умений
        if(ability.getDuration() != 0) {
            action = "";
            duration = " на " + ability.getDuration() + " раунда";
            if(ability.getHp() != 0) {
                if(ability.getHp() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " максимальное здоровье ";
                result = ability.getHp();
                target.setMaxHp(target.getMaxHp() + ability.getHp());
                target.getUnitEffect().setEffectHp(ability.getHp());
                target.getUnitEffect().setDurationEffectHp(ability.getDuration());
            }
            if(ability.getMana() != 0) {
                if(ability.getMana() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " максимальную ману ";
                result = ability.getMana();
                target.setMaxMana(target.getMaxMana() + ability.getMana());
                target.getUnitEffect().setEffectMana(ability.getMana());
                target.getUnitEffect().setDurationEffectMana(ability.getDuration());
            }
            if(ability.getDamage() != 0) {
                if(ability.getDamage() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " урон ";
                result = ability.getDamage();
                target.setDamage(target.getDamage() + ability.getDamage());
                target.getUnitEffect().setEffectDamage(ability.getDamage());
                target.getUnitEffect().setDurationEffectDamage(ability.getDuration());
            }
            if(ability.getDefense() != 0) {
                if(ability.getDefense() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " защиту ";
                result = ability.getDefense();
                target.setDefense(target.getDefense() + ability.getDefense());
                target.getUnitEffect().setEffectDefense(ability.getDefense());
                target.getUnitEffect().setDurationEffectDefense(ability.getDuration());
            }
        }

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(action)
                .append(characteristic)
                .append(" на ")
                .append(result)
                .append(" единиц(ы) игроку ")
                .append(target.getName())
                .append(" умением ")
                .append(ability.getName())
                .append(duration)
                .append("]");
    }

    //установка урона AI
    @Transactional
    private void setAiAttack() {
        Optional<Fight> fight = fightRepository.findById(fightId);
        if(fight.isEmpty()) return;

        //делим на команды всех unit
        List<Unit> teamOne = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.getTeamNumber() == 1).toList());
        List<Unit> teamTwo = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.getTeamNumber() == 2).toList());

        //TODO сделать случайный выбор атаки
        //все unit первой команды применяют атаку на любом unit из второй команды
        for(Unit aiUnit : teamOne){
            if(aiUnit.getUnitType().equals(UnitType.AI)) {
                int target = randomUtil.getRandomFromTo(0, teamTwo.size() - 1);
                Long targetId = teamTwo.get(target).getId();
                aiUnit.setAbilityId(1L);
                aiUnit.setTargetId(targetId);
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }

        //все unit второй команды применяют атаку на любом unit из первой команды
        for(Unit aiUnit : teamTwo){
            if(aiUnit.getUnitType().equals(UnitType.AI)) {
                int target = randomUtil.getRandomFromTo(0, teamOne.size() - 1);
                Long targetId = teamOne.get(target).getId();
                aiUnit.setAbilityId(1L);
                aiUnit.setTargetId(targetId);
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }
        endAiAttack = false;
    }

    //обновление UnitEffect для следующего сражения
    private void setUnit(Unit unit) {
        UnitEffect unitEffect = unit.getUnitEffect();
        //расчет действующих эффектов hp
        if(unitEffect.getDurationEffectHp() > 0) {
            unitEffect.setDurationEffectHp(unitEffect.getDurationEffectHp() - 1);
        }
        else {
            unit.setMaxHp(unit.getMaxHp() - unitEffect.getEffectHp());
            unitEffect.setEffectHp(0);
        }

        //расчет действующих эффектов mana
        if(unitEffect.getDurationEffectMana() > 0) {
            unitEffect.setDurationEffectMana(unitEffect.getDurationEffectMana() - 1);
        }
        else {
            unit.setMaxMana(unit.getMaxMana() - unitEffect.getEffectMana());
            unitEffect.setEffectMana(0);
        }

        //расчет действующих эффектов damage
        if(unitEffect.getDurationEffectDamage() > 0) {
            unitEffect.setDurationEffectDamage(unitEffect.getDurationEffectDamage() - 1);
        }
        else {
            unit.setDamage(unit.getDamage() - unitEffect.getEffectDamage());
            unitEffect.setEffectDamage(0);
        }

        //расчет действующих эффектов defense
        if(unitEffect.getDurationEffectDefense() > 0) {
            unitEffect.setDurationEffectDefense(unitEffect.getDurationEffectDefense() - 1);
        }
        else {
            unit.setDefense(unit.getDefense() - unitEffect.getEffectDefense());
            unitEffect.setEffectDefense(0);
        }
    }
}
