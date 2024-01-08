package org.bot0ff.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fight {

    private List<Long> players;

    private List<Long> enemies;

    private int countRound;

    private int timeToEndRound;

    private boolean roundEnd;
}
