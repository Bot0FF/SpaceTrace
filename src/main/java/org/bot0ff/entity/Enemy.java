package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.LocationType;
import org.bot0ff.entity.enums.Status;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Table(name = "enemy")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Enemy {
    @Id
    private Long id;

    @Column(name = "x")
    @JsonIgnore
    private int x;

    @Column(name = "y")
    @JsonIgnore
    private int y;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "locationType")
    private LocationType locationType;

    @ManyToOne()
    @JoinColumn(name = "location")
    @JsonIgnore
    private Location location;

    @ManyToOne()
    @JoinColumn(name = "fight")
    @JsonIgnore
    private Fight fight;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private Status status;

    @Column(name = "hp")
    private int hp;

    @Column(name = "damage")
    private int damage;

    @Column(name = "endRound")
    private boolean endRound;

    @Column(name = "roundDamage")
    private int roundDamage;

    @Column(name = "attackToId")
    private Long attackToId;

    @JsonIgnore
    public Long getLocationId() {
        return Long.parseLong("" + this.getX() + this.getY());
    }
}
