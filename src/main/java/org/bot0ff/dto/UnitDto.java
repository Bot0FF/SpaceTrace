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

@Data
@JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
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

    //конструктор для MainResponse
    public UnitDto(Long id,
                   String name,
                   SubjectType subjectType,
                   Status status,
                   boolean actionEnd,
                   int hp,
                   int maxHp,
                   int mana,
                   int maxMana,
                   int bonusPoint) {
        this.id = id;
        this.name = name;
        this.subjectType = subjectType;
        this.status = status;
        this.actionEnd = actionEnd;
        this.hp = hp;
        this.maxHp = maxHp;
        this.mana = mana;
        this.maxMana = maxMana;
        this.bonusPoint = bonusPoint;
    }

    //конструктор для FightResponse
    public UnitDto(Long id,
                   String name,
                   SubjectType subjectType,
                   Status status,
                   boolean actionEnd,
                   int hp,
                   int maxHp,
                   int mana,
                   int maxMana,
                   int physDamage,
                   double magModifier,
                   int physDefense,
                   int magDefense,
                   int initiative,
                   double chanceBlock,
                   double chanceEvade,
                   int pointAction,
                   int maxPointAction,
                   UnitSkill unitSkill,
                   List<Long> currentAbility,
                   List<Long> allAbility,
                   UnitArmor weapon,
                   UnitArmor head,
                   UnitArmor hand,
                   UnitArmor body,
                   UnitArmor leg,
                   Fight fight,
                   Long unitFightPosition,
                   List<UnitEffect> unitFightEffect,
                   Long teamNumber,
                   Long abilityId,
                   Long targetId) {
        this.id = id;
        this.name = name;
        this.subjectType = subjectType;
        this.status = status;
        this.actionEnd = actionEnd;
        this.hp = hp;
        this.maxHp = maxHp;
        this.mana = mana;
        this.maxMana = maxMana;
        this.physDamage = physDamage;
        this.magModifier = magModifier;
        this.physDefense = physDefense;
        this.magDefense = magDefense;
        this.initiative = initiative;
        this.chanceBlock = chanceBlock;
        this.chanceEvade = chanceEvade;
        this.pointAction = pointAction;
        this.maxPointAction = maxPointAction;
        this.unitSkill = unitSkill;
        this.currentAbility = currentAbility;
        this.allAbility = allAbility;
        this.weapon = weapon;
        this.head = head;
        this.hand = hand;
        this.body = body;
        this.leg = leg;
        this.fight = fight;
        this.unitFightPosition = unitFightPosition;
        this.unitFightEffect = unitFightEffect;
        this.teamNumber = teamNumber;
        this.abilityId = abilityId;
        this.targetId = targetId;
    }
}
