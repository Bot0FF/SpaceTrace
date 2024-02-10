package org.bot0ff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.SubjectType;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.unit.UnitEffect;
import org.bot0ff.entity.unit.UnitSkill;

import java.util.List;

/** DTO для всех возможных характеристик Unit */

@Data
@JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
@AllArgsConstructor
public class UnitDto {
    private Long id;
    private String name;
    private SubjectType subjectType;
    private Status status;
    private boolean actionEnd;
    private Long locationId;
    private int hp;
    private int maxHp;
    private int mana;
    private int maxMana;
    private int physDamage;
    private double magModifier;
    private int physDefense;
    private int magDefense;
    private int initiative;
    private int regeneration;
    private int meditation;
    private double chanceBlock;
    private double chanceEvade;
    private int pointAction;
    private int maxPointAction;
    private int strength;
    private int intelligence;
    private int dexterity;
    private int endurance;
    private int luck;
    private int bonusPoint;
    private UnitSkill unitSkill;
    private List<Long> currentAbility;
    private List<Long> allAbility;
    private UnitArmor weapon;
    private UnitArmor head;
    private UnitArmor hand;
    private UnitArmor body;
    private UnitArmor leg;
    private Fight fight;
    private Long unitFightPosition;
    private List<UnitEffect> unitFightEffect;
    private Long teamNumber;
    private Long abilityId;
    private Long targetId;
}
