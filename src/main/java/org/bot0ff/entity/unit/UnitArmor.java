package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private String skillType;
    private int hp;
    private int mana;
    private int physDamage;
    private int magImpact;
    private double magDamageModifier;
    private int physDefense;
    private int magDefense;
    private int pointAction;
    private int distance;
    private int duration;

    public UnitArmor() {
        this.id = 0L;
        this.name = "";
        this.skillType = "";
        this.hp = 0;
        this.mana = 0;
        this.physDamage = 0;
        this.magImpact = 0;
        this.magDamageModifier = 0;
        this.physDefense = 0;
        this.magDefense = 0;
        this.pointAction = 0;
        this.distance = 0;
        this.duration = 0;
    }
}
