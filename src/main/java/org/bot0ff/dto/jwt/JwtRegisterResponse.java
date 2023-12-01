package org.bot0ff.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bot0ff.entity.Player;

@Data
@AllArgsConstructor
public class JwtRegisterResponse {
    private Player player;
    private String accessToken;
}
