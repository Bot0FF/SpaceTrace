package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitFightEffect {
    //эффект
    private int E_Hp;
    //длительность эффекта
    private int DE_Hp;

    private int E_Mana;
    private int DE_Mana;

    private int E_PhysEff;
    private int DE_PhysEff;

    private double E_MagEff;
    private int DE_MagEff;

    private int E_PhysDef;
    private int DE_PhysDef;

    private int E_MagDef;
    private int DE_MagDef;

    private int E_Str;
    private int DE_Str;

    private int E_Intel;
    private int DE_Intel;

    private int E_Dext;
    private int DE_Dext;

    private int E_Endur;
    private int DE_Endur;

    private int E_Luck;
    private int DE_Luck;

    private int E_Init;
    private int DE_Init;

    private double E_Block;
    private int DE_Block;

    private double E_Evade;
    private int DE_Evade;

    public UnitFightEffect() {
        this.E_Hp = 0;
        this.DE_Hp = 0;
        this.E_Mana = 0;
        this.DE_Mana = 0;
        this.E_PhysEff = 0;
        this.DE_PhysEff = 0;
        this.E_MagEff = 0;
        this.DE_MagEff = 0;
        this.E_PhysDef = 0;
        this.DE_PhysDef = 0;
        this.E_MagDef = 0;
        this.DE_MagDef = 0;
        this.E_Str = 0;
        this.DE_Str = 0;
        this.E_Intel = 0;
        this.DE_Intel = 0;
        this.E_Dext = 0;
        this.DE_Dext = 0;
        this.E_Endur = 0;
        this.DE_Endur = 0;
        this.E_Luck = 0;
        this.DE_Luck = 0;
        this.E_Init = 0;
        this.DE_Init = 0;
        this.E_Block = 0;
        this.DE_Block = 0;
        this.E_Evade = 0;
        this.DE_Evade = 0;
    }
}
