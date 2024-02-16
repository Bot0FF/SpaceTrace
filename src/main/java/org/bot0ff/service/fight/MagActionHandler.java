package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Ability;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.util.Constants;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class MagActionHandler {
    private final RandomUtil randomUtil;

    //рассчитываем урона при атаке умением
    public StringBuilder calculateDamageAbility(Unit unit, Unit target, Ability ability) {
        //расчет уворота
        if(randomUtil.getDoubleChance() <= target.getChanceEvade())  {
            return new StringBuilder()
                    .append("[")
                    .append(target.getName())
                    .append(" уклонился от заклинания ")
                    .append(ability.getName())
                    .append("]");
        }

        //получаем общий модификатор магических умений unit
        double unitMagModifier = unit.getMagModifier();
        //System.out.println("Модификатор магического урона " + unit.getName() + "=" + unitMagModifier);
        //если тип магии примененного умения совпадает с типом магии надетого оружия, модификаторы складываются
        if(ability.getSkillType().name().equals(unit.getWeapon().getSkillType())) {
            unitMagModifier += unit.getWeapon().getMagModifier();
            //System.out.println("К базовому модификатору магического урона добавлен модификатор оружия=" + unitMagModifier);
        }
        //прибавляем к общему модификатору модификатор навыка, соответствующего навыку умения
        switch (ability.getSkillType()){
            case FIRE -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getFire()) * 1.0 / 100);
            case WATER -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getWater()) * 1.0 / 100);
            case LAND -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getLand()) * 1.0 / 100);
            case AIR -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getAir()) * 1.0 / 100);
        }
        //System.out.println("К базовому модификатору магического урона добавлен модификатор навыка=" + unitMagModifier);

        //получаем магический урон умножением модификатора на урон умения
        double unitHit = unitMagModifier * ability.getMagDamage();
        //System.out.println("Модификатор умножен на урон умения. Итоговый магический урон unit=" + unitHit);
        double targetDefense = (target.getMagDefense()) * 1.0;

        if (unitHit <= 1) unitHit = 1;
        if (targetDefense <= 1) targetDefense = 1;
        double damageMultiplier = unitHit / (unitHit + targetDefense);
        int totalDamage = (int) Math.round(unitHit * damageMultiplier);
        int result = randomUtil.getRNum30(totalDamage);
        if (result <= 0) result = 0;
        //System.out.println(unit.getName() + " нанес урон умением " + ability.getName() + " равный " + result);
        target.setHp(target.getHp() - result);

        //устанавливаем target параметры по результатам расчета
        if (target.getHp() <= 0) {
            target.setActionEnd(false);
            target.setStatus(Status.LOSS);
            System.out.println(target.getName() + " ход отменен. HP <= 0");
        }

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
    public StringBuilder calculateRecoveryAbility(Unit unit, Unit target, Ability ability) {
        int result = 0;
        String characteristic = "";
        String duration = "";

        //получаем общий модификатор магических умений unit
        double unitMagModifier = unit.getMagModifier();
        //если тип магии примененного умения совпадает с типом магии надетого оружия, модификаторы складываются
        if(ability.getSkillType().name().equals(unit.getWeapon().getSkillType())) {
            unitMagModifier += unit.getWeapon().getMagModifier();
        }
        //прибавляем к общему модификатору модификатор навыка, соответствующего навыку умения
        switch (ability.getSkillType()){
            case FIRE -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getFire()) * 1.0 / 100);
            case WATER -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getWater()) * 1.0 / 100);
            case LAND -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getLand()) * 1.0 / 100);
            case AIR -> unitMagModifier += (getSkillLevel(unit.getUnitSkill().getAir()) * 1.0 / 100);
        }

        if (ability.getDuration() == 0) {
            if (ability.getHp() != 0) {
                int unitImpact = (int) (unitMagModifier * ability.getHp());
                characteristic = " здоровья ";
                if ((target.getHp() + unitImpact) > target.getMaxHp()) {
                    result = target.getMaxHp() - target.getHp();
                    target.setHp(target.getHp() + result);
                }
                else {
                    result = unitImpact;
                    target.setHp(target.getHp() + unitImpact);
                }
            }
        }

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(" восстановил ")
                .append(result)
                .append(characteristic)
                .append(" игроку ")
                .append(target.getName())
                .append(" умением ")
                .append(ability.getName())
                .append(duration)
                .append("]");
    }

    //расчет повышения характеристик при использовании умения
    public StringBuilder calculateBoostAbility(Unit unit, Unit target, Ability ability) {
        double result = 0;
        String characteristic = "";

        if (ability.getHp() != 0) {
            characteristic = " максимальное здоровье ";
            result = ability.getHp();
            target.getFightEffect().setE_Hp(ability.getHp());
            target.getFightEffect().setDE_Hp(ability.getDuration() + 1);
        }
        else if (ability.getMana() != 0) {
            characteristic = " максимальную ману ";
            result = ability.getHp();
            target.getFightEffect().setE_Mana(ability.getMana());
            target.getFightEffect().setDE_Mana(ability.getDuration() + 1);
        }
        else if (ability.getPhysEffect() != 0) {
            characteristic = " физический урон ";
            result = ability.getHp();
            target.getFightEffect().setE_PhysEff(ability.getPhysEffect());
            target.getFightEffect().setDE_PhysEff(ability.getDuration() + 1);
        }
        else if (ability.getMagEffect() != 0) {
            characteristic = " магическую силу ";
            result = ability.getHp();
            target.getFightEffect().setE_MagEff(ability.getMagEffect());
            target.getFightEffect().setDE_MagEff(ability.getDuration() + 1);
        }
        else if (ability.getPhysDefense() != 0) {
            characteristic = " физическую защиту ";
            result = ability.getHp();
            target.getFightEffect().setE_PhysDef(ability.getPhysDefense());
            target.getFightEffect().setDE_PhysDef(ability.getDuration() + 1);
        }
        else if (ability.getMagDefense() != 0) {
            characteristic = " магическую защиту ";
            result = ability.getHp();
            target.getFightEffect().setE_MagEff(ability.getMagEffect());
            target.getFightEffect().setDE_MagEff(ability.getDuration() + 1);
        }
        else if (ability.getStrength() != 0) {
            characteristic = " силу ";
            result = ability.getHp();
            target.getFightEffect().setE_Str(ability.getStrength());
            target.getFightEffect().setDE_Str(ability.getDuration() + 1);
        }
        else if (ability.getIntelligence() != 0) {
            characteristic = " интеллект ";
            result = ability.getHp();
            target.getFightEffect().setE_Intel(ability.getIntelligence());
            target.getFightEffect().setDE_Intel(ability.getDuration() + 1);
        }
        else if (ability.getDexterity() != 0) {
            characteristic = " ловкость ";
            result = ability.getHp();
            target.getFightEffect().setE_Dext(ability.getDexterity());
            target.getFightEffect().setDE_Dext(ability.getDuration() + 1);
        }
        else if (ability.getEndurance() != 0) {
            characteristic = " выносливость ";
            result = ability.getHp();
            target.getFightEffect().setE_Endur(ability.getEndurance());
            target.getFightEffect().setDE_Endur(ability.getDuration() + 1);
        }
        else if (ability.getLuck() != 0) {
            characteristic = " удачу ";
            result = ability.getHp();
            target.getFightEffect().setE_Luck(ability.getLuck());
            target.getFightEffect().setDE_Luck(ability.getLuck() + 1);
        }
        else if (ability.getInitiative() != 0) {
            characteristic = " инициативу ";
            result = ability.getHp();
            target.getFightEffect().setE_Init(ability.getInitiative());
            target.getFightEffect().setDE_Init(ability.getDuration() + 1);
        }
        else if (ability.getBlock() != 0) {
            characteristic = " шанс блокирования ";
            result = ability.getHp();
            target.getFightEffect().setE_Block(ability.getBlock());
            target.getFightEffect().setDE_Block(ability.getDuration() + 1);
        }
        else if (ability.getEvade() != 0) {
            characteristic = " шанс уворота ";
            result = ability.getHp();
            target.getFightEffect().setE_Evade(ability.getEvade());
            target.getFightEffect().setDE_Evade(ability.getDuration() + 1);
        }

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(" повысил ")
                .append(characteristic)
                .append(" на ")
                .append(result)
                .append(" единиц(ы) игроку ")
                .append(target.getName())
                .append(" умением ")
                .append(ability.getName())
                .append(" на ")
                .append(ability.getDuration())
                .append(" раунда]");
    }

    //расчет понижения характеристик при использовании умения
    public StringBuilder calculateLowerAbility(Unit unit, Unit target, Ability ability) {
        double result = 0;
        String characteristic = "";

        if (ability.getHp() != 0) {
            characteristic = " максимальное здоровье ";
            result = ability.getHp();
            target.getFightEffect().setE_Hp(ability.getHp());
            target.getFightEffect().setDE_Hp(ability.getDuration());
        }
        else if (ability.getMana() != 0) {
            characteristic = " максимальную ману ";
            result = ability.getHp();
            target.getFightEffect().setE_Mana(ability.getMana());
            target.getFightEffect().setDE_Mana(ability.getDuration());
        }
        else if (ability.getPhysEffect() != 0) {
            characteristic = " физический урон ";
            result = ability.getHp();
            target.getFightEffect().setE_PhysEff(ability.getPhysEffect());
            target.getFightEffect().setDE_PhysEff(ability.getDuration());
        }
        else if (ability.getMagEffect() != 0) {
            characteristic = " магическую силу ";
            result = ability.getHp();
            target.getFightEffect().setE_MagEff(ability.getMagEffect());
            target.getFightEffect().setDE_MagEff(ability.getDuration());
        }
        else if (ability.getPhysDefense() != 0) {
            characteristic = " физическую защиту ";
            result = ability.getHp();
            target.getFightEffect().setE_PhysDef(ability.getPhysDefense());
            target.getFightEffect().setDE_PhysDef(ability.getDuration());
        }
        else if (ability.getMagDefense() != 0) {
            characteristic = " магическую защиту ";
            result = ability.getHp();
            target.getFightEffect().setE_MagEff(ability.getMagEffect());
            target.getFightEffect().setDE_MagEff(ability.getDuration());
        }
        else if (ability.getStrength() != 0) {
            characteristic = " силу ";
            result = ability.getHp();
            target.getFightEffect().setE_Str(ability.getStrength());
            target.getFightEffect().setDE_Str(ability.getDuration());
        }
        else if (ability.getIntelligence() != 0) {
            characteristic = " интеллект ";
            result = ability.getHp();
            target.getFightEffect().setE_Intel(ability.getIntelligence());
            target.getFightEffect().setDE_Intel(ability.getDuration());
        }
        else if (ability.getDexterity() != 0) {
            characteristic = " ловкость ";
            result = ability.getHp();
            target.getFightEffect().setE_Dext(ability.getDexterity());
            target.getFightEffect().setDE_Dext(ability.getDuration());
        }
        else if (ability.getEndurance() != 0) {
            characteristic = " выносливость ";
            result = ability.getHp();
            target.getFightEffect().setE_Endur(ability.getEndurance());
            target.getFightEffect().setDE_Endur(ability.getDuration());
        }
        else if (ability.getLuck() != 0) {
            characteristic = " удачу ";
            result = ability.getHp();
            target.getFightEffect().setE_Luck(ability.getLuck());
            target.getFightEffect().setDE_Luck(ability.getLuck());
        }
        else if (ability.getInitiative() != 0) {
            characteristic = " инициативу ";
            result = ability.getHp();
            target.getFightEffect().setE_Init(ability.getInitiative());
            target.getFightEffect().setDE_Init(ability.getDuration());
        }
        else if (ability.getBlock() != 0) {
            characteristic = " шанс блокирования ";
            result = ability.getHp();
            target.getFightEffect().setE_Block(ability.getBlock());
            target.getFightEffect().setDE_Block(ability.getDuration());
        }
        else if (ability.getEvade() != 0) {
            characteristic = " шанс уворота ";
            result = ability.getHp();
            target.getFightEffect().setE_Evade(ability.getEvade());
            target.getFightEffect().setDE_Evade(ability.getDuration());
        }

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(" понизил ")
                .append(characteristic)
                .append(" на ")
                .append(result)
                .append(" единиц(ы) игроку ")
                .append(target.getName())
                .append(" умением ")
                .append(ability.getName())
                .append(" на ")
                .append(ability.getDuration())
                .append(" раунда]");
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
