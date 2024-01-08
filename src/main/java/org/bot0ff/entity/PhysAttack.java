package org.bot0ff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhysAttack {
    private Long id;
    private String name;
    private int damage;
}
