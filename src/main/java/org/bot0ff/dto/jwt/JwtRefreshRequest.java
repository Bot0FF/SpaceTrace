package org.bot0ff.dto.jwt;

import lombok.Data;

@Data
public class JwtRefreshRequest {
    String refreshToken;
}
