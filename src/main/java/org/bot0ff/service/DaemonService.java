package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Location;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.UnitRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DaemonService {
    private final UnitRepository unitRepository;
    private final LocationRepository locationRepository;

    //заглушка на очистку статуса боя units
    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearFightDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {

//            unit.setStrength(1);
//            unit.setIntelligence(1);
//            unit.setDexterity(1);
//            unit.setEndurance(1);
//            unit.setLuck(1);
//            unit.setBonusPoint(50);

            unit.setHp(unit.getHp());
            unit.setLinePosition(null);
            unit.setHitPosition(null);
            unit.setTargetPosition(null);
            unit.setFightEffect(null);
            unit.setActionEnd(false);
            unit.setAbilityId(null);
            unit.setTargetId(null);
            unit.setTeamNumber(null);
            unit.setStatus(Status.ACTIVE);
            unit.setFight(null);
            unitRepository.save(unit);
        }
    }

    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearDeadAiDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {
            if(unit.getHp() <= 0) {
                Optional<Location> optionalLocation = locationRepository.findById(unit.getLocationId());
                optionalLocation.get().getAis().removeIf(u -> u.equals(unit.getId()));
                locationRepository.save(optionalLocation.get());
                unitRepository.delete(unit);
            }
        }
    }
}
