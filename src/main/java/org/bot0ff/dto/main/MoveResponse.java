package org.bot0ff.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveResponse {
    private String username;
    private String locationType;
    private int posX;
    private int posY;
}
