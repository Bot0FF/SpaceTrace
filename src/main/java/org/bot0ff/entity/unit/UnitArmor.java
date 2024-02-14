package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private String objectType;
    private String skillType;
    private int physDamage;
    private double magModifier;
    private int hp;
    private int mana;
    private int physDefense;
    private int magDefense;
    private int strength;
    private int intelligence;
    private int dexterity;
    private int endurance;
    private int luck;
    private int distance;
    private int condition;

    public UnitArmor() {
        this.id = 0L;
        this.name = "";
        this.objectType = "";
        this.skillType = "";
        this.physDamage = 0;
        this.magModifier = 0;
        this.hp = 0;
        this.mana = 0;
        this.physDefense = 0;
        this.magDefense = 0;
        this.strength = 0;
        this.intelligence = 0;
        this.dexterity = 0;
        this.endurance = 0;
        this.luck = 0;
        this.distance = 0;
        this.condition = 0;
    }
}
