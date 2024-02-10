package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitEffect {
    private Long id;

    private int effectHp;
    private int durationEffectHp;

    private int effectMana;
    private int durationEffectMana;

    private int effectPhysDamage;
    private int durationEffectPhysDamage;

    private double effectMagDamageModifier;
    private int durationEffectMagDamage;

    private int effectPhysDefense;
    private int durationEffectPhysDefense;

    private int effectMagDefense;
    private int durationEffectMagDefense;

    public UnitEffect() {
        this.id = 0L;
        this.effectHp = 0;
        this.durationEffectHp = 0;
        this.effectMana = 0;
        this.durationEffectMana = 0;
        this.effectPhysDamage = 0;
        this.durationEffectPhysDamage = 0;
        this.effectMagDamageModifier = 0;
        this.durationEffectMagDamage = 0;
        this.effectPhysDefense = 0;
        this.durationEffectPhysDefense = 0;
        this.effectMagDefense = 0;
        this.durationEffectMagDefense = 0;
    }
}
