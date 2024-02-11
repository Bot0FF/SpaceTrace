package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Subject;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.unit.UnitEffect;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class MagActionHandler {
    private final RandomUtil randomUtil;
    //рассчитываем урона при атаке умением
    //TODO настроить блок и уворот, расход маны
    public StringBuilder calculateDamageAbility(Unit unit, Unit target, Subject ability) {
        //расчет уворота
        if(randomUtil.getDoubleChance() <= target.getChanceEvade())  {
            unit.setActionEnd(true);
            return new StringBuilder()
                    .append("[")
                    .append(target.getName())
                    .append(" уклонился от удара ")
                    .append(unit.getName())
                    .append("]");
        }

        //получаем модификатор магической атаки unit
        double unitMagDamageModifier = unit.getMagModifier();
        //если тип магии примененного умения совпадает с типом магии надетого оружия, модификаторы складываются
        if(ability.getSkillType().name().equals(unit.getWeapon().getApplyType())) {
            unitMagDamageModifier += unit.getWeapon().getMagDamageModifier();
        }
        //получаем магический урон умножением модификатора на урон умения
        double unitHit = unitMagDamageModifier * ability.getMagDamage();
        double targetDefense = (target.getMagDefense()) * 1.0;

        if (unitHit <= 1) unitHit = 1;
        if (targetDefense <= 1) targetDefense = 1;
        double damageMultiplier = unitHit / (unitHit + targetDefense);
        int totalDamage = (int) Math.round(unitHit * damageMultiplier);
        int result = randomUtil.getRNum30(totalDamage);
        if (result <= 0) result = 0;
        System.out.println("unit " + unit.getName() + " нанес урон умением" + ability.getName() + " равный " + result);
        target.setHp(target.getHp() - result);

        //устанавливаем target параметры по результатам расчета
        if (target.getHp() <= 0) {
            target.setActionEnd(true);
            target.setStatus(Status.LOSS);
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
                .append(target.getWeapon().getName())
                .append("]");
    }

    //расчет восстановления при использовании умения
    //TODO добавить модификатор увеличивающий результат применения умения
    public StringBuilder calculateRecoveryAbility(Unit unit, Unit target, Subject ability) {
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
}
