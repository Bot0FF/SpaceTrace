package org.bot0ff.util.converter;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.bot0ff.dto.UnitDto;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.unit.UnitEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Конвертирует Unit в UnitDto и наоборот, для сохранения в БД */

public class DtoConverter {

    public UnitDto unitToUnitDto(Unit unit) {
        return new UnitDto(
                unit.getId(),
                unit.getName(),
                unit.getSubjectType(),
                unit.getStatus(),
                unit.isActionEnd(),
                unit.getLocationId(),
                unit.getHp(),
                getMaxHp(unit),
                unit.getMana(),
                getMaxMana(unit),
                getPhysDamage(unit),
                getMagModifier(unit),
                getPhysDefense(unit),
                getMagDefense(unit),
                getInitiative(unit),
                getRegeneration(unit),
                getMeditation(unit),
                getChanceBlock(unit),
                getChanceEvade(unit),
                unit.getPointAction(),
                unit.getMaxPointAction(),
                unit.getStrength(),
                unit.getIntelligence(),
                unit.getDexterity(),
                unit.getEndurance(),
                unit.getLuck(),
                unit.getBonusPoint(),
                unit.getUnitSkill(),
                unit.getCurrentAbility(),
                unit.getAllAbility(),
                unit.getWeapon(),
                unit.getHead(),
                unit.getHand(),
                unit.getBody(),
                unit.getLeg(),
                unit.getFight(),
                unit.getFightPosition(),
                unit.getFightEffect(),
                unit.getTeamNumber(),
                unit.getAbilityId(),
                unit.getTargetId()
        );
    }

    //конвертирует UnitDto в Unit для сохранения в БД
    public Unit unitDtoToUnit(UnitDto unit) {
        return new Unit(
                unit.getId(),
                unit.getName(),
                unit.getSubjectType(),
                unit.getStatus(),
                unit.isActionEnd(),
                unit.getLocationId(),
                unit.getHp(),
                unit.getMana(),
                unit.getPointAction(),
                unit.getMaxPointAction(),
                unit.getStrength(),
                unit.getIntelligence(),
                unit.getDexterity(),
                unit.getEndurance(),
                unit.getLuck(),
                unit.getBonusPoint(),
                unit.getUnitSkill(),
                unit.getCurrentAbility(),
                unit.getAllAbility(),
                unit.getWeapon(),
                unit.getHead(),
                unit.getHand(),
                unit.getBody(),
                unit.getLeg(),
                unit.getFight(),
                unit.getUnitFightPosition(),
                unit.getUnitFightEffect(),
                unit.getTeamNumber(),
                unit.getAbilityId(),
                unit.getTargetId()
        );
    }

    //полный физический урон
    private int getPhysDamage(Unit unit) {
        int fullPhysDamage = 0;
        if(unit.getWeapon() == null || unit.getWeapon().getApplyType() == null) return 1;
        switch (unit.getWeapon().getApplyType()) {
            case "ONE_HAND" -> {
                double physDamageModifier = (((unit.getStrength() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getOneHand()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
            case "TWO_HAND" -> {
                double physDamageModifier = (((unit.getStrength() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getTwoHand()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
            case "BOW" -> {
                double physDamageModifier = (((unit.getDexterity() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getBow()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
            case "FIRE" -> {
                double physDamageModifier = (((unit.getIntelligence() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getFire()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
            case "WATER" -> {
                double physDamageModifier = (((unit.getIntelligence() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getWater()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
            case "LAND" -> {
                double physDamageModifier = (((unit.getIntelligence() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getLand()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
            case "AIR" -> {
                double physDamageModifier = (((unit.getIntelligence() * 1.0) / 100) + 1) + (((unit.getLuck() * 1.0) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unit.getUnitSkill().getAir()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (unit.getWeapon().getPhysDamage() + 1));
            }
        }
        //прибавляем к базовому урону эффекты боя, если есть
        if(unit.getFightEffect() != null) {
            for (UnitEffect effect : unit.getFightEffect()) {
                fullPhysDamage += effect.getEffectPhysDamage();
            }
        }
        return fullPhysDamage;
    }

    //модификатор усиления магического умения
    private double getMagModifier(Unit unit) {
        return ((unit.getIntelligence() * 1.0) / 100) + 1 + (((unit.getLuck() * 1.0) / 100) + 0.10);
    }

    //максимальное здоровье:
    private int getMaxHp(Unit unit) {
        double maxHpModifier = (((unit.getStrength() * 10.0) / 100) + 0.30) + (((unit.getLuck() * 3.0) / 100) + 0.10) + (((unit.getEndurance() * 15.0) / 100) + 0.10);
        //устанавливаем модификаторы прибавки здоровья от каждого вида экипировки, начальное значение зависит о навыка
        int fullHp = (int) Math.round(maxHpModifier * 10);
        if(unit.getWeapon() != null && unit.getWeapon().getDuration() > 0) {
            fullHp += unit.getWeapon().getHp();
        }
        if(unit.getHead() != null && unit.getHead().getDuration() > 0) {
            fullHp += unit.getHead().getHp();
        }
        if(unit.getHand() != null && unit.getHand().getDuration() > 0) {
            fullHp += unit.getHand().getHp();
        }
        if(unit.getBody() != null && unit.getBody().getDuration() > 0) {
            fullHp += unit.getBody().getHp();
        }
        if(unit.getLeg() != null && unit.getLeg().getDuration() > 0) {
            fullHp += unit.getLeg().getHp();
        }
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(unit.getFightEffect() != null) {
            for (UnitEffect effect : unit.getFightEffect()) {
                fullHp += effect.getEffectHp();
            }
        }
        return fullHp;
    }

    //максимальная мана
    private int getMaxMana(Unit unit) {
        double maxManaModifier = (((unit.getIntelligence() * 17.0) / 100) + 0.30) + (((unit.getLuck() * 3.0) / 100) + 0.10) + (((unit.getEndurance() * 5.0) / 100) + 0.10) + (((unit.getDexterity() * 3.0) / 100) + 0.10);
        //устанавливаем модификаторы прибавки здоровья от каждого вида экипировки, начальное значение зависит о навыка
        int fullMana = (int) Math.round(maxManaModifier * 10);
        if(unit.getWeapon() != null && unit.getWeapon().getDuration() > 0) {
            fullMana += unit.getWeapon().getMana();
        }
        if(unit.getHead() != null && unit.getHead().getDuration() > 0) {
            fullMana += unit.getHead().getMana();
        }
        if(unit.getHand() != null && unit.getHand().getDuration() > 0) {
            fullMana += unit.getHand().getMana();
        }
        if(unit.getBody() != null && unit.getBody().getDuration() > 0) {
            fullMana += unit.getBody().getMana();
        }
        if(unit.getLeg() != null && unit.getLeg().getDuration() > 0) {
            fullMana += unit.getLeg().getMana();
        }
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(unit.getFightEffect() != null) {
            for (UnitEffect effect : unit.getFightEffect()) {
                fullMana += effect.getEffectMana();
            }
        }
        return fullMana;
    }

    //физическая защита
    private int getPhysDefense(Unit unit) {
        double defenseModifier = (((unit.getStrength() * 15.0) / 100) + 0.30) + (((unit.getLuck() * 5.0) / 100) + 0.10) + (((unit.getEndurance() * 5.0) / 100) + 0.10) + (((unit.getDexterity() * 2.0) / 100) + 0.10);
        //устанавливаем модификаторы прибавки здоровья от каждого вида экипировки, начальное значение зависит о навыка
        int fullDefense = (int) Math.round(defenseModifier * 10);
        if(unit.getWeapon() != null && unit.getWeapon().getDuration() > 0) {
            fullDefense += unit.getWeapon().getPhysDefense();
        }
        if(unit.getHead() != null && unit.getHead().getDuration() > 0) {
            fullDefense += unit.getHead().getPhysDefense();
        }
        if(unit.getHand() != null && unit.getHand().getDuration() > 0) {
            fullDefense += unit.getHand().getPhysDefense();
        }
        if(unit.getBody() != null && unit.getBody().getDuration() > 0) {
            fullDefense += unit.getBody().getPhysDefense();
        }
        if(unit.getLeg() != null && unit.getLeg().getDuration() > 0) {
            fullDefense += unit.getLeg().getPhysDefense();
        }
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(unit.getFightEffect() != null) {
            for (UnitEffect effect : unit.getFightEffect()) {
                fullDefense += effect.getDurationEffectPhysDefense();
            }
        }
        return fullDefense;
    }

    //магическая защита
    private int getMagDefense(Unit unit) {
        double defenseModifier = (((unit.getIntelligence() * 15.0) / 100) + 0.30) + (((unit.getLuck() * 5.0) / 100) + 0.10) + (((unit.getEndurance() * 5.0) / 100) + 0.10) + (((unit.getDexterity() * 2.0) / 100) + 0.10);
        //устанавливаем модификаторы прибавки здоровья от каждого вида экипировки, начальное значение зависит о навыка
        int fullDefense = (int) Math.round(defenseModifier * 10);
        if(unit.getWeapon() != null && unit.getWeapon().getDuration() > 0) {
            fullDefense += unit.getWeapon().getMagDefense();
        }
        if(unit.getHead() != null && unit.getHead().getDuration() > 0) {
            fullDefense += unit.getHead().getMagDefense();
        }
        if(unit.getHand() != null && unit.getHand().getDuration() > 0) {
            fullDefense += unit.getHand().getMagDefense();
        }
        if(unit.getBody() != null && unit.getBody().getDuration() > 0) {
            fullDefense += unit.getBody().getMagDefense();
        }
        if(unit.getLeg() != null && unit.getLeg().getDuration() > 0) {
            fullDefense += unit.getLeg().getMagDefense();
        }
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(unit.getFightEffect() != null) {
            for (UnitEffect effect : unit.getFightEffect()) {
                fullDefense += effect.getEffectMagDefense();
            }
        }
        return fullDefense;
    }

    //инициатива
    private int getInitiative(Unit unit) {
        int initiativeModifier = (int) (((unit.getLuck() * 10.0) / 100) + 1.30);
        return getRNum30(initiativeModifier);
    }

    //скорость регенерации
    private int getRegeneration(Unit unit) {
        double regenerationModifier = (((unit.getEndurance() * 10.0) / 100) + 0.30) + (getSkillLevel(unit.getUnitSkill().getRegeneration()) * 1.0 / 100);
        return (int) Math.round(regenerationModifier);
    }

    //скорость восстановления маны
    private int getMeditation(Unit unit) {
        double meditationModifier = (((unit.getIntelligence() * 10.0) / 100) + 0.30) + (getSkillLevel(unit.getUnitSkill().getMeditation()) * 1.0 / 100);
        return (int) Math.round(meditationModifier);
    }

    //шанс блока
    private double getChanceBlock(Unit unit) {
        return (int) (1 + (getSkillLevel(unit.getUnitSkill().getBlock()) * 1.0 / 100));
    }

    //шанс уклонения
    private double getChanceEvade(Unit unit) {
        return (int) (1 + (getSkillLevel(unit.getUnitSkill().getEvade()) * 1.0 / 100));
    }

    private int getSkillLevel(int skill) {
        int level = 1;
        List<Integer> levelList = new ArrayList<>(List.of(1, 1000, 2, 5000, 3, 10000));
        Collections.sort(levelList);
        for(Integer levelExp: levelList) {
            skill -= levelExp;
            if(skill < 0) break;
            level++;
        }
        return level;
    }

    //рандом +-30% от числа
    private int getRNum30(int num) {
        int min = (int) Math.round(num * 0.70);
        int max = (int) Math.round(num * 1.30);
        return new RandomDataGenerator().nextInt(min, max);
    }
}
