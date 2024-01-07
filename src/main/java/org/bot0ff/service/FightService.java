package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.main.FightRequest;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.dto.response.MainBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final PlayerRepository playerRepository;
    private final LocationRepository locationRepository;
    private final JsonProcessor jsonProcessor;

    public String getStartState(String username, FightRequest fightRequest) {
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = MainBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJson(response);
        }

        return "";
    }
}
