package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.ApplyType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private String applyType;
    private double hp;
    private double mana;
    private double physDamage;
    private double magDamage;
    private double physDefense;
    private double magDefense;
    private int distance;
    private int duration;
}
