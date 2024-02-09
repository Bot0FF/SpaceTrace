package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.unit.UnitEffect;
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
    @JsonIgnore
    private Long id;

    @Column(name = "name")
    @JsonIgnore
    private String name;

    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private SubjectType subjectType;

    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private Status status;

    @Column(name = "actionEnd")
    @JsonIgnore
    private boolean actionEnd;

    /** локация */
    @Column(name = "locationId")
    @JsonIgnore
    private Long locationId;

    /** основные характеристики */
    //здоровье
    @Column(name = "hp")
    @JsonIgnore
    private int hp;

    @Column(name = "mana")
    @JsonIgnore
    private int mana;

    //очки действия
    @Column(name = "pointAction")
    @JsonIgnore
    private int pointAction;

    //максимальные очки действия
    @Column(name = "maxPointAction")
    @JsonIgnore
    private int maxPointAction;

    /** аттрибуты */
    //сила
    @Column(name = "strength")
    @JsonIgnore
    private int strength;

    //интеллект
    @Column(name = "intelligence")
    @JsonIgnore
    private int intelligence;

    //ловкость
    @Column(name = "dexterity")
    @JsonIgnore
    private int dexterity;

    //выносливость
    @Column(name = "endurance")
    @JsonIgnore
    private int endurance;

    //удача
    @Column(name = "luck")
    @JsonIgnore
    private int luck;

    //свободные очки для распределения
    @Column(name = "bonusPoint")
    @JsonIgnore
    private int bonusPoint;

    /** навыки */
    @Convert(converter = UnitJsonSubjectToSkillConverter.class)
    @Column(name = "unitSkill")
    @JsonIgnore
    private UnitSkill unitSkill;

    /** список умений */
    //активные умения
    @Column(name = "currentAbility")
    @JsonIgnore
    private List<Long> currentAbility;

    //все умения
    @Column(name = "allAbility")
    @JsonIgnore
    private List<Long> allAbility;

    /** экипировка */
    //оружие
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "weapon", length = 1024)
    @JsonIgnore
    private UnitArmor weapon;

    //голова
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "head", length = 1024)
    @JsonIgnore
    private UnitArmor head;

    //руки
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "hand", length = 1024)
    @JsonIgnore
    private UnitArmor hand;

    //тело
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "body", length = 1024)
    @JsonIgnore
    private UnitArmor body;

    //ноги
    @Convert(converter = UnitJsonSubjectToArmorConverter.class)
    @Column(name = "leg", length = 1024)
    @JsonIgnore
    private UnitArmor leg;

    /** сражение */
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "fight")
    @JsonIgnore
    private Fight fight;

    @Column(name = "unitFightPosition")
    @JsonIgnore
    private Long unitFightPosition;

    @Convert(converter = UnitJsonSubjectToEffectConverter.class)
    @Column(name = "unitFightEffect", length = 1024)
    @JsonIgnore
    private List<UnitEffect> unitFightEffect;

    @Column(name = "teamNumber")
    @JsonIgnore
    private Long teamNumber;

    @Column(name = "abilityId")
    @JsonIgnore
    private Long abilityId;

    @Column(name = "targetId")
    @JsonIgnore
    private Long targetId;
}
