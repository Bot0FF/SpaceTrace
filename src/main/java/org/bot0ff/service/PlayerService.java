package org.bot0ff.service;

import org.bot0ff.entity.Player;

import java.util.Optional;

public interface PlayerService {
    Player findByName(String name);
    void savePlayer(Player player);
    Player findById(Long id);
}
