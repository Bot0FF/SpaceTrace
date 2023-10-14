package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MoveResponse;
import org.bot0ff.dto.jpa.MoveUser;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService{
    @Value("${app.endpoint.move}")
    private String userPositionEndpoint;
    private final UserRepository userRepository;

    @Override
    public MoveResponse getUserPosition(String username, String direction) {
        List<MoveUser> userPosition = userRepository.getSectorAndPosxAndPosyByUserName(username);
        String sector = userPosition.get(0).getSector();
        int posX = userPosition.get(0).getPosX();
        int posY = userPosition.get(0).getPosY();

        switch (direction) {
            case "up" -> posY = (posY < Constants.MAX_POS_Y) ? posY + 1 : posY;
            case "down" -> posY = (posY > Constants.MIN_POS_Y) ? posY - 1 : posY;
            case "left" -> posX = (posX > Constants.MIN_POS_X) ? posX - 1 : posX;
            case "right" -> posX = (posX < Constants.MAX_POS_X) ? posX + 1 : posX;
        }

        userRepository.saveNewUserPosition(posX, posY, username);
        var newUserPositionEndpoint = userPositionEndpoint + "sector=" + sector + "&x=" + posX + "&y=" + posY;
        return new MoveResponse(newUserPositionEndpoint, username, sector, posX, posY);
    }
}
