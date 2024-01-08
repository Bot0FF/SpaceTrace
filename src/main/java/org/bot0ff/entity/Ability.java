package org.bot0ff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "damage")
    private int damage;
}
