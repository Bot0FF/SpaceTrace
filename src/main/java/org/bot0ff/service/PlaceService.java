package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.model.InfoResponse;
import org.bot0ff.model.ProfileResponse;
import org.bot0ff.repository.*;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.JsonProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final ThingRepository thingRepository;
    private final AbilityRepository abilityRepository;
    private final EntityGenerator entityGenerator;
    private final ObjectsRepository objectsRepository;

    private final JsonProcessor jsonProcessor;

    /** Взаимодействие с местом - дом unit */
    //страница профиля
    @Transactional
    public String getPlaceHome(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        return jsonProcessor
                .toJson(player);
    }
}
