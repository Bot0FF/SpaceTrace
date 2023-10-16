package org.bot0ff.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sector")
    private String sector;
    @Column(name = "posX")
    private int posX;
    @Column(name = "posY")
    private int posY;
}
