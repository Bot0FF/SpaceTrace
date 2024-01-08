package org.bot0ff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerFight {
    private Long id;
    private int hp;
    private int damage;
}
