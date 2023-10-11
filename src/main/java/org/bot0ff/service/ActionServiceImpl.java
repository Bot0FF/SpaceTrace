package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.main.MoveResponse;
import org.bot0ff.dto.jpa.MoveUser;
import org.bot0ff.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService{
    private final UserRepository userRepository;

    @Override
    public MoveResponse getUserPosition(String username, String direction) {
        List<MoveUser> userPosition = userRepository.getPosxAndPosyByUserName(username);
        int posX = userPosition.get(0).getPosX();
        int posY = userPosition.get(0).getPosY();

        switch (direction) {
            case "up" -> posY = posY + 1;
            case "down" -> posY = posY - 1;
            case "left" -> posX = posX - 1;
            case "right" -> posX = posX + 1;
        }

        userRepository.saveNewUserPosition(posX, posY, username);
        return new MoveResponse(username, posX, posY);
    }
}
