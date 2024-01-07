package org.bot0ff.dto.response;

import lombok.Builder;
import lombok.Data;
import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.Player;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class FightBuilder {
    private List<Player> onePlayerTeam;
    private List<Player> twoPlayerTeam;
    private List<Enemy> enemyTeam;
    private HttpStatus status;
}
