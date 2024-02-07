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

    @Column(name = "hp")
    private int hp;

    @Column(name = "damage")
    private int damage;

    @Column(name = "defense")
    private int defense;

    @Column(name = "mana")
    private int mana;

    @Column(name = "distance")
    private int distance;

    @Column(name = "movePoint")
    private Long movePoint;

    @Column(name = "duration")
    private int duration;

    @Column(name = "description")
    private String description;
}
