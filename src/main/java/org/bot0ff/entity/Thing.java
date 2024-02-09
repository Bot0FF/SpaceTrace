package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.SubjectType;

@Data
@Table(name = "thing")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Thing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ownerId")
    private Long ownerId;

    @Column(name = "name")
    private String name;

    //тип экипировки
    @Enumerated(value = EnumType.STRING)
    @Column(name = "subjectType")
    private SubjectType subjectType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "applyType")
    @JsonIgnore
    private ApplyType applyType;

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
    @Column(name = "actionPoint")
    private Long actionPoint;

    @Column(name = "duration")
    private int duration;

    /** описание */
    @Column(name = "description")
    private String description;

    @Column(name = "isUse")
    private boolean isUse;
}
