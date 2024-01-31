package org.bot0ff.dto.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitEffect {
    private int effectHp;
    private int durationEffectHp;

    private int effectMana;
    private int durationEffectMana;

    private int startDamage;
    private int effectDamage;
    private int durationEffectDamage;

    private int startDefense;
    private int effectDefense;
    private int durationEffectDefense;
}
