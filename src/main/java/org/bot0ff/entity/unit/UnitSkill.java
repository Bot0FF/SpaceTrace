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
        this.oneHand = 0;
        this.twoHand = 0;
        this.bow = 0;
        this.fire = 0;
        this.water = 0;
        this.land = 0;
        this.air = 0;
        this.vitality = 0;
        this.spirituality = 0;
        this.regeneration = 0;
        this.meditation = 0;
        this.block = 0;
        this.evade = 0;
    }
}
