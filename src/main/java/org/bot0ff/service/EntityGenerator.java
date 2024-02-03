package org.bot0ff.service;

import org.bot0ff.dto.unit.UnitArmor;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.entity.enums.UnitType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityGenerator {

    public Unit getNewAiUnit(Location location) {
        return new Unit(
                //общее
                null,
                "*Паук*",
                UnitType.AI,
                Status.ACTIVE,
                false,
                //локация
                location.getId(),
                //характеристики
                1,
                10,
                10,
                10,
                1,
                4,
                //экипировка
                new UnitArmor(),
                new UnitArmor(),
                new UnitArmor(),
                new UnitArmor(),
                new UnitArmor(),
                //умения
                List.of(1L),
                //сражение
                null,
                null,
                null,
                null,
                null);
    }
}
