package org.bot0ff.world;

import org.bot0ff.util.Constants;
import org.springframework.stereotype.Service;

@Service
public class WorldGenerator {

    public WorldGenerator() {
        initSunSystem();
    }

    private void initSunSystem() {
        for(int y = 1; y <= Constants.SUN_MAX_POS_Y; y++) {
            for(int x = 1; x <= Constants.SUN_MAX_POS_X; x++) {
                World.SUN[y - 1][x - 1] = new SunLocations(x, y);
            }
        }
    }
}
