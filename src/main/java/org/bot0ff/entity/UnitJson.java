package org.bot0ff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.HitType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitJson {
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
