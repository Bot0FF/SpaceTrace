package org.bot0ff.util;

import lombok.Builder;
import lombok.Data;
import org.bot0ff.dto.UserDTO;
import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.User;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResponseBuilder {
    private UserDTO user;
    private List<Enemy> enemies;
    private List<User> players;
    private Map<String, String> content;
    private String status;
}
