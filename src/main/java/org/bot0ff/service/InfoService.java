package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.response.MainBuilder;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.util.JsonProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoService {
    private final PlayerRepository playerRepository;
    private final JsonProcessor jsonProcessor;

    //все players
    public String getAllPlayers() {
        var players = playerRepository.findAll();
        if(players.isEmpty()) {
            var response = MainBuilder.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдены players в БД по запросу getAllPlayers");
            return jsonProcessor.toJsonMainResponse(response);
        }
        var response = MainBuilder.builder()
                .players(players)
                .content(String.valueOf(players.size()))
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJsonMainResponse(response);
    }

    //профиль player
    public String getPlayerProfile(String username) {
        var player = playerRepository.findByName(username);
        if(player.isEmpty()) {
            var response = MainBuilder.builder()
                    .content("Игрок не найден")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найден player в БД по запросу username: {}", username);
            return jsonProcessor.toJsonMainResponse(response);
        }
        var response = MainBuilder.builder()
                .player(player.get())
                .status(HttpStatus.OK)
                .build();

        return jsonProcessor.toJsonMainResponse(response);
    }
}
