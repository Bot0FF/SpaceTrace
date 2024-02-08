package org.bot0ff.entity.unit;

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

    private int effectPhysDamage;
    private int durationEffectPhysDamage;

    private int effectMagDamage;
    private int durationEffectMagDamage;

    private int effectPhysDefense;
    private int durationEffectPhysDefense;

    private int effectMagDefense;
    private int durationEffectMagDefense;
}
