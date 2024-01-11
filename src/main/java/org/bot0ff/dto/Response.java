package org.bot0ff.dto;

import lombok.Builder;
import lombok.Data;
import org.bot0ff.entity.*;

@Data
@Builder
public class Response {
    private Unit player;
    private Location location;
    private Fight fight;
    private String info;
    private int status;
}
