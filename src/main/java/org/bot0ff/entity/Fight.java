package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Table(name = "fight")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Fight {

    @Id
    private Long id;

    @OneToMany(mappedBy = "fight", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Player> players;

    @OneToMany(mappedBy = "fight", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Enemy> enemies;

    @Column(name = "countRound")
    private int countRound;

    @Column(name = "fightEnd")
    private boolean fightEnd;

    @Transient
    private int timeToEndRound;
}
