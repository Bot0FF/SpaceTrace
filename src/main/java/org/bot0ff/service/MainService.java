package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.Player;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.world.WorldGenerator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {
    private final PlayerRepository playerRepository;
    private final WorldGenerator worldGenerator;

    //состояние user после обновления страницы
    public Player getPlayerState(String username) {
        Player player = playerRepository.findByName(username).orElse(null);
        //TODO сделать ответ, если user не найден
        if(player == null) {
            return null;
        }
        return player;
    }

    //позиция user после перемещения
    public Player setPlayerPosition(String username, String direction) {
        Player player = playerRepository.findByName(username).orElse(null);
        if(player == null) {
            return null;
        }

        int posX = player.getPosX();
        int posY = player.getPosY();
        worldGenerator.getLocation(posX, posY).removePlayer(player);

        switch (direction) {
            case "up" -> {
                if(posY + 1 < Constants.MAX_POS_Y) {
                    player.setPosY(posY + 1);
                }
            }
            case "down" -> {
                if(posY - 1 > 1) {
                    player.setPosY(posY - 1);
                }
            }
            case "left" -> {
                if(posX - 1 > 1) {
                    player.setPosX(posX - 1);
                }
            }
            case "right" -> {
                if(posX + 1 < Constants.MAX_POS_X) {
                    player.setPosX(posX + 1);
                }
            }
        }

        worldGenerator.getLocation(player.getPosX(), player.getPosY()).setPlayer(player);
        playerRepository.saveNewUserPosition(player.getPosX(), player.getPosY(), player.getName());
        return player;
    }
}
