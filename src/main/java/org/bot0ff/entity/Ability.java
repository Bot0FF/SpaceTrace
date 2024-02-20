package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.RangeType;
import org.bot0ff.entity.enums.SkillType;

@Data
@Table(name = "ability")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ability {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "skillType")
    private SkillType skillType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "applyType")
    private ApplyType applyType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "rangeType")
    private RangeType rangeType;

    /** урон */
    //магический урон
    @Column(name = "magDamage")
    private int magDamage;

    /** воздействия на характеристики */
    //воздействие на физический урон
    @Column(name = "physEffect")
    private int physEffect;

    //воздействие на магический модификатор
    @Column(name = "magEffect")
    private double magEffect;

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

    //воздействие на инициативу
    @Column(name = "initiative")
    private int initiative;

    //воздействие на шанс блокирование
    @Column(name = "block")
    private double block;

    //воздействие шанс уворота
    @Column(name = "evade")
    private double evade;

    /** прочие характеристики */
    //дистанция применения
    @Column(name = "distance")
    private int distance;

    //требуемое количество очков действия
    @Column(name = "pointAction")
    private int pointAction;

    //длительность действия
    @Column(name = "duration")
    private int duration;

    //количество расходуемой маны
    @Column(name = "manaCost")
    private int manaCost;

    //стоимость умения
    @Column(name = "price")
    private int price;

    //описание
    @Column(name = "description")
    private String description;

    //является ли умение избранным
    @Transient
    private boolean isCurrentAbility;
}
