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

    private int fire;
    private int water;
    private int land;
    private int air;

    //живучесть
    private int vitality;
    //духовность
    private int spirituality;
    //регенерация
    private int regeneration;
    //медитация
    private int meditation;
    //блокирование
    private int block;
    //уклонение
    private int evade;
}
