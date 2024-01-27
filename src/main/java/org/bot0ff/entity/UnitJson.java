package org.bot0ff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitJson {
    private Long id;

    private int startHp;
    private int currentHp;
    private int durationEffectHp;

    private int startMana;
    private int currentMana;
    private int durationEffectMana;

    private int startDamage;
    private int currentDamage;
    private int durationEffectDamage;

    private int startDefense;
    private int currentDefense;
    private int durationEffectDefense;


}
