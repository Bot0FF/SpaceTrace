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

    @JsonIgnore
    @OneToMany(mappedBy = "fight", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Unit> units;

    @Column(name = "countRound")
    private int countRound;

    @JsonIgnore
    @Column(name = "resultRound", length = 1024)
    private List<String> resultRound;

    @JsonIgnore
    @Column(name = "fightEnd")
    private boolean fightEnd;

    @Column(name = "unitsWin")
    private List<Long> unitsWin;

    @Column(name = "unitsLoss")
    private List<Long> unitsLoss;
}
