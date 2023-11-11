package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MoveResponse;
import org.bot0ff.entity.Player;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.Constants;
import org.bot0ff.world.World;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService{
    private final PlayerRepository playerRepository;

    @Override
    public MoveResponse getUserPosition(String username, String direction) {
        Player player = playerRepository.findByName(username).orElse(null);
        if(player == null) {
            return new MoveResponse();
        }

        String sector = player.getSector();
        int posX = player.getPosX();
        int posY = player.getPosY();

        int maxSectorPosX = 0;
        int maxSectorPosY = 0;

        switch (sector) {
            case "SUN" -> {
                maxSectorPosX = Constants.SUN_MAX_POS_X;
                maxSectorPosY = Constants.SUN_MAX_POS_Y;
            }
        }

        World.getLocation(sector, posX, posY).removePlayer(player);

        switch (direction) {
            case "up" -> posY = (posY < maxSectorPosY) ? posY + 1 : posY;
            case "down" -> posY = (posY > 1) ? posY - 1 : posY;
            case "left" -> posX = (posX > 1) ? posX - 1 : posX;
            case "right" -> posX = (posX < maxSectorPosX) ? posX + 1 : posX;
        }

        World.getLocation(sector, posX, posY).setPlayer(player);

        playerRepository.saveNewUserPosition(posX, posY, username);
        var newUserPositionEndpoint = "move?sector=" + sector + "&x=" + posX + "&y=" + posY;
        return new MoveResponse(newUserPositionEndpoint, username, sector, posX, posY);
    }
}
