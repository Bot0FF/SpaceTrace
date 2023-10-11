package org.bot0ff.dto.jwt;

import lombok.Data;

@Data
public class JwtAuthRequest {
    private String username;
    private String password;
}
