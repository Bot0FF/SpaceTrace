package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.Player;
import org.bot0ff.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService{
    private final PlayerRepository playerRepository;

    @Override
    public Player findByName(String name) {
        return playerRepository.findByName(name).orElseThrow(IllegalAccessError::new);
    }

    @Override
    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    @Override
    public Player findById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }
}
