package org.bot0ff.dto.response;

import lombok.Builder;
import lombok.Data;
import org.bot0ff.entity.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class FightBuilder {
    private Player player;
    private Enemy enemy;
    private int timeToEndRound;
    private HttpStatus status;
}
