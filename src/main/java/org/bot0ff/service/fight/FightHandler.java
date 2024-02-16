package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.bot0ff.entity.Ability;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.AbilityRepository;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.Constants;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Slf4j
public class FightHandler {
    private UnitRepository unitRepository;
    private FightRepository fightRepository;
    private AbilityRepository abilityRepository;
    private AiActionHandler aiActionHandler;
    private EntityGenerator entityGenerator;
    private PhysActionHandler physActionHandler;
    private MagActionHandler magActionHandler;

    private Long fightId;
    private Instant endRoundTimer;
    private boolean endFight;

    public FightHandler(Long fightId,
                        UnitRepository unitRepository,
                        FightRepository fightRepository,
                        AbilityRepository abilityRepository,
                        AiActionHandler aiActionHandler,
                        EntityGenerator entityGenerator,
                        PhysActionHandler physActionHandler,
                        MagActionHandler magActionHandler) {
        this.unitRepository = unitRepository;
        this.fightRepository = fightRepository;
        this.abilityRepository = abilityRepository;
        this.aiActionHandler = aiActionHandler;
        this.entityGenerator = entityGenerator;
        this.physActionHandler = physActionHandler;
        this.magActionHandler = magActionHandler;

        this.fightId = fightId;
        endRoundTimer = Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME);
        endFight = false;

        System.out.println("Запуск нового сражения");
        fightInitializer();
    }

    private void fightInitializer()  {
        Executors.newSingleThreadExecutor().execute(() -> {
            AtomicBoolean aiAction = new AtomicBoolean(false);
            do {
                if (Instant.now().isAfter(endRoundTimer)) {
                    //обработка результата раунда
                    resultRoundHandler(fightId);
                    //установка длительности нового раунда
                    this.endRoundTimer = Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME);
                    aiAction.set(false);
                } else {
                    if(!aiAction.get()) {
                        //ход AI в отдельном потоке
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                if(fightRepository.existsById(fightId)) {
                                    aiActionHandler.setAiAction(fightId);
                                    aiAction.set(true);
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } while (!endFight);
            FightService.FIGHT_MAP.remove(fightId);
        });
    }

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
            if (unit.isActionEnd() | (!unit.getAbilityId().equals(0L) | !unit.getTargetId().equals(0L))) {
                //находим примененное умение из бд
                Optional<Ability> ability = abilityRepository.findById(unit.getAbilityId());

                //если умение не найдено, рассчитываем как атаку оружием
                if(ability.isEmpty()) {
                    //если цель-unit не найден, переходим к следующей итерации цикла
                    Optional<Unit> optionalTarget = units.stream().filter(t -> t.getId().equals(unit.getTargetId())).findFirst();
                    if (optionalTarget.isEmpty()) {
                        resultRound
                                .append("[")
                                .append(unit.getName())
                                .append(" бездействовал в предыдущем раунде]");
                        continue;
                    }

                    Unit target = optionalTarget.get();
                    //если дальность применения оружия достает до расположения выбранного противника на шкале сражения, считаем нанесенный урон
                    if(unit.getHitPosition() - unit.getTargetPosition() == 0) {
                        StringBuilder result = physActionHandler.calculateDamageWeapon(unit, target);
                        resultRound.append(result);
                        //System.out.println("Атака оружием. Позиция unit " + unit.getLinePosition() + "/Позиция target " + target.getLinePosition());
                    }
                    else if(unit.getHitPosition() - unit.getTargetPosition() < 0) {
                        if((unit.getHitPosition() + unit.getWeapon().getDistance()) >= unit.getTargetPosition()) {
                            StringBuilder result = physActionHandler.calculateDamageWeapon(unit, target);
                            resultRound.append(result);
                            //System.out.println("Атака оружием. Позиция unit " + unit.getLinePosition() + "/Позиция target " + unit.getTargetPosition());
                        }
                        else {
                            resultRound.append("[");
                            resultRound.append(unit.getName());
                            resultRound.append(" не достал до противника ");
                            resultRound.append(target.getName());
                            resultRound.append(" при атаке]");
                            System.out.println("Не хватает дальности оружия для атаки. Позиция unit " + unit.getLinePosition() + "/Позиция target " + target.getLinePosition());
                        }
                    }
                    else if(unit.getHitPosition() - unit.getTargetPosition() > 0) {
                        if((unit.getHitPosition() - unit.getWeapon().getDistance()) <= unit.getTargetPosition()) {
                            StringBuilder result = physActionHandler.calculateDamageWeapon(unit, target);
                            resultRound.append(result);
                            //System.out.println("Атака оружием. Позиция unit " + unit.getLinePosition() + "/Позиция target " + unit.getTargetPosition());
                        }
                        else {
                            resultRound.append("[");
                            resultRound.append(unit.getName());
                            resultRound.append(" не достал до противника ");
                            resultRound.append(target.getName());
                            resultRound.append(" при атаке]");
                            //System.out.println("Не хватает дальности оружия для атаки. Позиция unit " + unit.getLinePosition() + "/Позиция target " + target.getLinePosition());
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
                                    StringBuilder result = magActionHandler.calculateLowerAbility(unit, target, ability.get());
                                    resultRound.append(result);
                                    System.out.println("Применено одиночное понижающее умение");
                                }
                            }
                        }
                        //массовые умения
                        case ALL -> {
                            switch (ability.get().getApplyType()) {
                                case DAMAGE -> {
                                    units.forEach(target -> {
                                        if(!target.getTeamNumber().equals(unit.getTeamNumber())) {
                                            StringBuilder result = magActionHandler.calculateDamageAbility(unit, target, ability.get());
                                            resultRound.append(result);
                                        }
                                    });
                                    System.out.println("Применено массовое умение атаки");
                                }
                                case RECOVERY -> {
                                    units.forEach(target -> {
                                        if(target.getTeamNumber().equals(unit.getTeamNumber())) {
                                            StringBuilder result = magActionHandler.calculateRecoveryAbility(unit, target, ability.get());
                                            resultRound.append(result);
                                        }
                                    });
                                    System.out.println("Применено массовое умение восстановления");
                                }
                                case BOOST -> {
                                    units.forEach(target -> {
                                        if(target.getTeamNumber().equals(unit.getTeamNumber())) {
                                            StringBuilder result = magActionHandler.calculateBoostAbility(unit, target, ability.get());
                                            resultRound.append(result);
                                        }
                                    });
                                    System.out.println("Применено массовое повышающее умение");
                                }
                                case LOWER -> {
                                    units.forEach(target -> {
                                        if(!target.getTeamNumber().equals(unit.getTeamNumber())) {
                                            StringBuilder result = magActionHandler.calculateLowerAbility(unit, target, ability.get());
                                            resultRound.append(result);
                                        }
                                    });
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
            if(unit.getMana() <= 0) {
                unit.setMana(0);
            }
        });

        //сохраняем состояние всех unit,
        //если unit USER и DIE, удаляем его из сражения со сбросом статуса сражения, если AI удаляем из бд
        for (Unit unit : units) {
            if (unit.getStatus().equals(Status.LOSS)) {
                //сохранение вещи на локации в случае поражения aiUnit
                switch (unit.getUnitType()) {
                    case AI -> {
                        unit.setFight(null);
                        entityGenerator.setNewThingToLocation(unit.getLocationId());
                    }
                    case USER -> unit.setHp(1);
                }
                unit.setFightEffect(null);
                unit.setLinePosition(null);
                unit.setTargetPosition(null);
                unit.setTeamNumber(null);
                unit.setAbilityId(null);
                unit.setHitPosition(null);
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
                unit.setHitPosition(0L);
                unit.setTargetPosition(0L);
                unit.setAbilityId(0L);
                unit.setTargetId(0L);
                unitRepository.save(unit);
                //System.out.println("Настройки " + unit.getName() + " сброшены для следующего раунда");
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
            System.out.println("-->Раунд " + fight.getCountRound() + " завершен");
        }
        //если во второй команде нет игроков, а в первой есть, завершаем бой,
        //сохраняем результаты победы у unit из первой команды
        else if (!teamOne.isEmpty() & teamTwo.isEmpty()) {
            for (Unit unit : teamOne) {
                switch (unit.getUnitType()) {
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
                unit.setHitPosition(null);
                unit.setTargetPosition(null);
                unit.setLinePosition(null);
                unit.setPointAction(unit.getMaxPointAction());
                unitRepository.save(unit);
                fight.getUnitsWin().add(unit.getId());
                //System.out.println(unit.getName() + " победил в сражении");
            }
            endFight = true;
            fight.setFightEnd(true);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            //System.out.println(fightId + " сражение завершено и удалено из map");
        }
        //если в первой команде нет игроков, а во второй есть, завершаем бой,
        //сохраняем результаты победы у unit из второй команды
        else {
            for (Unit unit : teamTwo) {
                switch (unit.getUnitType()) {
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
                unit.setHitPosition(null);
                unit.setTargetPosition(null);
                unit.setLinePosition(null);
                unit.setPointAction(unit.getMaxPointAction());
                unitRepository.save(unit);
                fight.getUnitsWin().add(unit.getId());
                //System.out.println(unit.getName() + " победил в сражении");
            }
            endFight = true;
            fight.setFightEnd(true);
            fight.getResultRound().add(String.valueOf(resultRound));
            fight.setUnits(units);
            fightRepository.save(fight);
            //System.out.println(fightId + " сражение завершено и удалено из map");
        }
    }

    //обновление состояния UnitEffect для следующего раунда
    private void refreshUnitEffect(Unit unit) {
        //расчет действующих эффектов hp
        if(unit.getFightEffect().getDE_Hp() != 0) {
            unit.getFightEffect().setDE_Hp(unit.getFightEffect().getDE_Hp() - 1);
            if(unit.getFightEffect().getDE_Hp() <= 0) {
                unit.getFightEffect().setE_Hp(0);
            }
        }
        //расчет действующих эффектов mana
        if(unit.getFightEffect().getDE_Mana() != 0) {
            unit.getFightEffect().setDE_Mana(unit.getFightEffect().getDE_Mana() - 1);
            if(unit.getFightEffect().getDE_Mana() <= 0) {
                unit.getFightEffect().setE_Mana(0);
            }
        }

        //расчет действующих эффектов физического воздействия
        if(unit.getFightEffect().getDE_PhysEff() != 0) {
            unit.getFightEffect().setDE_PhysEff(unit.getFightEffect().getDE_PhysEff() - 1);
            if(unit.getFightEffect().getDE_PhysEff() <= 0) {
                unit.getFightEffect().setE_PhysEff(0);
            }
        }

        //расчет действующих эффектов магического воздействия
        if(unit.getFightEffect().getDE_MagEff() != 0) {
            unit.getFightEffect().setDE_MagEff(unit.getFightEffect().getDE_MagEff() - 1);
            if(unit.getFightEffect().getDE_MagEff() <= 0) {
                unit.getFightEffect().setE_MagEff(0);
            }
        }

        //расчет действующих эффектов физической защиты
        if(unit.getFightEffect().getDE_PhysDef() != 0) {
            unit.getFightEffect().setDE_PhysDef(unit.getFightEffect().getDE_PhysDef() - 1);
            if(unit.getFightEffect().getDE_PhysDef() <= 0) {
                unit.getFightEffect().setE_PhysDef(0);
            }
        }

        //расчет действующих эффектов магической защиты
        if(unit.getFightEffect().getDE_MagDef() != 0) {
            unit.getFightEffect().setDE_MagDef(unit.getFightEffect().getDE_MagDef() - 1);
            if(unit.getFightEffect().getDE_MagDef() <= 0) {
                unit.getFightEffect().setE_MagDef(0);
            }
        }

        //расчет действующих эффектов силы
        if(unit.getFightEffect().getDE_Str() != 0) {
            unit.getFightEffect().setDE_Str(unit.getFightEffect().getDE_Str() - 1);
            if(unit.getFightEffect().getDE_Str() <= 0) {
                unit.getFightEffect().setE_Str(0);
            }
        }

        //расчет действующих эффектов интеллекта
        if(unit.getFightEffect().getDE_Intel() != 0) {
            unit.getFightEffect().setDE_Intel(unit.getFightEffect().getDE_Intel() - 1);
            if(unit.getFightEffect().getDE_Intel() <= 0) {
                unit.getFightEffect().setE_Intel(0);
            }
        }

        //расчет действующих эффектов ловкости
        if(unit.getFightEffect().getDE_Dext() != 0) {
            unit.getFightEffect().setDE_Dext(unit.getFightEffect().getDE_Dext() - 1);
            if(unit.getFightEffect().getDE_Dext() <= 0) {
                unit.getFightEffect().setE_Dext(0);
            }
        }

        //расчет действующих эффектов выносливости
        if(unit.getFightEffect().getDE_Endur() != 0) {
            unit.getFightEffect().setDE_Endur(unit.getFightEffect().getDE_Endur() - 1);
            if(unit.getFightEffect().getDE_Endur() <= 0) {
                unit.getFightEffect().setE_Endur(0);
            }
        }

        //расчет действующих эффектов удачи
        if(unit.getFightEffect().getDE_Luck() != 0) {
            unit.getFightEffect().setDE_Luck(unit.getFightEffect().getDE_Luck() - 1);
            if(unit.getFightEffect().getDE_Luck() <= 0) {
                unit.getFightEffect().setE_Luck(0);
            }
        }

        //расчет действующих эффектов инициативы
        if(unit.getFightEffect().getDE_Init() != 0) {
            unit.getFightEffect().setDE_Intel(unit.getFightEffect().getDE_Intel() - 1);
            if(unit.getFightEffect().getDE_Init() <= 0) {
                unit.getFightEffect().setE_Intel(0);
            }
        }

        //расчет действующих эффектов блокирования
        if(unit.getFightEffect().getDE_Block() != 0) {
            unit.getFightEffect().setDE_Block(unit.getFightEffect().getDE_Block() - 1);
            if(unit.getFightEffect().getDE_Block() <= 0) {
                unit.getFightEffect().setE_Block(0);
            }
        }

        //расчет действующих эффектов уворота
        if(unit.getFightEffect().getDE_Evade() != 0) {
            unit.getFightEffect().setDE_Evade(unit.getFightEffect().getDE_Evade() - 1);
            if(unit.getFightEffect().getDE_Evade() <= 0) {
                unit.getFightEffect().setE_Evade(0);
            }
        }
    }

    //рандом +-30% от числа
    public int getRNum30(int num) {
        int min = (int) Math.round(num * 0.70);
        int max = (int) Math.round(num * 1.30);
        return new RandomDataGenerator().nextInt(min, max);
    }
}