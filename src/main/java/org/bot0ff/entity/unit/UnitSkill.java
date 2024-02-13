package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public UnitSkill() {
        this.oneHand = 1;
        this.twoHand = 1;
        this.bow = 1;
        this.fire = 1;
        this.water = 1;
        this.land = 1;
        this.air = 1;
        this.vitality = 1;
        this.spirituality = 1;
        this.regeneration = 1;
        this.meditation = 1;
        this.block = 1;
        this.evade = 1;
    }
}
