package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    //тип экипировки
    @Enumerated(value = EnumType.STRING)
    @Column(name = "subjectType")
    private SubjectType subjectType;

    @Column(name = "name")
    private String name;

    @Column(name = "hp")
    private int hp;

    @Column(name = "damage")
    private int damage;

    @Column(name = "defense")
    private int defense;

    @Column(name = "mana")
    private int mana;

    //прочность экипировки
    @Column(name = "duration")
    private int duration;

    @Column(name = "description")
    private String description;

    @Column(name = "isUse")
    private boolean isUse;
}
