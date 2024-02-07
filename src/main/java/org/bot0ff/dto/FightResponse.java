package org.bot0ff.dto;

import lombok.Data;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;

import java.util.ArrayList;
import java.util.List;

@Data
public class FightResponse {
    private Unit player;
    private Fight fight;
    private List<Unit> teamOne;
    private List<Unit> teamTwo;
    private String resultRound;
    private int countRound;
    private Long endRoundTimer;
    private String info;
    private int status;

    public FightResponse(Unit player, Fight fight, String info) {
        this.player = player;
        this.fight = fight;
        this.teamOne = new ArrayList<>();
        this.teamTwo = new ArrayList<>();
        if(!fight.getUnits().isEmpty()) {
            this.teamOne = new ArrayList<>(fight.getUnits().stream().filter(unit -> unit.getTeamNumber() == 1).toList());
            this.teamTwo = new ArrayList<>(fight.getUnits().stream().filter(unit -> unit.getTeamNumber() == 2).toList());
        }
        if(this.fight.getResultRound().isEmpty()) {
            this.resultRound = "";
        }
        else {
            this.resultRound = this.fight.getResultRound().get(this.fight.getResultRound().size() - 1);
        }
        this.countRound = this.fight.getCountRound();
        this.endRoundTimer = this.fight.getEndRoundTimer();
        this.info = info;
        this.status = 1;
    }
}