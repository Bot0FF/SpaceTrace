package org.bot0ff.util;

import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomUtil {

    //TODO добавить навык игрока
    public boolean getChanceCreateEnemy() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        int chance = randomGenerator.nextInt(0, 100);
        return chance >= Constants.CHANCE_CREATE_ENEMY;
    }

    //TODO добавить привязку enemy к локации
    public Long getRandomEnemyId() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        return (long) randomGenerator.nextInt(0, 10);
    }

    public Long getRandomId() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        return (long) randomGenerator.nextInt(0, 10000);
    }
}
