package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Subject;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.unit.UnitEffect;
import org.bot0ff.util.Constants;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class MagActionHandler {
    private final RandomUtil randomUtil;

    //рассчитываем урона при атаке умением
    public StringBuilder calculateDamageAbility(Unit unit, Unit target, Subject ability) {
        //расчет уворота
        if(randomUtil.getDoubleChance() <= target.getChanceEvade())  {
            unit.setActionEnd(true);
            return new StringBuilder()
                    .append("[")
                    .append(target.getName())
                    .append(" уклонился от умения ")
                    .append(ability.getName())
                    .append("]");
        }

        //получаем общий модификатор магических умений unit
        double unitMagModifier = unit.getMagModifier();
        //если тип магии примененного умения совпадает с типом магии надетого оружия, модификаторы складываются
        if(ability.getSkillType().name().equals(unit.getWeapon().getSkillType())) {
            unitMagModifier += unit.getWeapon().getMagDamageModifier();
        }
        //прибавляем к общему модификатору модификатор навыка, соответствующего навыку умения
        switch (ability.getSkillType()){
            case FIRE -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getFire()) * 1.0 / 100);
            case WATER -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getWater()) * 1.0 / 100);
            case LAND -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getLand()) * 1.0 / 100);
            case AIR -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getAir()) * 1.0 / 100);
        }

        //получаем магическое воздействие умножением модификатора на действие умения
        double unitHit = unitMagModifier * ability.getMagImpact();
        double targetDefense = (target.getMagDefense()) * 1.0;

        if (unitHit <= 1) unitHit = 1;
        if (targetDefense <= 1) targetDefense = 1;
        double damageMultiplier = unitHit / (unitHit + targetDefense);
        int totalDamage = (int) Math.round(unitHit * damageMultiplier);
        int result = randomUtil.getRNum30(totalDamage);
        if (result <= 0) result = 0;
        System.out.println(unit.getName() + " нанес урон умением " + ability.getName() + " равный " + result);
        target.setHp(target.getHp() - result);

        //устанавливаем target параметры по результатам расчета
        if (target.getHp() <= 0) {
            target.setActionEnd(true);
            target.setStatus(Status.LOSS);
            System.out.println(target.getName() + " ход отменен. HP <= 0");
        }
        unit.setMana(unit.getMana() - ability.getCost());
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

    //расчет восстановления при использовании умения
    public StringBuilder calculateRecoveryAbility(Unit unit, Unit target, Subject ability) {
        int result = 0;
        String action = "";
        String characteristic = "";
        String duration = "";

        //получаем общий модификатор магических умений unit
        double unitMagModifier = unit.getMagModifier();
        //если тип магии примененного умения совпадает с типом магии надетого оружия, модификаторы складываются
        if(ability.getSkillType().name().equals(unit.getWeapon().getSkillType())) {
            unitMagModifier += unit.getWeapon().getMagDamageModifier();
        }
        //прибавляем к общему модификатору модификатор навыка, соответствующего навыку умения
        switch (ability.getSkillType()){
            case FIRE -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getFire()) * 1.0 / 100);
            case WATER -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getWater()) * 1.0 / 100);
            case LAND -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getLand()) * 1.0 / 100);
            case AIR -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getAir()) * 1.0 / 100);
        }

        if (ability.getDuration() == 0) {
            action = " восстановил ";
            if (ability.getHp() != 0) {
                int unitImpact = (int) (unitMagModifier * ability.getHp());
                characteristic = " здоровья ";
                result = ability.getHp();
                target.setHp(target.getHp() + unitImpact);
                if (target.getHp() > target.getMaxHp()) {
                    result = Math.abs(target.getMaxHp() - target.getHp());
                    target.setHp(target.getMaxHp());
                }
            }
            if (ability.getMana() != 0) {
                int unitImpact = (int) (unitMagModifier * ability.getMana());
                characteristic = " маны ";
                result = ability.getMana();
                target.setMana(target.getMana() + unitImpact);
                if (target.getMana() > target.getMaxMana()) {
                    result = Math.abs(target.getMaxMana() - target.getMana());
                    target.setMana(target.getMaxMana());
                }
            }
        }
        unit.setMana(unit.getMana() - ability.getCost());
        unit.setActionEnd(true);

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

    //расчет повышения/понижения характеристик при использовании умения
    public StringBuilder calculateBoostAbility(Unit unit, Unit target, Subject ability) {
        double result = 0;
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
                target.getFightEffect().add(addFightEffect(ability));
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
                target.getFightEffect().add(addFightEffect(ability));
            }
            if(ability.getPhysDamage() != 0) {
                if(ability.getPhysDamage() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " физический урон ";
                result = ability.getPhysDamage();
                target.getFightEffect().add(addFightEffect(ability));
            }
            if(ability.getPhysDefense() != 0) {
                if(ability.getPhysDefense() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " физическую защиту ";
                result = ability.getPhysDefense();
                target.getFightEffect().add(addFightEffect(ability));
            }
            if(ability.getMagDamageModifier() != 0) {
                if(ability.getMagDamageModifier() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " магический урон ";
                result = ability.getMagDamageModifier();
                target.getFightEffect().add(addFightEffect(ability));
            }
            if(ability.getMagDefense() != 0) {
                if(ability.getMagDefense() > 0) {
                    action = " повысил ";
                }
                else {
                    action = " понизил ";
                }
                characteristic = " магическую защиту ";
                result = ability.getMagDefense();
                target.getFightEffect().add(addFightEffect(ability));
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

    //добавляет эффекты умения в список действующих эффектов unit
    public UnitEffect addFightEffect(Subject ability) {
        UnitEffect unitEffect;
        if(ability.getHp() != 0) {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    ability.getHp(), ability.getDuration(),
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0
            );
        }
        else if(ability.getMana() != 0) {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    0, 0,
                    ability.getMana(), ability.getDuration(),
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0
            );
        }
        else if(ability.getPhysDamage() != 0) {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    0, 0,
                    0, 0,
                    ability.getPhysDamage(), ability.getDuration(),
                    0, 0,
                    0, 0,
                    0, 0
            );
        }
        else if(ability.getMagDamageModifier() != 0) {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    0, 0,
                    0, 0,
                    0, 0,
                    ability.getMagDamageModifier(), ability.getDuration(),
                    0, 0,
                    0, 0
            );
        }
        else if(ability.getPhysDefense() != 0) {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    ability.getPhysDefense(), ability.getDuration(),
                    0, 0
            );
        }
        else if(ability.getMagDefense() != 0) {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    ability.getMagDefense(), ability.getDuration()
            );
        }
        else {
            unitEffect = new UnitEffect(
                    ability.getId(),
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0
            );
        }
        return unitEffect;
    }

    //рассчитывает уровень навыка, исходя из опыта навыка unit
    private int getSkillLevel(int skill) {
        int level = 1;
        for(Integer levelExp: Constants.SKILL_EXP) {
            skill -= levelExp;
            if(skill < 0) break;
            level++;
        }
        return level;
    }
}
