package org.bot0ff.model;

import lombok.Data;
import org.bot0ff.dto.UnitDto;
import org.bot0ff.entity.Location;

@Data
public class MainResponse {
    private UnitDto player;
    private Location location;
    private int ais;
    private int units;
    private int things;
    private String info;
    private int status;

    public MainResponse(UnitDto player, Location location, String info) {
        this.player = player;
        this.location = location;
        this.ais = location.getAis().size();
        location.getUnits().removeIf(u -> u.equals(player.getId()));
        this.units = location.getUnits().size();
        this.things = location.getThings().size();
        this.info = info;
        this.status = 1;
    }
}