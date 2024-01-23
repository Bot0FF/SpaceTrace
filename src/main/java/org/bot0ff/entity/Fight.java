package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Table(name = "fight")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Fight {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "fight", fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private List<Unit> units;

    @Column(name = "countRound")
    private int countRound;

    @Column(name = "resultRound")
    private String resultRound;

    @Column(name = "fightEnd")
    @JsonIgnore
    private boolean fightEnd;

    @Transient
    private int timeToEndRound;
}
