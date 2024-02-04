package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.InfoResponse;
import org.bot0ff.dto.FightResponse;
import org.bot0ff.dto.NavigateResponse;
import org.bot0ff.dto.unit.UnitEffect;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.HitType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.SubjectRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.service.generate.EntityGenerator;
import org.bot0ff.util.Constants;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.RandomUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightService {
    private final UnitRepository unitRepository;
    private final FightRepository fightRepository;
    private final LocationRepository locationRepository;
    private final SubjectRepository subjectRepository;

    private final EntityGenerator entityGenerator;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    public static Map<Long, FightHandler> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //начать сражение с выбранным unit
    @Transactional
    public String setStartFight(String name, Long initiatorId, Long targetId) {
        //определяем инициатора сражения и противника, в зависимости от того, кто напал
        Optional<Unit> optionalInitiator;
        if(name != null) {
            optionalInitiator = unitRepository.findByName(name);
        }
        else {
            optionalInitiator = unitRepository.findById(initiatorId);
        }

        if(optionalInitiator.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }
        Unit initiator = optionalInitiator.get();

        //поиск opponent в бд
        var optionalOpponent = unitRepository.findById(targetId);
        if(optionalOpponent.isEmpty()) {
            resetUnitFight(initiator, true);
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник не найден"));
            log.info("Не найден opponent в БД по запросу targetId: {}", targetId);
            return response;
        }
        Unit opponent = optionalOpponent.get();

        //если противник уже в бою, возвращаем уведомление
        if(opponent.getStatus().equals(Status.FIGHT)) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник уже сражается"));
            log.info("Попытка сражения с противником, который уже в бою - opponentId: {}", opponent.getId());
            return response;
        }

        //создание и сохранение нового сражения
        Fight newFight = getNewFight(initiator, opponent);
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        setUnitFight(initiator, newFight, 1L);

        //сохранение статуса FIGHT у enemy
        setUnitFight(opponent, newFight, 2L);

        //добавление нового сражения в map и запуск обработчика раундов
        FIGHT_MAP.put(newFightId, new FightHandler(
                newFightId, unitRepository, fightRepository, subjectRepository, entityGenerator, randomUtil
        ));

        return jsonProcessor
                .toJsonFight(new FightResponse(initiator, newFight, "Загрузка сражения..."));
    }

    //текущее состояние сражения
    public String getRefreshCurrentRound(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //если сражение не найдено и статус unit WIN, LOSS, сбрасываем настройки сражения unit, не меняя статус
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            switch (player.getStatus()) {
                case WIN, LOSS -> resetUnitFight(player, false);
                default -> resetUnitFight(player, true);
            }
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.getName());
            return response;
        }
        Fight fight = optionalFight.get();

        //если сражение завершено и статус unit НЕ ACTIVE, сбрасываем настройки сражения unit
        if(fight.isFightEnd()) {
            resetUnitFight(player, true);
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Попытка обращения к завершенному сражению от player: {}", player.getName());
            return response;
        }

        //устанавливает в ответе текущее время раунда
        fight.setEndRoundTimer(FIGHT_MAP.get(fight.getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player, fight, null));
    }

    //атака по выбранному противнику
    //TODO добавить id примененного умения
    @Transactional
    public String setAttack(String name, Long abilityId, Long targetId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //если сражение не найдено и статус unit WIN, LOSS, сбрасываем настройки сражения unit, не меняя статус
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            switch (player.getStatus()) {
                case WIN, LOSS -> resetUnitFight(player, false);
                default -> resetUnitFight(player, true);
            }
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.getName());
            return response;
        }
        Fight fight = optionalFight.get();

        //если ход уже сделан, возвращаем уведомление с текущим состоянием сражения
        if(player.isActionEnd()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Ход уже сделан"));
        }

        //находим примененное умение из бд
        Optional<Subject> optionalSubject = subjectRepository.findById(abilityId);
        if(optionalSubject.isEmpty()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Умение не найдено"));
        }
        Subject ability = optionalSubject.get();

        //находим выбранного unit
        Optional<Unit> optionalTarget = unitRepository.findById(targetId);
        if(optionalTarget.isEmpty()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник не найден"));
        }
        Unit target = optionalTarget.get();

        //уведомление при попытке использовать восстанавливающие умения на противниках
        if((ability.getHitType().equals(HitType.RECOVERY)
                | ability.getHitType().equals(HitType.BOOST))
                & !player.getTeamNumber().equals(target.getTeamNumber())) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Это умение для союзников"));
        }

        //уведомление при попытке использовать понижающие умения на союзниках
        if((ability.getHitType().equals(HitType.DAMAGE)
                | ability.getHitType().equals(HitType.LOWER))
                & player.getTeamNumber().equals(target.getTeamNumber())) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Это умение для противников"));
        }

        //если на цели уже применено данное умение, возвращаем уведомление
        if(ability.getHitType().equals(HitType.BOOST)
                | ability.getHitType().equals(HitType.LOWER)) {
            if((ability.getHp() != 0 && target.getUnitEffect().getDurationEffectHp() != 0)
                    | (ability.getMana() != 0 && target.getUnitEffect().getDurationEffectMana() != 0)
                    | (ability.getDamage() != 0 && target.getUnitEffect().getDurationEffectDamage() != 0)
                    | (ability.getDefense() != 0 && target.getUnitEffect().getDurationEffectDefense() != 0)) {
                return jsonProcessor
                        .toJsonInfo(new InfoResponse("Умение уже применено"));
            }
        }

        //сохранение умения и цели, по которой произведено действие
        player.setAbilityId(abilityId);
        player.setTargetId(targetId);
        player.setActionEnd(true);
        unitRepository.save(player);

        //если все участники сражения к этому времени сделали ходы, завершаем раунд
        if(fight.getUnits().stream().allMatch(Unit::isActionEnd)) {
            FIGHT_MAP.get(player.getFight().getId()).setEndRoundTimer(Instant.now().plusSeconds(3));
        }

        //устанавливает в ответе текущее время раунда
        fight.setEndRoundTimer(FIGHT_MAP.get(fight.getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player, fight, player.getName() + " применил умение " + ability.getName()));
    }

    //возвращает умения unit
    public String getUnitAbility(String name) {
        //поиск player в бд
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //TODO привязать расчет силы умений к unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.getAbility());

        return jsonProcessor
                .toJson(unitAbility);
    }

    //TODO сделать метод для массовых умений

    //создание UnitEffect для сражения
    private UnitEffect setUnitEffect(Unit unit) {
        return new UnitEffect(
                0, 0,
                0, 0,
                unit.getDamage(), 0, 0,
                unit.getDefense(), 0, 0
        );
    }

    //создание нового сражения
    private Fight getNewFight(Unit initiator, Unit opponent) {
        return new Fight(null,
                new ArrayList<>(List.of(initiator, opponent)),
                1, new ArrayList<>(), false,
                Instant.now().plusSeconds(Constants.ROUND_LENGTH_TIME).toEpochMilli());
    }

    //сохранение настроек нового сражения unit
    private void setUnitFight(Unit unit, Fight newFight, Long teamNumber) {
        unit.setActionEnd(false);
        unit.setStatus(Status.FIGHT);
        unit.setFight(newFight);
        unit.setTeamNumber(teamNumber);
        unit.setAbilityId(0L);
        unit.setTargetId(0L);
        unit.setUnitEffect(setUnitEffect(unit));
        unitRepository.save(unit);
    }

    //сброс настроек сражения unit при ошибке
    private void resetUnitFight(Unit unit, boolean isSetStatus) {
        if(isSetStatus) {
            unit.setStatus(Status.ACTIVE);
        }
        unit.setActionEnd(false);
        unit.setFight(null);
        unit.setTeamNumber(null);
        unit.setAbilityId(null);
        unit.setTargetId(null);
        unit.setUnitEffect(null);
        unitRepository.save(unit);
    }

    //заглушка на очистку статуса боя units
    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearFightDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {
            if(unit.getStatus().equals(Status.LOSS)) {
                Optional<Location> location = locationRepository.findById(unit.getLocationId());
                location.get().getAis().removeIf(u -> u.equals(unit.getId()));
                locationRepository.save(location.get());
                unitRepository.delete(unit);
            }
            else {
                unit.setHp(unit.getHp());
                unit.setUnitEffect(new UnitEffect());
                unit.setActionEnd(false);
                unit.setAbilityId(null);
                unit.setStatus(Status.ACTIVE);
                unit.setFight(null);
                unit.setUnitEffect(null);
                unitRepository.save(unit);
            }
        }
    }
}
