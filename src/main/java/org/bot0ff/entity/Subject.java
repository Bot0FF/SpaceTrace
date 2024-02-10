package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.HitType;
import org.bot0ff.entity.enums.RangeType;
import org.bot0ff.entity.enums.SubjectType;

@Data
@Table(name = "subject")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Subject {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "subjectType")
    private SubjectType subjectType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "applyType")
    private ApplyType applyType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "hitType")
    private HitType hitType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "rangeType")
    private RangeType rangeType;

    /** для экипировки, боевых умений */
    @Column(name = "hp")
    private int hp;

    @Column(name = "mana")
    private int mana;

    @Column(name = "physDamage")
    private int physDamage;

    //для магического умения
    @Column(name = "magDamage")
    private int magDamage;

    //для магического оружия
    @Column(name = "magDamageModifier")
    private double magDamageModifier;

    @Column(name = "physDefense")
    private int physDefense;

    @Column(name = "magDefense")
    private int magDefense;

    /** для книг */
    @Column(name = "vitality")
    private int vitality;

    @Column(name = "spirituality")
    private int spirituality;

    @Column(name = "regeneration")
    private int regeneration;

    @Column(name = "meditation")
    private int meditation;

    @Column(name = "evade")
    private int evade;

    @Column(name = "block")
    private int block;

    /** для оружия, боевых умений */
    //дистанция применения
    @Column(name = "distance")
    private int distance;

    //требуемое количество очков действия
    @Column(name = "pointAction")
    private int pointAction;

    @Column(name = "duration")
    private int duration;

    /** описание */
    @Column(name = "description")
    private String description;
}
