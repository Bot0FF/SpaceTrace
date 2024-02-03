package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.dto.unit.UnitEffect;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.util.converter.UnitJsonSubjectToEffectConverter;

import java.util.List;

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
    @JsonIgnore
    private UnitType unitType;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "actionEnd")
    private boolean actionEnd;

    //локация
    @Column(name = "locationId")
    @JsonIgnore
    private Long locationId;

    //характеристики
    @Column(name = "hp")
    private int hp;

    @Column(name = "maxHp")
    private int maxHp;

    @Column(name = "mana")
    private int mana;

    @Column(name = "maxMana")
    private int maxMana;

    @Column(name = "damage")
    private int damage;

    @Column(name = "defense")
    private int defense;

//    //экипировка
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "weapon")
//    private Thing weapon;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "head")
//    private Thing head;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "hand")
//    private Thing hand;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "body")
//    private Thing body;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "leg")
//    private Thing leg;

    //умения
    @Column(name = "ability")
    @JsonIgnore
    private List<Long> ability;

    //сражение
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "fight")
    @JsonIgnore
    private Fight fight;

    @Convert(converter = UnitJsonSubjectToEffectConverter.class)
    @Column(name = "unitJson")
    @JsonIgnore
    private UnitEffect unitEffect;

    @Column(name = "teamNumber")
    private Long teamNumber;

    @Column(name = "abilityId")
    @JsonIgnore
    private Long abilityId;

    @Column(name = "targetId")
    @JsonIgnore
    private Long targetId;

//    public int getFullDamage() {
//        int fullDamage = damage;
//        if(weapon != null && weapon.getDuration() > 0) {
//            fullDamage += weapon.getDamage();
//        }
//        if(head != null && head.getDuration() > 0) {
//            fullDamage += head.getDamage();
//        }
//        if(hand != null && hand.getDuration() > 0) {
//            fullDamage += hand.getDamage();
//        }
//        if(body != null && body.getDuration() > 0) {
//            fullDamage += body.getDamage();
//        }
//        if(leg != null && leg.getDuration() > 0) {
//            fullDamage += leg.getDamage();
//        }
//        return fullDamage;
//    }
//
//    public int getFullHp() {
//        int fullHp = hp;
//        if(weapon != null && weapon.getDuration() > 0) {
//            fullHp += weapon.getHp();
//        }
//        if(head != null && head.getDuration() > 0) {
//            fullHp += head.getHp();
//        }
//        if(hand != null && hand.getDuration() > 0) {
//            fullHp += hand.getHp();
//        }
//        if(body != null && body.getDuration() > 0) {
//            fullHp += body.getHp();
//        }
//        if(leg != null && leg.getDuration() > 0) {
//            fullHp += leg.getHp();
//        }
//        return fullHp;
//    }
//
//    public int getFullMana() {
//        int fullMana = mana;
//        if(weapon != null && weapon.getDuration() > 0) {
//            fullMana += weapon.getMana();
//        }
//        if(head != null && head.getDuration() > 0) {
//            fullMana += head.getMana();
//        }
//        if(hand != null && hand.getDuration() > 0) {
//            fullMana += hand.getMana();
//        }
//        if(body != null && body.getDuration() > 0) {
//            fullMana += body.getMana();
//        }
//        if(leg != null && leg.getDuration() > 0) {
//            fullMana += leg.getMana();
//        }
//        return fullMana;
//    }
//
//    public int getFullDefense() {
//        int fullDefense = defense;
//        if(weapon != null && weapon.getDuration() > 0) {
//            fullDefense += weapon.getDefense();
//        }
//        if(head != null && head.getDuration() > 0) {
//            fullDefense += head.getDefense();
//        }
//        if(hand != null && hand.getDuration() > 0) {
//            fullDefense += hand.getDefense();
//        }
//        if(body != null && body.getDuration() > 0) {
//            fullDefense += body.getDefense();
//        }
//        if(leg != null && leg.getDuration() > 0) {
//            fullDefense += leg.getDefense();
//        }
//        return fullDefense;
//    }
}
