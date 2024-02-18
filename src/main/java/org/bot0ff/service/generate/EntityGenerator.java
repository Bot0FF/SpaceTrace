package org.bot0ff.service.generate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.*;
import org.bot0ff.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityGenerator {
    private final ObjectsRepository objectsRepository;
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final ThingRepository thingRepository;
    private final AiRepository aiRepository;

    //генерация и сохранение нового ai
    public Long getNewAiUnitId(Location location) {
        Optional<Ai> optionalAiUnit = aiRepository.findById(1L);
        if(optionalAiUnit.isEmpty()) return 0L;
        Ai aiUnit = optionalAiUnit.get();

        Unit newAiUnit = new Unit(
                null,
                aiUnit.getName(),
                aiUnit.getUnitType(),
                aiUnit.getStatus(),
                aiUnit.isActionEnd(),
                location.getId(),
                aiUnit.getHp(),
                aiUnit.getMana(),
                aiUnit.getPointAction(),
                aiUnit.getMaxPointAction(),
                aiUnit.getStrength(),
                aiUnit.getIntelligence(),
                aiUnit.getDexterity(),
                aiUnit.getEndurance(),
                aiUnit.getLuck(),
                aiUnit.getBonusPoint(),
                aiUnit.getUnitSkill(),
                aiUnit.getCurrentAbility(),
                aiUnit.getAllAbility(),
                aiUnit.getWeapon(),
                aiUnit.getHead(),
                aiUnit.getHand(),
                aiUnit.getBody(),
                aiUnit.getLeg(),
                aiUnit.getFight(),
                aiUnit.getTeamNumber(),
                aiUnit.getLinePosition(),
                aiUnit.getFightStep(),
                aiUnit.getFightEffect()
        );
        return unitRepository.save(newAiUnit).getId();
    }

    //генерация и сохранение нового предмета на локацию
    public void setNewThingToLocation(Long locationId) {
        Optional<Objects> optionalThing = objectsRepository.findById(1L);
        if(optionalThing.isEmpty()) return;
        Optional<Location> location = locationRepository.findById(locationId);
        if(location.isEmpty()) return;
        Objects objects = optionalThing.get();
        Thing newThing = new Thing(
                null,
                objects.getName(),
                null,
                false,
                objects.getObjectType(),
                objects.getSkillType(),
                objects.getPhysDamage(),
                objects.getMagModifier(),
                objects.getHp(),
                objects.getMana(),
                objects.getPhysDefense(),
                objects.getMagDefense(),
                objects.getStrength(),
                objects.getIntelligence(),
                objects.getDexterity(),
                objects.getEndurance(),
                objects.getLuck(),
                objects.getDistance(),
                objects.getCondition(),
                //для книг
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                objects.getPrice(),
                objects.getDescription()
        );
        Long thingId = thingRepository.save(newThing).getId();
        location.get().getThings().add(thingId);
        locationRepository.save(location.get());
    }

    //генерация и сохранение нового предмета в инвентарь
    public Thing setNewThingToInventory(Long playerId, Objects object) {
        Thing newThing = new Thing(
                null,
                object.getName(),
                playerId,
                false,
                object.getObjectType(),
                object.getSkillType(),
                object.getPhysDamage(),
                object.getMagModifier(),
                object.getHp(),
                object.getMana(),
                object.getPhysDefense(),
                object.getMagDefense(),
                object.getStrength(),
                object.getIntelligence(),
                object.getDexterity(),
                object.getEndurance(),
                object.getLuck(),
                object.getDistance(),
                object.getCondition(),
                //для книг
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                object.getPrice(),
                object.getDescription()
        );
        return thingRepository.save(newThing);
    }
}
