package org.bot0ff.world;

import lombok.Data;
import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.Player;
import org.bot0ff.util.Constants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
public abstract class Location {
    private int x;
    private int y;
    private String name;
    private SectorType sectorType;
    private int maxCountResource;
    private int updateTime;

    private Set<Player> players;
    private Set<Enemy> enemies;

    public Location() {
        players = Collections.synchronizedSet(new HashSet<>());
        enemies = Collections.synchronizedSet(new HashSet<>());
    }

    public synchronized void setPlayer(Player player) {
        players.add(player);
    }

    public synchronized void removePlayer(Player player) {
        players.removeIf(pl -> pl.getId().equals(player.getId()));
    }

    public synchronized void setEnemy(Enemy enemy) {
        if(enemies.size() < Constants.MAX_COUNT_ENEMY_ON_LOCATION) {
            enemies.add(enemy);
        }
    }
}
