package org.bot0ff.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "sector")
public class Sector {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sectorType")
    @Enumerated(value = EnumType.STRING)
    private SectorType sectorType;

//    @Column(name = "enemy")
//    @ElementCollection(targetClass=String.class)
//    private List<String> enemy;
//
//    @Column(name = "resource")
//    @ElementCollection(targetClass=String.class)
//    private List<String> resource;
}
