package org.bot0ff.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bot0ff.entity.Player;

import javax.servlet.http.Cookie;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private Player player;
    private String accessToken;
}