package org.bot0ff.dto.jwt;

import lombok.Data;

@Data
public class JwtRegisterRequest {
    private String username;
    private String password;
}
