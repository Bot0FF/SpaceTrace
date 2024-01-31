package org.bot0ff.dto.unit;

import lombok.Data;

@Data
public class UnitArmor {
    private Long id;
    private String name;
    private int hp;
    private int mana;
    private int damage;
    private int defense;
}
