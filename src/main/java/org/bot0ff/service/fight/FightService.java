package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.MistakeResponse;
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
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    public static Map<Long, FightHandler> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //начать сражение с выбранным unit
    @Transactional
    public String setStartFight(String name, Long initiatorId, Long targetId) {
        //определяем инициатора сражения и противника, в зависимости от того, кто напал
        Optional<Unit> initiator;
        if(name != null) {
            initiator = unitRepository.findByName(name);
        }
        else {
            initiator = unitRepository.findById(initiatorId);
        }

        if(initiator.isEmpty()) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }

        //поиск opponent в бд
        var opponent = unitRepository.findById(targetId);
        if(opponent.isEmpty()) {
            resetUnitFight(initiator.get(), true);
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Противник не найден"));
            log.info("Не найден opponent в БД по запросу name: {}", opponent);
            return response;
        }

        //если противник уже в бою, возвращаем уведомление
        if(opponent.get().getStatus().equals(Status.FIGHT)) {
            var response = jsonProcessor
                    .toJsonMistake(new MistakeResponse("Противник уже сражается"));
            log.info("Попытка сражения с противником, который уже в бою - opponentId: {}", opponent.get().getId());
            return response;
        }

        //создание и сохранение нового сражения
        Fight newFight = getNewFight(initiator.get(), opponent.get());
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        setUnitFight(initiator.get(), newFight, 1L);

        //сохранение статуса FIGHT у enemy
        setUnitFight(opponent.get(), newFight, 2L);

        //добавление нового сражения в map и запуск обработчика раундов
        FIGHT_MAP.put(newFightId, new FightHandler(
                newFightId, unitRepository, fightRepository, subjectRepository, randomUtil
        ));

        return jsonProcessor
                .toJsonFight(new FightResponse(initiator.get(), newFight, "Загрузка сражения..."));
    }

    //текущее состояние сражения
    public String getRefreshCurrentRound(String name) {
        //поиск unit в бд
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        //если сражение не найдено и статус unit WIN, LOSS, сбрасываем настройки сражения unit, не меняя статус
        Optional<Fight> fight = Optional.ofNullable(unit.get().getFight());
        if(fight.isEmpty()) {
            switch (unit.get().getStatus()) {
                case WIN, LOSS -> resetUnitFight(unit.get(), false);
                default -> resetUnitFight(unit.get(), true);
            }
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", unit.get().getName());
            return response;
        }

        //если сражение завершено и статус unit НЕ ACTIVE, сбрасываем настройки сражения unit
        if(fight.get().isFightEnd()) {
            resetUnitFight(unit.get(), true);
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Попытка обращения к завершенному сражению от player: {}", unit.get().getName());
            return response;
        }

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(unit.get(), fight.get(), null));
    }


    //возвращает умения unit
    public String getUnitAbility(String name) {
        //поиск player в бд
        var player = unitRepository.findByName(name);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        //TODO привязать расчет силы умений к unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.get().getAbility());

        return jsonProcessor
                .toJson(unitAbility);
    }

    //атака по выбранному противнику
    //TODO добавить id примененного умения
    @Transactional
    public String setAttack(String name, Long abilityId, Long targetId) {
        //поиск player в бд
        var player = unitRepository.findByName(name);
        if(player.isEmpty()) {
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        //поиск сражения
        Optional<Fight> fight = Optional.ofNullable(player.get().getFight());
        if(fight.isEmpty()) {
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение не найдено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.get().getName());
            return response;
        }

        //если ход уже сделан, возвращаем уведомление с текущим состоянием сражения
        if(player.get().isActionEnd()) {
            return jsonProcessor
                    .toJsonMistake(new MistakeResponse("Ход уже сделан"));
        }

        //находим примененное умение из бд
        Optional<Subject> ability = subjectRepository.findById(abilityId);
        if(ability.isEmpty()) {
            return jsonProcessor
                    .toJsonMistake(new MistakeResponse("Умение не найдено"));
        }
        //находим выбранного unit
        Optional<Unit> target = unitRepository.findById(targetId);
        if(target.isEmpty()) {
            return jsonProcessor
                    .toJsonMistake(new MistakeResponse("Противник не найден"));
        }

        //уведомление при попытке использовать восстанавливающие умения на противниках
        if((ability.get().getHitType().equals(HitType.RECOVERY)
                | ability.get().getHitType().equals(HitType.BOOST))
                & !player.get().getTeamNumber().equals(target.get().getTeamNumber())) {
            return jsonProcessor
                    .toJsonMistake(new MistakeResponse("Это умение для союзников"));
        }

        //уведомление при попытке использовать понижающие умения на союзниках
        if((ability.get().getHitType().equals(HitType.DAMAGE)
                | ability.get().getHitType().equals(HitType.LOWER))
                & player.get().getTeamNumber().equals(target.get().getTeamNumber())) {
            return jsonProcessor
                    .toJsonMistake(new MistakeResponse("Это умение для противников"));
        }

        //если на цели уже применено данное умение, возвращаем уведомление
        if(ability.get().getHitType().equals(HitType.BOOST)
                | ability.get().getHitType().equals(HitType.LOWER)) {
            if((ability.get().getHp() != 0 && target.get().getUnitEffect().getDurationEffectHp() != 0)
                    | (ability.get().getMana() != 0 && target.get().getUnitEffect().getDurationEffectMana() != 0)
                    | (ability.get().getDamage() != 0 && target.get().getUnitEffect().getDurationEffectDamage() != 0)
                    | (ability.get().getDefense() != 0 && target.get().getUnitEffect().getDurationEffectDefense() != 0)) {
                return jsonProcessor
                        .toJsonMistake(new MistakeResponse("Умение уже применено"));
            }
        }

        //сохранение умения и цели, по которой произведено действие
        player.get().setAbilityId(abilityId);
        player.get().setTargetId(targetId);
        player.get().setActionEnd(true);
        unitRepository.save(player.get());

        //если все участники сражения к этому времени сделали ходы, завершаем раунд
        if(fight.get().getUnits().stream().allMatch(Unit::isActionEnd)) {
            FIGHT_MAP.get(player.get().getFight().getId()).setEndRoundTimer(Instant.now().plusSeconds(3));
        }

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), fight.get(), player.get().getName() + " применил умение " + ability.get().getName()));
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
