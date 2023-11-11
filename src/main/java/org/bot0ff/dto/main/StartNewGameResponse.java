package org.bot0ff.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartNewGameResponse {
    private String name;
    private String sector;
    private int posX;
    private int posY;
}
