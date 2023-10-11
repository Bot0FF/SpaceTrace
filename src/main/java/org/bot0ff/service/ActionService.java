package org.bot0ff.service;

import org.bot0ff.dto.main.MoveResponse;

public interface ActionService {
    MoveResponse getUserPosition(String username, String direction);
}
