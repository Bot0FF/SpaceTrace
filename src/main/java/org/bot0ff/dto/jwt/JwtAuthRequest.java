package org.bot0ff.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthRequest {
    //@Size(min = 3, max = 20, message = "Логин должен быть от 3 до 20 символов")
    private String username;
    //@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Пароль не должен быть меньше 8 символов")
    private String password;
}
