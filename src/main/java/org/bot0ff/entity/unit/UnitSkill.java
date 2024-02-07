package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitSkill {
    private int oneHand;
    private int twoHand;
    private int bow;
    private int stick;

    private int fire;
    private int water;
    private int land;
    private int air;

    //регенерация
    private int regeneration;
    //восстановление маны
    private int meditation;
    //блокирование
    private int block;
    //уклонение
    private int evade;
}
