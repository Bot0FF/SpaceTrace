package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private String applyType;
    private int hp;
    private int mana;
    private int physDamage;
    private int magDamage;
    private double magDamageModifier;
    private int physDefense;
    private int magDefense;
    private int distance;
    private int duration;

    public UnitArmor() {
        this.id = 0L;
        this.name = "";
        this.applyType = "";
        this.hp = 0;
        this.mana = 0;
        this.physDamage = 0;
        this.magDamage = 0;
        this.magDamageModifier = 0;
        this.physDefense = 0;
        this.magDefense = 0;
        this.distance = 0;
        this.duration = 0;
    }
}
