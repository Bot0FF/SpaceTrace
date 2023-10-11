package org.bot0ff.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveResponse {
    private String username;
    private int posX;
    private int posY;
}
