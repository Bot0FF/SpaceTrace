package org.bot0ff.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRegisterResponse {
    private String token;
}