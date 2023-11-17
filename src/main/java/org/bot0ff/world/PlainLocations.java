package org.bot0ff.world;

public class PlainLocations extends Location {

    public PlainLocations(int x, int y) {
        setX(x);
        setY(y);
        setName("Равнина");
        setLocationType(LocationType.PLAIN);
        setUpdateTime(300);
    }
}
