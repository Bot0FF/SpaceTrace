package org.bot0ff.world;

public class SunLocations extends Location {

    public SunLocations(int x, int y) {
        setX(x);
        setY(y);
        setName("Солнечная система");
        setSectorType(SectorType.SUN);
        setUpdateTime(300);
    }
}
