package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.SkillType;
import org.bot0ff.entity.enums.ObjectType;

/** базовый тип, на основе которого создаются предметы */
@Data
@Table(name = "objects")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Objects {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "objectType")
    private ObjectType objectType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "skillType")
    private SkillType skillType;

    /** воздействия на характеристики для предметов */
    //физический урон
    @Column(name = "physDamage")
    private int physDamage;

    //модификатор магического воздействия
    @Column(name = "magModifier")
    private double magModifier;

    //воздействие на hp
    @Column(name = "hp")
    private int hp;

    //воздействие на mana
    @Column(name = "mana")
    private int mana;

    //воздействие на физическую защиту
    @Column(name = "physDefense")
    private int physDefense;

    //воздействие на магическую защиту
    @Column(name = "magDefense")
    private int magDefense;

    //воздействие на силу
    @Column(name = "strength")
    private int strength;

    //воздействие на интеллект
    @Column(name = "intelligence")
    private int intelligence;

    //воздействие на ловкость
    @Column(name = "dexterity")
    private int dexterity;

    //воздействие на выносливость
    @Column(name = "endurance")
    private int endurance;

    //воздействие на удачу
    @Column(name = "luck")
    private int luck;

    //дистанция применения
    @Column(name = "distance")
    private int distance;

    //состояние предмета
    @Column(name = "condition")
    private int condition;

    /** количество прибавляемого опыта к навыку для книг */
    @Column(name = "oneHand")
    private int oneHand;

    @Column(name = "twoHand")
    private int twoHand;

    @Column(name = "bow")
    private int bow;

    @Column(name = "fire")
    private int fire;

    @Column(name = "water")
    private int water;

    @Column(name = "land")
    private int land;

    @Column(name = "air")
    private int air;

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

    /** прочие характеристики */
    //цена объекта
    @Column(name = "price")
    private int price;

    //описание
    @Column(name = "description")
    private String description;
}
