package org.bot0ff.util;

import lombok.Builder;
import lombok.Data;
import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.Library;
import org.bot0ff.entity.Player;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ResponseBuilder {
    private Player player;
    private List<Enemy> enemies;
    private List<Player> players;
    private String content;
    private HttpStatus httpStatus;
}
