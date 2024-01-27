package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Subject;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.UnitJson;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.SubjectRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.util.RandomUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
                        if(!endAiAttack) {
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
        if(optionalFight.isEmpty()) {
            endFight = true;
            return;
        }

        Fight fight = optionalFight.get();
        //добавляем unit в общий лист
        List<Unit> units = new ArrayList<>(fight.getUnits());
        List<UnitJson> unitJsons = new ArrayList<>(fight.getUnitJson());
        if(unitJsons.isEmpty()) {
            endFight = true;
            System.out.println("Не найдены unitJson в сражении: " + fightId + ", сражение завершено с ошибкой");
            return;
        }

        //Сортируем unit в порядке убывания инициативы
        units.sort(Comparator.comparingLong(Unit::getId));
        List<String> unitName = units.stream().map(Unit::getName).toList();
        System.out.println("Сортировка по убыванию инициативы " + unitName);

        for(Unit unit: units) {
            if(unit.isActionEnd() & unit.getStatus().equals(Status.FIGHT)) {
                Optional<Subject> ability = subjectRepository.findById(unit.get_abilityId());
                if(ability.isEmpty()) {
                    System.out.println("Не найдено умение " + unit.get_abilityId() + " при выборе игроком" + unit.getName() + " . Ход следующего unit");
                    continue;
                }
                System.out.println(unit.getName() + "применил умение " + ability.get().getName());
                String applyType = String.valueOf(ability.get().getApplyType());
                switch (applyType) {
                    case "MYSELF" -> {
                        System.out.println("Применено умение на себя");
                    }
                    case "OPPONENT" -> {
                        Optional<UnitJson> unitJson = unitJsons.stream().filter(u -> u.getId().equals(unit.getId())).findFirst();
                        Optional<Unit> target = units.stream().filter(u -> u.getId().equals(unit.get_targetId())).findFirst();
                        Optional<UnitJson> targetJson = unitJsons.stream().filter(u -> u.getId().equals(unit.get_targetId())).findFirst();
                        //если unit или цель-unit не найден, переходим к следующей итерации цикла
                        if(unitJson.isEmpty() | targetJson.isEmpty() | target.isEmpty()) continue;
                        UnitJson uj = unitJson.get();
                        Unit t = target.get();
                        UnitJson tj = unitJson.get();
                        System.out.println("Юнит " + unit.getName() + " применил умение на юнита " + target.getId());
                        String hitType = String.valueOf(ability.get().getHitType());
                        switch (hitType) {
                            case "DAMAGE" -> {
                                //рассчитываем урон и сохраняем результат
                                StringBuilder result = calculateDamage(unit, unitJson, target, targetJson);
                                resultRound.append(result);
                            }
                            case "RECOVERY" -> {
                                System.out.println("Применено умение восстановления на unit");
                            }
                        }
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

        //меняем значения параметры unit на параметры из unitJson
        for(Unit unit : units) {
            Optional<UnitJson> unitJson = unitJsons.stream().filter(uj -> unit.getId().equals(uj.getId())).findFirst();
            if(unitJson.isPresent()) {
                unit.setHp(unitJson.get().getCurrentHp());
                unit.setMana(unitJson.get().getCurrentMana());
                unit.setDamage(unitJson.get().getCurrentDamage());
                unit.setDefense(unitJson.get().getCurrentDefense());
            }
        }

        //сохраняем состояние всех unit,
        //если unit DIE, удаляем его из сражения со сбросом статуса сражения
        for(Unit unit: units) {
            if(unit.getStatus().equals(Status.DIE)) {
                if(unit.getUnitType().equals(UnitType.USER)) {
                    unit.setHp(1);
                    unit.setActionEnd(false);
                    unit.setStatus(Status.LOSS);
                    unit.setFight(null);
                    unit.set_teamType(null);
                    unit.set_damage(null);
                    unit.set_applyType(null);
                    unit.set_targetId(null);
                    unitRepository.save(unit);
                }
                else {
                    unit.setStatus(Status.LOSS);
                    unitRepository.delete(unit);
                }
                System.out.println(unit.getName() + " удален из сражения");
            }
            else {
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.set_damage(0L);
                unit.set_applyType(ApplyType.NONE);
                unit.set_targetId(0L);
                unitRepository.save(unit);
                System.out.println("Настройки " + unit.getName() + " сброшены для следующего раунда");
            }
        }

        //удаляем из списка unit со статусом LOSS
        units.removeIf(unit -> unit.getStatus().equals(Status.LOSS));

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
            fight.setCountRound(fight.getCountRound() + 1);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            System.out.println("-->Запуск следующего раунда");
        }
        //если в первой команде есть игроки, а во второй нет, завершаем бой,
        //сохраняем результаты победы у unit из второй команды
        else if(!teamOne.isEmpty() & teamTwo.isEmpty()) {
            for(Unit unit: teamOne) {
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setStatus(Status.WIN);
                unit.setFight(null);
                unit.set_teamType(null);
                unit.set_damage(null);
                unit.set_applyType(null);
                unit.set_targetId(null);
                unitRepository.save(unit);
                System.out.println(unit.getName() + " победил в сражении");
            }
            endFight = true;
            fight.setFightEnd(true);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
        //если во второй команде нет игроков со статусом FIGHT, а в первой есть, завершаем бой,
        //сохраняем результаты победы у unit из первой команды
        else {
            for(Unit unit: teamTwo) {
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setStatus(Status.WIN);
                unit.set_teamType(null);
                unit.set_damage(null);
                unit.set_applyType(null);
                unit.set_targetId(null);
                unitRepository.save(unit);
                System.out.println(unit.getName() + " победил в сражении");
            }
            endFight = true;
            fight.setFightEnd(true);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            System.out.println(fightId + " сражение завершено и удалено из map");
        }
    }

    private StringBuilder calculateDamage(Unit unit, UnitJson unitJson, Unit target, UnitJson targetJson) {
        //рассчитываем урон, который нанес текущий unit противнику
        int unitHit = unit.getDamage();
        int targetDefense = target.getDefense();
        double unitHitDouble = unitHit * 1.0;
        double targetDefenseDouble = targetDefense * 1.0;
        if(unitHit <= 1) unitHit = 1;
        if(targetDefense <= 1) targetDefense = 1;
        int result = (int) ((int) (((unitHit - Math.random() * 10) * ((double) unitHit / (unitHit + targetDefense)))) * (unitHitDouble / targetDefenseDouble));
        if(result <= 0) result = 0;
        if(result > unit.getDamage()) result = randomUtil.getRNum30(Math.toIntExact(unit.getDamage()));
        System.out.println("unit " + unit.getName() + " нанес урон " + result);
        target.setHp(target.getHp() - result);

        if(target.getHp() <= 0) {
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
                .append(" умением обычная атака")
                .append("]");
    }

    //установка урона AI
    @Transactional
    private void setAiAttack() {
        Optional<Fight> fight = fightRepository.findById(fightId);
        if(fight.isEmpty()) return;

        //делим на команды всех unit
        List<Unit> teamOne = new ArrayList<>();
        List<Unit> teamTwo = new ArrayList<>();
        for(Unit unit: fight.get().getUnits()) {
            if(unit.get_teamNumber() == 1) {
                teamOne.add(unit);
            }
            else {
                teamTwo.add(unit);
            }
        }

        //TODO сделать случайный выбор атаки
        //все unit первой команды применяют атаку на любом unit из второй команды
        for(Unit aiUnit : teamOne){
            if(aiUnit.getUnitType().equals(UnitType.AI)) {
                int target = randomUtil.getRandomFromTo(0, teamTwo.size() - 1);
                Long targetId = teamTwo.get(target).getId();
                aiUnit.set_abilityId(0L);
                aiUnit.set_targetId(targetId);
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }

        //все unit второй команды применяют атаку на любом unit из первой команды
        for(Unit aiUnit : teamTwo){
            if(aiUnit.getUnitType().equals(UnitType.AI)) {
                int target = randomUtil.getRandomFromTo(0, teamOne.size() - 1);
                Long targetId = teamOne.get(target).getId();
                aiUnit.set_abilityId(0L);
                aiUnit.set_targetId(targetId);
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }

        endAiAttack = true;
    }
}
