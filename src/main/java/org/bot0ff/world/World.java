package org.bot0ff.world;

<<<<<<< HEAD
import org.bot0ff.entity.Enemy;
=======
>>>>>>> origin/main
import org.bot0ff.util.Constants;
import org.springframework.stereotype.Service;

@Service
public class World {
    public static Location[][] SUN = new Location[Constants.SUN_MAX_POS_Y][Constants.SUN_MAX_POS_X];

    public static Location getLocation(String sector, int posX, int posY) {
        Location location = null;
        switch (sector) {
<<<<<<< HEAD
            case "SUN" -> {
                location = SUN[posY - 1][posX - 1];
                //удалить
                location.getEnemies().add(new Enemy(1L, SectorType.SUN, "Enemy", 1, 1));
            }
=======
            case "SUN" -> {location = SUN[posY - 1][posX - 1];}
>>>>>>> origin/main
        }
        return location;
    }
}
