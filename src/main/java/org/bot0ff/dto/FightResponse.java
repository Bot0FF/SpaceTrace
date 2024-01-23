package org.bot0ff.dto;

import lombok.Data;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class FightResponse {
    private Unit player;
    private Fight fight;
    private List<Unit> teamOne;
    private List<Unit> teamTwo;
    private String info;
    private int status;

    public FightResponse(Unit player, Fight fight, String info) {
        this.player = player;
        this.fight = fight;
        if(player.get_teamType() != null) {
            this.teamOne = new ArrayList<>(fight.getUnits().stream().filter(unit -> unit.get_teamType() == 1).toList());
            this.teamTwo = new ArrayList<>(fight.getUnits().stream().filter(unit -> unit.get_teamType() == 2).toList());
        }
        else {
            this.teamOne = new ArrayList<>();
            this.teamTwo = new ArrayList<>();
        }
        this.info = Objects.requireNonNullElse(info, "Идет сражение...");
        this.status = 1;
    }
}
