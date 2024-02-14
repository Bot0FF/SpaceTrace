package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Objects;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.model.InfoResponse;
import org.bot0ff.model.ProfileResponse;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.ObjectsRepository;
import org.bot0ff.repository.ThingRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.JsonProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialService {
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final ThingRepository thingRepository;
    private final ObjectsRepository objectsRepository;

    private final EntityGenerator entityGenerator;
    private final JsonProcessor jsonProcessor;

    /** для админа */
    //добавить предмет в инвентарь по id
    public String addThingToInventory(String name, Long thingId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalObject = objectsRepository.findById(thingId);
        if(optionalObject.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Предмет не найден"));
            log.info("Не найден предмет в БД по запросу thingId: {}", thingId);
            return response;
        }
        Objects object = optionalObject.get();

        Thing newThing = entityGenerator.setNewThingToInventory(player.getId(), object);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, "Вещь (" + newThing.getName() + ") добавлена в инвентарь"));
    }

    //удалить предмет по id
    public String removeThingFromDB(String name, Long thingId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalThing = thingRepository.findById(thingId);
        if(optionalThing.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Предмет не найден"));
            log.info("Не найден предмет в БД по запросу thingId: {}", thingId);
            return response;
        }
        Thing thing = optionalThing.get();

        if(thing.isUse()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Нужно снять вещь"));
            log.info("Попытка удалить надетую вещь, thingId: {}", thingId);
            return response;
        }
        thingRepository.delete(thing);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, "Вещь (" + thing.getName() + ") удалена из БД"));
    }
}
