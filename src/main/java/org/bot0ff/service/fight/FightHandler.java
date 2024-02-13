package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Subject;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.unit.UnitEffect;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.SubjectType;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.SubjectRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.Constants;
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
    private EntityGenerator entityGenerator;
    private PhysActionHandler physActionHandler;
    private MagActionHandler magActionHandler;

    private Long fightId;
    private Instant endRoundTimer;
    private boolean endAiAttack;
    private boolean endFight;

    public FightHandler(Long fightId,
                        UnitRepository unitRepository,
                        FightRepository fightRepository,
                        SubjectRepository subjectRepository,
                        EntityGenerator entityGenerator,
                        PhysActionHandler physActionHandler,
                        MagActionHandler magActionHandler) {
        this.unitRepository = unitRepository;
        this.fightRepository = fightRepository;
        this.subjectRepository = subjectRepository;
        this.entityGenerator = entityGenerator;
        this.physActionHandler = physActionHandler;
        this.magActionHandler = magActionHandler;

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
                    endAiAttack = false;
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        if (!endAiAttack) {
                            setAiAttack();
                            endAiAttack = true;
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

        //если сражение не найдено в бд, заканчиваем обработку сражения
        if (optionalFight.isEmpty()) {
            endFight = true;
            return;
        }
        Fight fight = optionalFight.get();

        //получаем участников сражения
        List<Unit> units = optionalFight.get().getUnits();

        //сортируем unit в порядке убывания инициативы
        units.sort(Comparator.comparingLong(u -> getRNum30(u.getInitiative())));
        List<String> unitName = units.stream().map(Unit::getName).toList();
        System.out.println("Сортировка по убыванию инициативы " + unitName);

        for (Unit unit : units) {
            if (unit.isActionEnd() & unit.getStatus().equals(Status.FIGHT)) {
                //находим примененное умение из бд
                Optional<Subject> ability = subjectRepository.findById(unit.getAbilityId());

                //если умение не найдено, рассчитываем как атаку оружием
                if(ability.isEmpty()) {
                    //если цель-unit не найден, переходим к следующей итерации цикла
                    Optional<Unit> optionalTarget = units.stream().filter(t -> t.getId().equals(unit.getTargetId())).findFirst();
                    if (optionalTarget.isEmpty()) continue;

                    Unit target = optionalTarget.get();
                    //если дальность применения оружия достает до расположения выбранного противника на шкале сражения, считаем нанесенный урон
                    if(unit.getFightPosition() - target.getFightPosition() == 0) {
                        StringBuilder result = physActionHandler.calculateDamageWeapon(unit, target);
                        resultRound.append(result);
                        System.out.println("Применено одиночное умение атаки");
                    }
                    else if(unit.getFightPosition() - target.getFightPosition() < 0) {
                        if((unit.getFightPosition() + unit.getWeapon().getDistance()) >= target.getFightPosition()) {
                            StringBuilder result = physActionHandler.calculateDamageWeapon(unit, target);
                            resultRound.append(result);
                            System.out.println("Применено одиночное умение атаки");
                        }
                        else {
                            resultRound.append("[");
                            resultRound.append(unit.getName());
                            resultRound.append(" не достал до противника ");
                            resultRound.append(target.getName());
                            resultRound.append(" при атаке]");
                            System.out.println(unit.getName() + " не достал до противника " + target.getName() + " при атаке");
                        }
                    }
                    else if(unit.getFightPosition() - target.getFightPosition() > 0) {
                        if((unit.getFightPosition() - unit.getWeapon().getDistance()) <= target.getFightPosition()) {
                            StringBuilder result = physActionHandler.calculateDamageWeapon(unit, target);
                            resultRound.append(result);
                            System.out.println("Применено одиночное умение атаки");
                        }
                        else {
                            resultRound.append("[");
                            resultRound.append(unit.getName());
                            resultRound.append(" не достал до противника ");
                            resultRound.append(target.getName());
                            resultRound.append(" при атаке]");
                            System.out.println(unit.getName() + " не достал до противника " + target.getName() + " при атаке");
                        }
                    }
                }
                else {
                    //определяем область применения умения (одиночное или массовое)
                    switch (ability.get().getRangeType()) {
                        //одиночные умения
                        case ONE -> {
                            //Ищем unit на которого применено умение
                            Optional<Unit> optionalTarget = units.stream().filter(t -> t.getId().equals(unit.getTargetId())).findFirst();
                            if (optionalTarget.isEmpty()) continue;

                            Unit target = optionalTarget.get();
                            //определяем тип действия примененного умения (урон, восстановление, повышение, понижение)
                            switch (ability.get().getApplyType()) {
                                case DAMAGE -> {
                                    StringBuilder result = magActionHandler.calculateDamageAbility(unit, target, ability.get());
                                    resultRound.append(result);
                                }
                                case RECOVERY -> {
                                    StringBuilder result = magActionHandler.calculateRecoveryAbility(unit, target, ability.get());
                                    resultRound.append(result);
                                    System.out.println("Применено одиночное умение восстановления");
                                }
                                case BOOST -> {
                                    StringBuilder result = magActionHandler.calculateBoostAbility(unit, target, ability.get());
                                    resultRound.append(result);
                                    System.out.println("Применено одиночное повышающее умение");
                                }
                                case LOWER -> {
                                    System.out.println("Применено одиночное понижающее умение");
                                }
                            }
                        }
                        //массовые умения
                        case ALL -> {
                            switch (ability.get().getApplyType()) {
                                case DAMAGE -> {
                                    //ищем units из команды противников
                                    List<Unit> opponentUnits = units.stream().filter(u -> !u.getTeamNumber().equals(unit.getTeamNumber())).toList();
                                    System.out.println("Применено массовое умение атаки");
                                }
                                case RECOVERY -> {
                                    //ищем units из команды союзников
                                    List<Unit> opponentUnits = units.stream().filter(u -> u.getTeamNumber().equals(unit.getTeamNumber())).toList();
                                    System.out.println("Применено массовое умение восстановления");
                                }
                                case BOOST -> {
                                    //ищем units из команды союзников
                                    List<Unit> opponentUnits = units.stream().filter(u -> u.getTeamNumber().equals(unit.getTeamNumber())).toList();
                                    System.out.println("Применено массовое повышающее умение");
                                }
                                case LOWER -> {
                                    //ищем units из команды противников
                                    List<Unit> opponentUnits = units.stream().filter(u -> !u.getTeamNumber().equals(unit.getTeamNumber())).toList();
                                    System.out.println("Применено массовое понижающее умение");
                                }
                            }
                        }
                        default -> System.out.println("Умение не выбрано");
                    }
                    System.out.println("Переход хода к следующему юниту в раунде");
                }
            }
        }

        //проверяем длительность эффектов
        units.forEach(this::refreshUnitEffect);

        //устанавливаем состояние характеристик на 0, если после завершения действия эффекта
        //характеристика стала ниже 0
        units.forEach(unit -> {
            if(unit.getHp() <= 0) {
                unit.setStatus(Status.LOSS);
            }
            if(unit.getMaxHp() <= 0) {
                unit.setStatus(Status.LOSS);
            }
            if(unit.getMana() <= 0) {
                unit.setMana(0);
            }
        });

        //сохраняем состояние всех unit,
        //если unit USER и DIE, удаляем его из сражения со сбросом статуса сражения, если AI удаляем из бд
        for (Unit unit : units) {
            if (unit.getStatus().equals(Status.LOSS)) {
                //сохранение вещи на локации в случае поражения aiUnit
                switch (unit.getSubjectType()) {
                    case AI -> {
                        unit.setFight(null);
                        entityGenerator.setNewThing(unit.getLocationId());
                    }
                    case USER -> unit.setHp(1);
                }
                unit.setFightEffect(null);
                unit.setFightPosition(null);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setTargetId(null);
                unit.setActionEnd(false);
                unit.setPointAction(unit.getMaxPointAction());
                unitRepository.save(unit);
                fight.getUnitsLoss().add(unit.getId());
            }
            //сбрасываем настройки unit
            else {
                unit.setActionEnd(false);
                unit.setPointAction(unit.getMaxPointAction());
                unit.setAbilityId(0L);
                unit.setTargetId(0L);
                unitRepository.save(unit);
                System.out.println("Настройки " + unit.getName() + " сброшены для следующего раунда");
            }
        }

        //удаляем из списка unit со статусом LOSS
        units.removeIf(unit -> unit.getStatus().equals(Status.LOSS));

        //делим на команды всех unit
        List<Unit> teamOne = new ArrayList<>(units.stream().filter(unit -> unit.getTeamNumber() == 1).toList());
        List<Unit> teamTwo = new ArrayList<>(units.stream().filter(unit -> unit.getTeamNumber() == 2).toList());

        //если в обеих командах нет units, сражение завершается
        if (teamOne.isEmpty() & teamTwo.isEmpty()) {
            endFight = true;
            optionalFight.get().setFightEnd(true);
            fightRepository.save(optionalFight.get());
            System.out.println(fightId + " сражение завершено и удалено из map. Ничья");
        }
        //если в обеих командах есть unit, сражение продолжается
        else if (!teamOne.isEmpty() & !teamTwo.isEmpty()) {
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
                switch (unit.getSubjectType()) {
                    case AI -> {
                        unit.setStatus(Status.ACTIVE);
                        unit.setFight(null);
                    }
                    case USER -> unit.setStatus(Status.WIN);
                }
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setTargetId(null);
                unit.setFightEffect(null);
                unit.setFightPosition(null);
                unit.setPointAction(unit.getMaxPointAction());
                unitRepository.save(unit);
                fight.getUnitsWin().add(unit.getId());
                System.out.println(unit.getName() + " победил в сражении");
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
                switch (unit.getSubjectType()) {
                    case AI -> {
                        unit.setStatus(Status.ACTIVE);
                        unit.setFight(null);
                    }
                    case USER -> unit.setStatus(Status.WIN);
                }
                unit.setHp(unit.getHp());
                unit.setActionEnd(false);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setTargetId(null);
                unit.setFightEffect(null);
                unit.setFightPosition(null);
                unit.setPointAction(unit.getMaxPointAction());
                unitRepository.save(unit);
                fight.getUnitsWin().add(unit.getId());
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

    //обновление состояния UnitEffect для следующего раунда
    private void refreshUnitEffect(Unit unit) {
        for(UnitEffect unitEffect : unit.getFightEffect()) {
            //расчет действующих эффектов hp
            if(unitEffect.getDurationEffectHp() > 0) {
                unitEffect.setDurationEffectHp(unitEffect.getDurationEffectHp() - 1);
            }

            //расчет действующих эффектов mana
            if(unitEffect.getDurationEffectMana() > 0) {
                unitEffect.setDurationEffectMana(unitEffect.getDurationEffectMana() - 1);
            }

            //расчет действующих эффектов физического урона
            if(unitEffect.getDurationEffectPhysDamage() > 0) {
                unitEffect.setDurationEffectPhysDamage(unitEffect.getDurationEffectPhysDamage() - 1);
            }

            //расчет действующих эффектов физической защиты
            if(unitEffect.getDurationEffectPhysDefense() > 0) {
                unitEffect.setDurationEffectPhysDefense(unitEffect.getDurationEffectPhysDefense() - 1);
            }
            //расчет действующих эффектов магического урона
            if(unitEffect.getDurationEffectMagDamage() > 0) {
                unitEffect.setDurationEffectMagDamage(unitEffect.getDurationEffectMagDamage() - 1);
            }

            //расчет действующих эффектов магической защиты
            if(unitEffect.getDurationEffectMagDefense() > 0) {
                unitEffect.setDurationEffectMagDefense(unitEffect.getDurationEffectMagDefense() - 1);
            }
        }
        //удаляем эффект из списка активных эффектов unit, если действия на все характеристики равно 0
        unit.getFightEffect().removeIf(unitEffect ->
                unitEffect.getDurationEffectHp() == 0
                        & unitEffect.getDurationEffectMana() == 0
                        & unitEffect.getDurationEffectPhysDamage() == 0
                        & unitEffect.getDurationEffectPhysDefense() == 0
                        & unitEffect.getDurationEffectMagDamage() == 0
                        & unitEffect.getDurationEffectMagDefense() == 0
        );
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
            if(aiUnit.getSubjectType().equals(SubjectType.AI)) {
                int target = getTargetForAi(teamTwo.size() - 1);
                Long targetId = teamTwo.get(target).getId();
                if(aiUnit.getAllAbility().isEmpty()) {
                    aiUnit.setAbilityId(0L);
                    aiUnit.setTargetId(targetId);
                }
                else {
                    aiUnit.setAbilityId(1L);
                    aiUnit.setTargetId(targetId);
                }
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }

        //все unit второй команды применяют атаку на любом unit из первой команды
        for(Unit aiUnit : teamTwo){
            if(aiUnit.getSubjectType().equals(SubjectType.AI)) {
                int target = getTargetForAi(teamOne.size() - 1);
                Long targetId = teamOne.get(target).getId();
                if (aiUnit.getAllAbility().isEmpty()) {
                    aiUnit.setAbilityId(0L);
                    aiUnit.setTargetId(targetId);
                } else {
                    aiUnit.setAbilityId(1L);
                    aiUnit.setTargetId(targetId);
                }
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }
    }

    public int getTargetForAi(int targetTeamSize) {
        return new RandomDataGenerator().nextInt(0, targetTeamSize);
    }

    //рандом +-30% от числа
    public int getRNum30(int num) {
        int min = (int) Math.round(num * 0.70);
        int max = (int) Math.round(num * 1.30);
        return new RandomDataGenerator().nextInt(min, max);
    }
}