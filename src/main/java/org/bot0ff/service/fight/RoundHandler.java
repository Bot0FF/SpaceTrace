package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Fight;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor

public class RoundHandler {

    public static Map<Long, Fight> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    @Scheduled(fixedDelay = 1000)
    public void mapRoundHandler() {

    }
}
