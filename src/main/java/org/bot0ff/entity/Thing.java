package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "owner")
    private Long owner;

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

    //здесь - прочность экипировки
    @Column(name = "duration")
    private int duration;

    @Column(name = "description")
    private String description;
}
