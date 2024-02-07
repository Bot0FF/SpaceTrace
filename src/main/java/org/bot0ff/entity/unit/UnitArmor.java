package org.bot0ff.entity.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.SubjectType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitArmor {
    private Long id;
    private String name;
    private SubjectType subjectType;
    private ApplyType applyType;
    private int hp;
    private int mana;
    private int damage;
    private int distance;
    private int defense;
    private int duration;
}
