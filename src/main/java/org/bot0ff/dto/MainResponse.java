package org.bot0ff.dto;

import lombok.Data;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Unit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class MainResponse {
    private Unit player;
    private Location location;
    private List<Unit> enemies;
    private List<Unit> players;
    private String info;
    private int status;

    public MainResponse(Unit player, Location location, String info) {
        this.player = player;
        this.location = location;
        this.enemies = new ArrayList<>(location.getUnits().stream().filter(unit -> unit.getName().startsWith("*")).toList());
        this.players = new ArrayList<>(location.getUnits().stream().filter(unit -> !unit.getName().startsWith("*")).toList());
        this.info = Objects.requireNonNullElseGet(info, () -> new SimpleDateFormat("dd-MM-yyyy HH:mm")
                .format(new Date()));
        this.status = 1;
    }
}
