package org.bot0ff.model;

import lombok.Data;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;

import java.util.List;

/** Класс ответа, с информацией о профиле игрока */

@Data
public class ProfileResponse {
    private Unit player;
    private List<Thing> things;
    private String info;
    private int status;

    public ProfileResponse(Unit player, List<Thing> things, String info) {
        this.player = player;
        this.things = things;
        this.info = info;
        this.status = 1;
    }
}
