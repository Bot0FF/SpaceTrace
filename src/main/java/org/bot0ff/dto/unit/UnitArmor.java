package org.bot0ff.dto.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private int hp;
    private int mana;
    private int damage;
    private int defense;
    private int duration;

}
