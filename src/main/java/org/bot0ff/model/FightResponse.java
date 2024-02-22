package org.bot0ff.model;

import lombok.Data;
import org.bot0ff.entity.Ability;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;
import org.bot0ff.service.fight.FightService;

import java.util.ArrayList;
import java.util.List;

/** Класс ответа, с информацией об игроке, сражении */

@Data
public class FightResponse {
    private Unit player;
    private Fight fight;
    private List<Unit> teamOne;
    private List<Unit> teamTwo;
    private List<Ability> ability;
    private String resultRound;
    private int countRound;
    private Long endRoundTimer;
    private String info;
    private int status;

    public FightResponse(Unit player, Fight fight, List<Ability> ability, String info) {
        this.player = player;
        this.fight = fight;
        if(fight != null) {
            this.teamOne = new ArrayList<>(fight.getUnits().stream().filter(unit -> unit.getTeamNumber() == 1).toList());
            this.teamTwo = new ArrayList<>(fight.getUnits().stream().filter(unit -> unit.getTeamNumber() == 2).toList());
            this.resultRound = fight.getResultRound().get(fight.getResultRound().size() - 1);
            this.countRound = this.fight.getCountRound();
        }
        if(fight != null && FightService.FIGHT_MAP.get(fight.getId()) != null) {
            this.endRoundTimer = FightService.FIGHT_MAP.get(fight.getId()).getEndRoundTimer().toEpochMilli();
        }
        if(ability != null) {
            this.ability = ability;
        }
        this.info = info;
        this.status = 1;
    }
}