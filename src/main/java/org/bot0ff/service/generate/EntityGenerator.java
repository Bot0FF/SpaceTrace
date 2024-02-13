package org.bot0ff.service.generate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Subject;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.SubjectType;
import org.bot0ff.entity.unit.UnitEffect;
import org.bot0ff.entity.unit.UnitSkill;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.SubjectRepository;
import org.bot0ff.repository.ThingRepository;
import org.bot0ff.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityGenerator {
    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;
    private final ThingRepository thingRepository;

    //генерация нового ai
    public Long setNewAiUnit(Location location) {
        Unit aiUnit = new Unit(
                //общее
                null,
                "*Муравей*",
                SubjectType.AI,
                Status.ACTIVE,
                false,
                //локация
                location.getId(),
                //основные характеристики
                10,
                10,
                4,
                4,
                //атрибуты
                1,
                1,
                1,
                1,
                1,
                0,
                //навыки
                new UnitSkill(),
                List.of(),
                List.of(),
                //экипировка
                new UnitArmor(),
                new UnitArmor(),
                new UnitArmor(),
                new UnitArmor(),
                new UnitArmor(),
                //сражение
                null,
                null,
                List.of(new UnitEffect()),
                null,
                null,
                null
        );
        return unitRepository.save(aiUnit).getId();
    }

    //генерация новой вещи
    public void setNewThing(Long locationId) {
        Optional<Subject> optionalSubject = subjectRepository.findById(1L);
        if(optionalSubject.isEmpty()) return;
        Optional<Location> location = locationRepository.findById(locationId);
        if(location.isEmpty()) return;
        Subject subject = optionalSubject.get();
        Thing newThing = new Thing(
                null,
                null,
                subject.getName(),
                subject.getSubjectType(),
                subject.getSkillType(),
                subject.getApplyType(),
                subject.getHp(),
                subject.getMana(),
                subject.getPhysDamage(),
                subject.getMagImpact(),
                subject.getMagDamageModifier(),
                subject.getPhysDefense(),
                subject.getMagDefense(),
                subject.getVitality(),
                subject.getSpirituality(),
                subject.getRegeneration(),
                subject.getMeditation(),
                subject.getEvade(),
                subject.getBlock(),
                subject.getDistance(),
                subject.getPointAction(),
                subject.getDuration(),
                subject.getCost(),
                subject.getPrice(),
                subject.getDescription(),
                false
        );
        Long thingId = thingRepository.save(newThing).getId();
        location.get().getThings().add(thingId);
        locationRepository.save(location.get());
    }
}
