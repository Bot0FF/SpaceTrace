package org.bot0ff.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.Cookie;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
    private Cookie cookie;
}