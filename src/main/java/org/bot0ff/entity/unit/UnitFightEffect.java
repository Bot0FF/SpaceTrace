package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitFightEffect {
    private int effectHp;
    private int durationEffectHp;

    private int effectMana;
    private int durationEffectMana;

    private int effectDamage;
    private int durationEffectDamage;

    private int effectDefense;
    private int durationEffectDefense;
}
