package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private String applyType;
    private int hp;
    private int mana;
    private int physDamage;
    private double magDamageModifier;
    private int physDefense;
    private int magDefense;
    private int distance;
    private int duration;
}
