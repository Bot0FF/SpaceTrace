package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class MainService {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    //состояние user после обновления страницы
    public JSONObject getPlayerState(String username) {
        var response = new JSONObject();
        var user = userRepository.findUserByName(username).orElse(null);
        var location = locationRepository.findById(Long.valueOf("" + user.getX() + user.getY())).orElse(null);
//        var enemies = location.getEnemies();
//        var heroes = location.getUsers();

        response.put("user", user);
//        response.put("enemies", enemies);
//        response.put("heroes", heroes);
        return response;
    }
}
