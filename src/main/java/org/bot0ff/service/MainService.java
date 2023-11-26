package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MainResponse;
import org.bot0ff.dto.main.MoveResponse;
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
    public MainResponse getPlayerState(String username) {
        Player player = playerRepository.findByName(username).orElse(null);
        //TODO сделать ответ, если user не найден
        if(player == null) {
            return new MainResponse();
        }

        int posX = player.getPosX();
        int posY = player.getPosY();
        String locationType = String.valueOf(worldGenerator.getLocation(posX, posY).getLocationType());

        return new MainResponse(username, locationType, posX, posY);
    }

    //позиция user после перемещения
    public MoveResponse setPlayerPosition(String username, String direction) {
        Player player = playerRepository.findByName(username).orElse(null);
        //TODO сделать ответ, если user не найден
        if(player == null) {
            return new MoveResponse();
        }

        int posX = player.getPosX();
        int posY = player.getPosY();

        worldGenerator.getLocation(posX, posY).removePlayer(player);

        switch (direction) {
            case "up" -> posY = (posY < Constants.MAX_POS_Y) ? posY + 1 : posY;
            case "down" -> posY = (posY > 1) ? posY - 1 : posY;
            case "left" -> posX = (posX > 1) ? posX - 1 : posX;
            case "right" -> posX = (posX < Constants.MAX_POS_X) ? posX + 1 : posX;
        }

        worldGenerator.getLocation(posX, posY).setPlayer(player);

        String locationType = String.valueOf(worldGenerator.getLocation(posX, posY).getLocationType());

        playerRepository.saveNewUserPosition(posX, posY, username);
        return new MoveResponse(username, locationType, posX, posY);
    }
}
