package org.bot0ff.util;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.Random;

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

    //рандом от 0 до 10000
    public Long getRandomId() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        return (long) randomGenerator.nextInt(0, 10000);
    }

    //рандом 1 или 2
    public int getRandom1or2() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        return randomGenerator.nextInt(1, 2);
    }

    //рандом +-30% от числа
    public int getRNum30(int num) {
        Random random = new Random();
        int min = (int) (num * 0.70);
        int max = (int) (num * 1.30);
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }
}
