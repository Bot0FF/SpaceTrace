package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.unit.UnitFightEffect;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.SubjectType;
import org.bot0ff.entity.unit.UnitSkill;
import org.bot0ff.util.converter.UnitJsonSubjectToArmorConverter;
import org.bot0ff.util.converter.UnitJsonSubjectToEffectConverter;
import org.bot0ff.util.converter.UnitJsonSubjectToSkillConverter;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "unit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {
    //общее
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    private SubjectType subjectType;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "actionEnd")
    private boolean actionEnd;

    /** локация */
    @Column(name = "locationId")
    @JsonIgnore
    private Long locationId;

    /** текущие характеристики */
    //здоровье
    @Column(name = "hp")
    private int hp;

    //максимальное здоровье
    @Column(name = "maxHp")
    @JsonIgnore
    private int maxHp;

    //мана
    @Column(name = "mana")
    private int mana;

    //максимальная мана
    @Column(name = "maxMana")
    @JsonIgnore
    private int maxMana;

    //урон
    @Column(name = "damage")
    @JsonIgnore
    private int damage;

    //защита
    @Column(name = "defense")
    @JsonIgnore
    private int defense;

    //уклонение
    @Column(name = "evade")
    @JsonIgnore
    private int evade;

    //максимальные очки перемещения
    @Column(name = "maxMovePoint")
    private int maxMovePoint;

    /** экипировка */
    //оружие
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "weapon")
    @JsonIgnore
    private UnitArmor weapon;

    //голова
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "head")
    @JsonIgnore
    private UnitArmor head;

    //руки
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "hand")
    @JsonIgnore
    private UnitArmor hand;

    //тело
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "body")
    @JsonIgnore
    private UnitArmor body;

    //ноги
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "leg")
    @JsonIgnore
    private UnitArmor leg;

    /** основные характеристики */
    //сила (1 очко прибавляет:
    // +3 к макс здоровью, +1 к макс мане, +3 к урону, +2 к защите, +1 к уклонению)
    @Column(name = "power")
    private int power;

    //ловкость (1 очко прибавляет:
    // +2 к макс здоровью, +2 к макс мане, +3 к урону, +1 к защите, +4 к уклонению)
    @Column(name = "agility")
    private int agility;

    //выносливость (1 очко прибавляет:
    // +3 к макс здоровью, +1 к макс мане, +1 к урону, +2 к защите, +1 к уклонению)
    @Column(name = "endurance")
    private int endurance;

    //магия (1 очко прибавляет:
    // +2 к макс здоровью, +4 к макс мане, +1 к урону, + 1 к защите, +1 к уклонению)
    @Column(name = "magic")
    private int magic;

    //свободные очки для распределения
    @Column(name = "freePoint")
    private int freePoint;

    /** навыки */
    @Convert(converter = UnitJsonSubjectToSkillConverter.class)
    @Column(name = "unitSkill")
    @JsonIgnore
    private UnitSkill unitSkill;

    /** список умений */
    @Column(name = "ability")
    @JsonIgnore
    private List<Long> ability;

    /** сражение */
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "fight")
    @JsonIgnore
    private Fight fight;

    //очки движения
    @Column(name = "movePoint")
    @JsonIgnore
    private Long movePoint;

    @Column(name = "unitFightPosition")
    private Long unitFightPosition;

    @Convert(converter = UnitJsonSubjectToEffectConverter.class)
    @Column(name = "unitFightEffect")
    @JsonIgnore
    private Map<Long, UnitFightEffect> unitFightEffect;

    @Column(name = "teamNumber")
    private Long teamNumber;

    @Column(name = "abilityId")
    @JsonIgnore
    private Long abilityId;

    @Column(name = "targetId")
    @JsonIgnore
    private Long targetId;

    //полный урон: базовый (от очков умений) + урон от оружия + модификатор навыка
    public int getFullDamage() {
        int fullDamage = damage;
        if(weapon != null && weapon.getDuration() > 0) {
            //прибавляем к базовому урону урон от оружия
            switch (weapon.getSubjectType()) {
                //если тип - оружие, к общему урону прибавляется урон оружия
                case WEAPON -> fullDamage += weapon.getDamage();
                //если тип - посох, общий урон умножается на модификатор урона посоха
                case STICK -> fullDamage = fullDamage * weapon.getDamage();
            }
            //прибавляем к базовому урону урон от навыков
            switch (weapon.getApplyType()) {
                case ONE_HAND -> fullDamage += unitSkill.getOneHand() / 10;
                case TWO_HAND -> fullDamage += unitSkill.getTwoHand() / 10;
                case BOW -> fullDamage += unitSkill.getBow() / 10;
                case FIRE -> fullDamage += unitSkill.getFire() / 10;
                case WATER -> fullDamage += unitSkill.getWater() / 10;
                case LAND -> fullDamage += unitSkill.getLand() / 10;
                case AIR -> fullDamage += unitSkill.getAir() / 10;
            }
        }
        if(head != null && head.getDuration() > 0) {
            fullDamage += head.getDamage();
        }
        if(hand != null && hand.getDuration() > 0) {
            fullDamage += hand.getDamage();
        }
        if(body != null && body.getDuration() > 0) {
            fullDamage += body.getDamage();
        }
        if(leg != null && leg.getDuration() > 0) {
            fullDamage += leg.getDamage();
        }
        //если эффекты в бою не равны нулю, применяем их
        if(unitFightEffect != null) {
            for(UnitFightEffect effect : unitFightEffect.values()) {
                fullDamage += effect.getEffectDamage();
            }
        }
        return fullDamage;
    }

    //максимальное здоровье
    public int getFullHp() {
        int fullHp = maxHp;
        if(weapon != null && weapon.getDuration() > 0) {
            fullHp += weapon.getHp();
        }
        if(head != null && head.getDuration() > 0) {
            fullHp += head.getHp();
        }
        if(hand != null && hand.getDuration() > 0) {
            fullHp += hand.getHp();
        }
        if(body != null && body.getDuration() > 0) {
            fullHp += body.getHp();
        }
        if(leg != null && leg.getDuration() > 0) {
            fullHp += leg.getHp();
        }
        //если эффекты в бою не равны нулю, применяем их
        if(unitFightEffect != null) {
            for(UnitFightEffect effect : unitFightEffect.values()) {
                fullHp += effect.getEffectHp();
            }
        }
        return fullHp;
    }

    //максимальная мана
    public int getFullMana() {
        int fullMana = maxMana;
        if(weapon != null && weapon.getDuration() > 0) {
            fullMana += weapon.getMana();
        }
        if(head != null && head.getDuration() > 0) {
            fullMana += head.getMana();
        }
        if(hand != null && hand.getDuration() > 0) {
            fullMana += hand.getMana();
        }
        if(body != null && body.getDuration() > 0) {
            fullMana += body.getMana();
        }
        if(leg != null && leg.getDuration() > 0) {
            fullMana += leg.getMana();
        }
        //если эффекты в бою не равны нулю, применяем их
        if(unitFightEffect != null) {
            for(UnitFightEffect effect : unitFightEffect.values()) {
                fullMana += effect.getEffectMana();
            }
        }
        return fullMana;
    }

    //защита
    public int getFullDefense() {
        int fullDefense = defense;
        if(weapon != null && weapon.getDuration() > 0) {
            fullDefense += weapon.getDefense();
        }
        if(head != null && head.getDuration() > 0) {
            fullDefense += head.getDefense();
        }
        if(hand != null && hand.getDuration() > 0) {
            fullDefense += hand.getDefense();
        }
        if(body != null && body.getDuration() > 0) {
            fullDefense += body.getDefense();
        }
        if(leg != null && leg.getDuration() > 0) {
            fullDefense += leg.getDefense();
        }
        //если эффекты в бою не равны нулю, применяем их
        if(unitFightEffect != null) {
            for(UnitFightEffect effect : unitFightEffect.values()) {
                fullDefense += effect.getEffectDefense();
            }
        }
        return fullDefense;
    }

    //скорость регенерации
    @JsonIgnore
    public int getRegeneration() {
        return 1 + unitSkill.getRegeneration() / 10;
    }

    //скорость восстановления маны
    @JsonIgnore
    public int getMeditation() {
        return 1 + unitSkill.getMeditation() / 10;
    }

    //шанс блока
    @JsonIgnore
    public int getChanceBlock() {
        return unitSkill.getBlock() / 10;
    }

    //шанс уклонения
    @JsonIgnore
    public int getChanceEvade() {
        return evade + unitSkill.getEvade() / 10;
    }
}
