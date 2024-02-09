package org.bot0ff.model;

import lombok.Data;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Unit;

@Data
public class MainResponse {
    private Unit player;
    private Location location;
    private int ais;
    private int units;
    private int things;
    private String info;
    private int status;

    public MainResponse(Unit player, Location location, String info) {
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