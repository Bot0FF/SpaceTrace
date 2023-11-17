package org.bot0ff.world;

import org.bot0ff.util.Constants;
import org.springframework.stereotype.Service;

@Service
public class WorldGenerator {
    public static Location[][] WORLD = new Location[Constants.MAX_POS_Y][Constants.MAX_POS_Y];

    public WorldGenerator() {
        initPlainLocation();
    }

    //создание локации "Равнина"
    private void initPlainLocation() {
        for(int y = 0; y < Constants.MAX_POS_Y; y++) {
            for(int x = 0; x < Constants.MAX_POS_X; x++) {
                WORLD[y][x] = new PlainLocations(x, y);
            }
        }
    }

    //возвращает локацию player
    public Location getLocation(int posX, int posY) {
        return WORLD[posY][posX];
    }
}
