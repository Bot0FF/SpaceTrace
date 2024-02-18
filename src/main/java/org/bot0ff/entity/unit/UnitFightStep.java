package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitFightStep {
    //id выбранного умения
    private Long abilityId;
    //id выбранной цели
    private Long targetId;
    //позиция unit во время атаки
    private Long hitPosition;
    //позиция target во время атаки unit
    private Long targetPosition;

    public UnitFightStep() {
        this.abilityId = 0L;
        this.targetId = 0L;
        this.hitPosition = 0L;
        this.targetPosition = 0L;
    }
}
