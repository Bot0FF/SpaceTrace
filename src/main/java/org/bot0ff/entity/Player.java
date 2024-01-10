package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.Status;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    //общее
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "location")
    @JsonIgnore
    private Location location;

    @ManyToOne()
    @JoinColumn(name = "fight")
    @JsonIgnore
    private Fight fight;

    @Column(name = "x")
    @JsonIgnore
    private int x;

    @Column(name = "y")
    @JsonIgnore
    private int y;

    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private Status status;

    //характеристики
    @Column(name = "hp")
    private int hp;

    @Column(name = "mana")
    private int mana;

    @Column(name = "damage")
    private int damage;

    //сражения
    @Column(name = "roundActionEnd")
    private boolean roundActionEnd;

    @Column(name = "roundChangeAbility")
    @JsonIgnore
    private Long roundChangeAbility;

    @Column(name = "roundTargetType")
    @JsonIgnore
    private String roundTargetType;

    @Column(name = "roundTargetId")
    @JsonIgnore
    private Long roundTargetId;

    @JsonIgnore
    public Long getLocationId() {
        return Long.parseLong("" + this.getX() + this.getY());
    }
}
