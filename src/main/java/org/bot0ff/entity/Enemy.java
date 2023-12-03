package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bot0ff.world.LocationType;

//будут загружаться из базы в зависимости от сектора
@Data
@Table(name = "enemy")
@Entity
@AllArgsConstructor
public class Enemy {
    @Id
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "sectorType")
    private LocationType locationType;

    @Column(name = "name")
    private String name;

    @Column(name = "hp")
    private int hp;

    @Column(name = "damage")
    private int damage;
}
