package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.util.UnitJsonConverter;

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
    @Column(name = "x")
    @JsonIgnore
    private int x;

    @Column(name = "y")
    @JsonIgnore
    private int y;

    @ManyToOne()
    @JoinColumn(name = "location")
    @JsonIgnore
    private Location location;

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

    @Column(name = "ability")
    @JsonIgnore
    private List<Long> ability;

    //сражение
    @ManyToOne()
    @JoinColumn(name = "fight")
    @JsonIgnore
    private Fight fight;

    @Convert(converter = UnitJsonConverter.class)
    @Column(name = "unitJson")
    @JsonIgnore
    private UnitJson unitJson;

    @Column(name = "teamNumber")
    @JsonIgnore
    private Long teamNumber;

    @Column(name = "abilityId")
    @JsonIgnore
    private Long abilityId;

    @Column(name = "targetId")
    @JsonIgnore
    private Long targetId;

    @JsonIgnore
    public Long getLocationId() {
        return Long.parseLong("" + this.getX() + this.getY());
    }
}
