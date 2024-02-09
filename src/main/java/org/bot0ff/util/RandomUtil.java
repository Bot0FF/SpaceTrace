package org.bot0ff.util;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

@Service
public class RandomUtil {

    //случайное десятичное число от 0.00 до 100.00
    public double getDoubleChance() {
        BigDecimal bigDecimal = BigDecimal.valueOf(Math.random() * 100);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    //TODO добавить навык игрока
    public boolean getChanceCreateEnemy() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        int chance = randomGenerator.nextInt(0, 100);
        return chance >= Constants.CHANCE_CREATE_ENEMY;
    }

    //рандом 1 или 2
    public int getRandom1or2() {
        RandomDataGenerator randomGenerator = new RandomDataGenerator();
        return randomGenerator.nextInt(1, 2);
    }

    //рандом от до
    public int getRandomFromTo(int from, int to) {
        return new RandomDataGenerator().nextInt(from, to);
    }

    //рандом +-30% от числа
    public int getRNum30(int num) {
        int min = (int) Math.round(num * 0.70);
        int max = (int) Math.round(num * 1.30);
        return new RandomDataGenerator().nextInt(min, max);
    }
}
