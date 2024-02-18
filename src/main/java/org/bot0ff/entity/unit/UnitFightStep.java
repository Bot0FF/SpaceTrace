package org.bot0ff.entity.unit;

import lombok.Data;

@Data
public class UnitFightStep {
    private Long abilityId;
    private Long targetId;
    private Long hitPosition;
    private Long targetPosition;

    public UnitFightStep() {
        this.abilityId = 0L;
        this.targetId = 0L;
        this.hitPosition = 0L;
        this.targetPosition = 0L;
    }
}
