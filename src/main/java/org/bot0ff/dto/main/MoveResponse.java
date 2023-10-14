package org.bot0ff.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class MoveResponse {
    private String imageMap;
    private String username;
    private int posX;
    private int posY;
}
