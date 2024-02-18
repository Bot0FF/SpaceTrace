package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.unit.UnitFightEffect;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.unit.UnitSkill;
import org.bot0ff.util.Constants;
import org.bot0ff.util.converter.UnitJsonSubjectToArmorConverter;
import org.bot0ff.util.converter.UnitJsonSubjectToEffectConverter;
import org.bot0ff.util.converter.UnitJsonSubjectToSkillConverter;

import java.util.*;

/** таблица для всех unit */
@Entity
@Table(name = "unit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    //общее
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private UnitType unitType;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Status status;

    @Column(name = "actionEnd")
    @NotNull
    private boolean actionEnd;

    /** локация */
    @Column(name = "locationId")
    @NotNull
    private Long locationId;

    /** основные характеристики */
    //здоровье
    @Column(name = "hp")
    @NotNull
    private int hp;

    @Column(name = "mana")
    @NotNull
    private int mana;

    //очки действия
    @Column(name = "pointAction")
    @NotNull
    private int pointAction;

    //максимальные очки действия
    @Column(name = "maxPointAction")
    @NotNull
    private int maxPointAction;

    /** аттрибуты */
    //сила
    @Column(name = "strength")
    @NotNull
    private int strength;

    //интеллект
    @Column(name = "intelligence")
    @NotNull
    private int intelligence;

    //ловкость
    @Column(name = "dexterity")
    @NotNull
    private int dexterity;

    //выносливость
    @Column(name = "endurance")
    @NotNull
    private int endurance;

    //удача
    @Column(name = "luck")
    @NotNull
    private int luck;

    //свободные очки для распределения
    @Column(name = "bonusPoint")
    @NotNull
    private int bonusPoint;

    /** навыки */
    @JsonIgnore
    @Convert(converter = UnitJsonSubjectToSkillConverter.class)
    @Column(name = "unitSkill")
    @NotNull
    private UnitSkill unitSkill;

    /** список умений */
    //активные умения
    @JsonIgnore
    @Column(name = "currentAbility")
    @NotNull
    private List<Long> currentAbility;

    //все умения
    @JsonIgnore
    @Column(name = "allAbility")
    @NotNull
    private List<Long> allAbility;

    /** экипировка */
    //оружие
    @JsonIgnore
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "weapon", length = 1024)
    @NotNull
    private UnitArmor weapon;

    //голова
    @JsonIgnore
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "head", length = 1024)
    @NotNull
    private UnitArmor head;

    //руки
    @JsonIgnore
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "hand", length = 1024)
    @NotNull
    private UnitArmor hand;

    //тело
    @JsonIgnore
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "body", length = 1024)
    @NotNull
    private UnitArmor body;

    //ноги
    @JsonIgnore
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "leg", length = 1024)
    @NotNull
    private UnitArmor leg;

    /** сражение */
    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "fight")
    private Fight fight;

    //позиция unit во время атаки
    @Column(name = "hitPosition")
    private Long hitPosition;

    //позиция target во время атаки unit
    @Column(name = "targetPosition")
    private Long targetPosition;

    //позиция на линии сражения
    @Column(name = "linePosition")
    private Long linePosition;

    @Convert(converter = UnitJsonSubjectToEffectConverter.class)
    @Column(name = "fightEffect", length = 1024)
    private UnitFightEffect fightEffect;

    @Column(name = "teamNumber")
    private Long teamNumber;

    @Column(name = "abilityId")
    @JsonIgnore
    private Long abilityId;

    @Column(name = "targetId")
    @JsonIgnore
    private Long targetId;

    //максимальное здоровье:
    public int getMaxHp() {
        int maxHp = (strength * 2) + (luck) + (endurance * 3) + (intelligence);
        if(weapon == null | head == null | hand == null | body == null | leg == null) return maxHp;
        maxHp = maxHp + weapon.getHp() + head.getHp() + hand.getHp() + body.getHp() + leg.getHp();
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(fightEffect != null) {
            maxHp += fightEffect.getE_Hp();
        }
        return Math.max(maxHp, 0);
    }

    //максимальная мана
    public int getMaxMana() {
        int maxMana = (intelligence * 3) + (luck) + (dexterity * 2) + (strength);
        if(weapon == null | head == null | hand == null | body == null | leg == null) return maxMana;
        maxMana = maxMana + weapon.getMana() + head.getMana() + hand.getMana() + body.getMana() + leg.getMana();
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(fightEffect != null) {
            maxMana += fightEffect.getE_Mana();
        }
        return Math.max(maxMana, 0);
    }

    //физический урон
    public int getPhysDamage() {
        int fullPhysDamage;
        switch (weapon.getSkillType()) {
            case "ONE_HAND" -> {
                double physDamageModifier = (((strength * 5.0) / 100) + 1) + (((luck * 0.9) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unitSkill.getOneHand()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (weapon.getPhysDamage() + 1));
            }
            case "TWO_HAND" -> {
                double physDamageModifier = (((strength * 5.0) / 100) + 1) + (((luck * 0.9) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unitSkill.getTwoHand()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (weapon.getPhysDamage() + 1));
            }
            case "BOW" -> {
                double physDamageModifier = (((dexterity * 5.0) / 100) + 1) + (((luck * 0.9) / 100) + 0.10);
                physDamageModifier += (getSkillLevel(unitSkill.getBow()) * 1.0 / 100);
                fullPhysDamage = (int) Math.round(physDamageModifier * (weapon.getPhysDamage() + 1));
            }
            default -> fullPhysDamage = strength + dexterity + intelligence;
        }
        //прибавляем к базовому урону эффекты боя, если есть
        if(fightEffect != null) {
            fullPhysDamage += fightEffect.getE_PhysEff();
        }
        return Math.max(fullPhysDamage, 0);
    }

    //модификатор усиления магического умения
    public double getMagModifier() {
        double magModifier = ((intelligence * 1.0) / 100) + 1 + (((luck * 1.0) / 100) + 0.10);
        //прибавляем к базовому урону эффекты боя, если есть
        if(fightEffect != null) {
            magModifier += fightEffect.getE_MagEff();
        }
        if(magModifier < 0) return 0.01;
        return Math.round (magModifier * 100.0) / 100.0;
    }

    //физическая защита
    public int getPhysDefense() {
        int physDefense = (strength * 4) + (luck) + (endurance * 2) + (dexterity);
        if(weapon == null | head == null | hand == null | body == null | leg == null) return physDefense;
        physDefense = physDefense + weapon.getPhysDefense() + head.getPhysDefense() + hand.getPhysDefense() + body.getPhysDefense() + leg.getPhysDefense();
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(fightEffect != null) {
            physDefense += fightEffect.getDE_PhysDef();
        }
        return Math.max(physDefense, 0);
    }

    //магическая защита
    public int getMagDefense() {
        int magDefense = (intelligence * 5) + (luck) + (endurance) + (dexterity * 2);
        if(weapon == null | head == null | hand == null | body == null | leg == null) return magDefense;
        magDefense = magDefense + weapon.getMagDefense() + head.getMagDefense() + hand.getMagDefense() + body.getMagDefense() + leg.getMagDefense();
        //прибавляем к максимальному здоровью эффекты боя, если есть
        if(fightEffect != null) {
            magDefense += fightEffect.getE_MagDef();
        }
        return Math.max(magDefense, 0);
    }

    //сила
    public int getStrength() {
        int str = strength;
        if(fightEffect != null) {
            str += fightEffect.getE_Str();
        }
        return Math.max(str, 0);
    }

    //интеллект
    public int getIntelligence() {
        int intel = intelligence;
        if(fightEffect != null) {
            intel += fightEffect.getE_Intel();
        }
        return Math.max(intel, 0);
    }

    //ловкость
    public int getDexterity() {
        int dex = dexterity;
        if(fightEffect != null) {
            dex += fightEffect.getE_Dext();
        }
        return Math.max(dex, 0);
    }

    //выносливость
    public int getEndurance() {
        int end = endurance;
        if(fightEffect != null) {
            end += fightEffect.getE_Endur();
        }
        return Math.max(end, 0);
    }

    //удача
    public int getLuck() {
        int lck = luck;
        if(fightEffect != null) {
            lck += fightEffect.getE_Luck();
        }
        return Math.max(lck, 0);
    }

    //инициатива
    public int getInitiative() {
        int initiative = (int) ((luck * 0.5) + (dexterity * 0.5));
        if(fightEffect != null) {
            initiative += fightEffect.getE_Init();
        }
        return Math.max(initiative, 0);
    }

    //шанс блока
    public double getChanceBlock() {
        double chanceBlock = (1 + (getSkillLevel(unitSkill.getBlock()) * 1.0 / 100) + (strength * 0.2));
        if(fightEffect != null) {
             chanceBlock += fightEffect.getE_Block();
        }
        return Math.round (chanceBlock * 100.0) / 100.0;
    }

    //шанс уклонения
    public double getChanceEvade() {
        double chanceEvade =  (1 + (getSkillLevel(unitSkill.getEvade()) * 1.0 / 100) + (dexterity * 0.2));
        if(fightEffect != null) {
             chanceEvade += fightEffect.getE_Evade();
        }
        return Math.round (chanceEvade * 100.0) / 100.0;
    }

    //скорость регенерации
    public int getRegeneration() {
        double regenerationModifier = (((endurance * 10.0) / 100) + 1) + (getSkillLevel(unitSkill.getRegeneration()) * 10.0 / 100);
        return (int) Math.round(regenerationModifier);
    }

    //скорость восстановления маны
    public int getMeditation() {
        double meditationModifier = (((intelligence * 20.0) / 100) + 1) + (getSkillLevel(unitSkill.getSpirituality()) * 12.0 / 100);
        return (int) Math.round(meditationModifier);
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
