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

    public UnitFightStep() {
        this.abilityId = 0L;
        this.targetId = 0L;
    }
}
