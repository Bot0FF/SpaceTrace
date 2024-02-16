package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.model.InfoResponse;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.model.ProfileResponse;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.ObjectsRepository;
import org.bot0ff.repository.ThingRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.JsonProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Обрабатывает запросы для страниц:
 * - Информация об игроке (profile)
 * - Список вещей в инвентаре и взаимодействие с ними (profile/inventory)
 * - Распределение аттрибутов (profile/attribute)
 * - Уровень навыков (profile/skill)
 * - Список и выбор умений (profile/ability) */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final ThingRepository thingRepository;

    private final JsonProcessor jsonProcessor;

    //страница профиля
    @Transactional
    public String getUnitProfileState(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, null));
    }

    //удаляет вещь из инвентаря
    @Transactional
    public String removeInventoryThing(String name, Long thingId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        var optionalLocation = locationRepository.findById(player.getLocationId());
        if(optionalLocation.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Локация не найдена"));
            log.info("Не найдена location в БД по запросу locationId: {}", player.getLocationId());
            return response;
        }
        Location location = optionalLocation.get();

        var optionalThing = thingRepository.findById(thingId);
        if(optionalThing.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Вещь не найдена"));
            log.info("Не найдена вещь в БД по запросу thingId: {}", thingId);
            return response;
        }
        Thing thing = optionalThing.get();

        if(thing.isUse()) {
            switch (thing.getObjectType()) {
                case WEAPON -> {
                    player.setWeapon(new UnitArmor());
                    thing.setUse(false);
                }
                case HEAD -> {
                    player.setHead(new UnitArmor());
                    thing.setUse(false);
                }
                case HAND -> {
                    player.setHand(new UnitArmor());
                    thing.setUse(false);
                }
                case BODY -> {
                    player.setBody(new UnitArmor());
                    thing.setUse(false);
                }
                case LEG -> {
                    player.setLeg(new UnitArmor());
                    thing.setUse(false);
                }
            }
        }

        //удаляем владельца у выбранной вещь и привязываем к локации
        thing.setUse(false);
        thing.setOwnerId(null);
        thingRepository.save(thing);
        location.getThings().add(thingId);
        locationRepository.save(location);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, "Вещь (" + thing.getName() + ") удалена из инвентаря"));
    }

    //надеть вещь из инвентаря
    @Transactional
    public String putOnInventoryThing(String name, Long thingId) {
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
                    .toJsonInfo(new InfoResponse("Вещь не найдена"));
            log.info("Не найдена вещь в БД по запросу thingId: {}", thingId);
            return response;
        }
        Thing thing = optionalThing.get();

        if(thing.isUse()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Вещь уже надета"));
            log.info("Попытка повторно надеть вещь, thingId: {}", thingId);
            return response;
        }

        switch (thing.getObjectType()) {
            case WEAPON -> {
                if(!player.getWeapon().getId().equals(0L)) {
                    takeOffExistThing(player.getWeapon());
                }
                player.setWeapon(new UnitArmor(
                        thing.getId(),
                        thing.getName(),
                        thing.getObjectType().name(),
                        thing.getSkillType().name(),
                        thing.getPhysDamage(),
                        thing.getMagModifier(),
                        thing.getHp(),
                        thing.getMana(),
                        thing.getPhysDefense(),
                        thing.getMagDefense(),
                        thing.getStrength(),
                        thing.getIntelligence(),
                        thing.getDexterity(),
                        thing.getEndurance(),
                        thing.getLuck(),
                        thing.getDistance(),
                        thing.getCondition()
                ));
                thing.setUse(true);
            }
            case HEAD -> {
                if(!player.getHead().getId().equals(0L)) {
                    takeOffExistThing(player.getHead());
                }
                player.setHead(new UnitArmor(
                        thing.getId(),
                        thing.getName(),
                        thing.getObjectType().name(),
                        thing.getSkillType().name(),
                        thing.getPhysDamage(),
                        thing.getMagModifier(),
                        thing.getHp(),
                        thing.getMana(),
                        thing.getPhysDefense(),
                        thing.getMagDefense(),
                        thing.getStrength(),
                        thing.getIntelligence(),
                        thing.getDexterity(),
                        thing.getEndurance(),
                        thing.getLuck(),
                        thing.getDistance(),
                        thing.getCondition()
                ));
                thing.setUse(true);
            }
            case HAND -> {
                if(!player.getHand().getId().equals(0L)) {
                    takeOffExistThing(player.getHand());
                }
                player.setHand(new UnitArmor(
                        thing.getId(),
                        thing.getName(),
                        thing.getObjectType().name(),
                        thing.getSkillType().name(),
                        thing.getPhysDamage(),
                        thing.getMagModifier(),
                        thing.getHp(),
                        thing.getMana(),
                        thing.getPhysDefense(),
                        thing.getMagDefense(),
                        thing.getStrength(),
                        thing.getIntelligence(),
                        thing.getDexterity(),
                        thing.getEndurance(),
                        thing.getLuck(),
                        thing.getDistance(),
                        thing.getCondition()
                ));
                thing.setUse(true);
            }
            case BODY -> {
                if(!player.getBody().getId().equals(0L)) {
                    takeOffExistThing(player.getBody());
                }
                player.setBody(new UnitArmor(
                        thing.getId(),
                        thing.getName(),
                        thing.getObjectType().name(),
                        thing.getSkillType().name(),
                        thing.getPhysDamage(),
                        thing.getMagModifier(),
                        thing.getHp(),
                        thing.getMana(),
                        thing.getPhysDefense(),
                        thing.getMagDefense(),
                        thing.getStrength(),
                        thing.getIntelligence(),
                        thing.getDexterity(),
                        thing.getEndurance(),
                        thing.getLuck(),
                        thing.getDistance(),
                        thing.getCondition()
                ));
                thing.setUse(true);
            }
            case LEG -> {
                if(!player.getLeg().getId().equals(0L)) {
                    takeOffExistThing(player.getLeg());
                }
                player.setLeg(new UnitArmor(
                        thing.getId(),
                        thing.getName(),
                        thing.getObjectType().name(),
                        thing.getSkillType().name(),
                        thing.getPhysDamage(),
                        thing.getMagModifier(),
                        thing.getHp(),
                        thing.getMana(),
                        thing.getPhysDefense(),
                        thing.getMagDefense(),
                        thing.getStrength(),
                        thing.getIntelligence(),
                        thing.getDexterity(),
                        thing.getEndurance(),
                        thing.getLuck(),
                        thing.getDistance(),
                        thing.getCondition()
                ));
                thing.setUse(true);
            }
        }

        unitRepository.save(player);
        thingRepository.save(thing);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, "Вещь (" + thing.getName() + ") надета"));
    }

    //снять надетую вещь
    @Transactional
    public String takeOffInventoryThing(String name, Long thingId) {
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
                    .toJsonInfo(new InfoResponse("Вещь не найдена"));
            log.info("Не найдена вещь в БД по запросу thingId: {}", thingId);
            return response;
        }
        Thing thing = optionalThing.get();

        if(!thing.isUse()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Вещь не надета"));
            log.info("Попытка повторно снять вещь, thingId: {}", thingId);
            return response;
        }

        switch (thing.getObjectType()) {
            case WEAPON -> {
                thing.setCondition(player.getWeapon().getCondition());
                player.setWeapon(new UnitArmor());
                thing.setUse(false);
            }
            case HEAD -> {
                thing.setCondition(player.getHead().getCondition());
                player.setHead(new UnitArmor());
                thing.setUse(false);
            }
            case HAND -> {
                thing.setCondition(player.getHand().getCondition());
                player.setHand(new UnitArmor());
                thing.setUse(false);
            }
            case BODY -> {
                thing.setCondition(player.getBody().getCondition());
                player.setBody(new UnitArmor());
                thing.setUse(false);
            }
            case LEG -> {
                thing.setCondition(player.getLeg().getCondition());
                player.setLeg(new UnitArmor());
                thing.setUse(false);
            }
        }

        unitRepository.save(player);
        thingRepository.save(thing);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, "Вещь (" + thing.getName() + ") снята"));
    }

    //повышение аттрибутов
    @Transactional
    public String upAttribute(String name, String attribute) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        if(player.getBonusPoint() <= 0) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Нет бонусных очков"));
            log.info("Отсутствуют бонусные очки при распределении username: {}", name);
            return response;
        }

        String message = "Невозможно повысить аттрибут";
        switch (attribute) {
            case "strength" -> {
                player.setStrength(player.getStrength() + 1);
                message = "Сила повышена на 1 единицу";
                player.setBonusPoint(player.getBonusPoint() - 1);
            }
            case "intelligence" -> {
                player.setIntelligence(player.getIntelligence() + 1);
                message = "Интеллект повышен на 1 единицу";
                player.setBonusPoint(player.getBonusPoint() - 1);
            }
            case "dexterity" -> {
                player.setDexterity(player.getDexterity() + 1);
                message = "Ловкость повышена на 1 единицу";
                player.setBonusPoint(player.getBonusPoint() - 1);
            }
            case "endurance" -> {
                player.setEndurance(player.getEndurance() + 1);
                message = "Выносливость повышена на 1 единицу";
                player.setBonusPoint(player.getBonusPoint() - 1);
            }
            case "luck" -> {
                player.setLuck(player.getLuck() + 1);
                message = "Удача повышена на 1 единицу";
                player.setBonusPoint(player.getBonusPoint() - 1);
            }
        }

        unitRepository.save(player);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, message));
    }

    /** для админа */
    //понижение аттрибутов
    @Transactional
    public String downAttribute(String name, String attribute) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        String message = "Невозможно понизить аттрибут";
        switch (attribute) {
            case "strength" -> {
                if(player.getStrength() > 1) {
                    player.setStrength(player.getStrength() - 1);
                    message = "Сила понижена на 1 единицу";
                    player.setBonusPoint(player.getBonusPoint() + 1);
                }
            }
            case "intelligence" -> {
                if(player.getIntelligence() > 1) {
                    player.setIntelligence(player.getIntelligence() - 1);
                    message = "Интеллект понижен на 1 единицу";
                    player.setBonusPoint(player.getBonusPoint() + 1);
                }
            }
            case "dexterity" -> {
                if(player.getDexterity() > 1) {
                    player.setDexterity(player.getDexterity() - 1);
                    message = "Ловкость понижена на 1 единицу";
                    player.setBonusPoint(player.getBonusPoint() + 1);
                }
            }
            case "endurance" -> {
                if(player.getEndurance() > 1) {
                    player.setEndurance(player.getEndurance() - 1);
                    message = "Выносливость понижена на 1 единицу";
                    player.setBonusPoint(player.getBonusPoint() + 1);
                }
            }
            case "luck" -> {
                if(player.getLuck() > 1) {
                    player.setLuck(player.getLuck() - 1);
                    message = "Удача понижена на 1 единицу";
                    player.setBonusPoint(player.getBonusPoint() + 1);
                }
            }
        }

        unitRepository.save(player);

        var optionalThings = thingRepository.findAllByOwnerId(player.getId());
        List<Thing> things = optionalThings.stream().toList();

        return jsonProcessor
                .toJsonProfile(new ProfileResponse(player, things, message));
    }

    //снимает существующую вещь, перед надеванием другой
    public void takeOffExistThing(UnitArmor existThing) {
        var optionalExistThing = thingRepository.findById(existThing.getId());
        if(optionalExistThing.isEmpty()) {
            log.info("Ошибка при поиске надетой вещи, existThingId: {}", existThing.getId());
            return;
        }
        Thing thing = optionalExistThing.get();

        thing.setCondition(existThing.getCondition());
        thing.setUse(false);
        thingRepository.save(thing);
    }

}
