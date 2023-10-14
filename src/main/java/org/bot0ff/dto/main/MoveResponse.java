package org.bot0ff.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveResponse {
    private String imageMap;
    private String username;
    private String sector;
    private int posX;
    private int posY;
}
