package org.bot0ff.dto;

import lombok.Builder;
import lombok.Data;
import org.bot0ff.entity.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class Response {
    private Player player;
    private Location location;
    private Fight fight;
    private String info;
    private HttpStatus status;
}
