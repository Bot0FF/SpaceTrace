package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.dto.ErrorResponse;
import org.bot0ff.dto.FightResponse;
import org.bot0ff.dto.ReloadResponse;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.HitType;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.repository.FightRepository;
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
    private final SubjectRepository subjectRepository;
    private final JsonProcessor jsonProcessor;
    private final RandomUtil randomUtil;

    public static Map<Long, FightHandler> FIGHT_MAP = Collections.synchronizedMap(new HashMap<>());

    //начать сражение с выбранным enemy
    @Transactional
    public String setStartFight(String name, Long targetId) {
        //поиск unit в бд
        var initiator = unitRepository.findByName(name);
        if(initiator.isEmpty()) {
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу name: {}", name);
            return response;
        }

        //поиск opponent в бд
        var opponent = unitRepository.findById(targetId);
        if(opponent.isEmpty()) {
            resetUnitFight(initiator.get());
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Противник не найден"));
            log.info("Не найден opponent в БД по запросу name: {}", opponent);
            return response;
        }

        //создание и сохранение нового сражения
        Fight newFight = getNewFight(initiator.get(), opponent.get());
        Long newFightId = fightRepository.save(newFight).getId();

        //сохранение статуса FIGHT у player
        setUnitFight(initiator.get(), newFight, 1L);

        //сохранение статуса FIGHT у enemy
        setUnitFight(opponent.get(), newFight, 2L);

        //проверка успешного добавления сражения ответа в БД
        Optional<Fight> currentFight = fightRepository.findById(newFightId);
        if(currentFight.isEmpty()) {
            resetUnitFight(initiator.get());
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Сражение не найдено"));
            log.info("Не найдено новое сражение в БД по запросу fightId: {}", newFightId);
            return response;
        }

        //добавление нового сражения в map и запуск обработчика раундов
        FIGHT_MAP.put(newFightId, new FightHandler(
                newFightId, unitRepository, fightRepository, subjectRepository, randomUtil
        ));

        return jsonProcessor
                .toJsonFight(new FightResponse(initiator.get(), currentFight.get(), "Загрузка сражения..."));
    }

    //текущее состояние сражения
    public String getRefreshCurrentRound(String name) {
        //поиск unit в бд
        var unit = unitRepository.findByName(name);
        if(unit.isEmpty()) {
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        Optional<Fight> fight = Optional.ofNullable(unit.get().getFight());
        if(fight.isEmpty()) {
            resetUnitFight(unit.get());
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Сражение не найдено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", unit.get().getName());
            return response;
        }

        //если сражение завершено и статус unit НЕ ACTIVE, сбрасываем настройки сражения unit
        if(fight.get().isFightEnd()) {
            resetUnitFight(unit.get());
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Сражение завершено"));
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
                    .toJsonReload(new ReloadResponse("Игрок не найден"));
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
                    .toJsonReload(new ReloadResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }

        //поиск сражения
        Optional<Fight> fight = Optional.ofNullable(player.get().getFight());
        if(fight.isEmpty()) {
            var response = jsonProcessor
                    .toJsonReload(new ReloadResponse("Сражение не найдено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.get().getName());
            return response;
        }

        //если ход уже сделан, возвращаем уведомление с текущим состоянием сражения
        if(player.get().isActionEnd()) {
            return jsonProcessor
                    .toJsonError(new ErrorResponse("Ход уже сделан"));
        }

        //находим примененное умение из бд
        Optional<Subject> ability = subjectRepository.findById(abilityId);
        if(ability.isEmpty()) {
            return jsonProcessor
                    .toJsonError(new ErrorResponse("Умение не найдено"));
        }
        //находим выбранного unit
        Optional<Unit> target = unitRepository.findById(targetId);
        if(target.isEmpty()) {
            return jsonProcessor
                    .toJsonError(new ErrorResponse("Противник не найден"));
        }

        //уведомление при попытке использовать восстанавливающие умения на противниках
        if((ability.get().getHitType().equals(HitType.RECOVERY)
                | ability.get().getHitType().equals(HitType.BOOST))
                & !player.get().getTeamNumber().equals(target.get().getTeamNumber())) {
            return jsonProcessor
                    .toJsonError(new ErrorResponse("Это умение для союзников"));
        }

        //уведомление при попытке использовать понижающие умения на союзниках
        if((ability.get().getHitType().equals(HitType.DAMAGE)
                | ability.get().getHitType().equals(HitType.LOWER))
                & player.get().getTeamNumber().equals(target.get().getTeamNumber())) {
            return jsonProcessor
                    .toJsonError(new ErrorResponse("Это умение для противников"));
        }

        //если на цели уже применено данное умение, возвращаем уведомление
        if(ability.get().getHitType().equals(HitType.BOOST)
                | ability.get().getHitType().equals(HitType.LOWER)) {
            if((ability.get().getHp() != 0 && target.get().getUnitJson().getDurationEffectHp() != 0)
                    | (ability.get().getMana() != 0 && target.get().getUnitJson().getDurationEffectMana() != 0)
                    | (ability.get().getDamage() != 0 && target.get().getUnitJson().getDurationEffectDamage() != 0)
                    | (ability.get().getDefense() != 0 && target.get().getUnitJson().getDurationEffectDefense() != 0)) {
                return jsonProcessor
                        .toJsonError(new ErrorResponse("Умение уже применено"));
            }
        }

        //сохранение умения и цели, по которой произведено действие
        player.get().setAbilityId(abilityId);
        player.get().setTargetId(targetId);
        player.get().setActionEnd(true);
        unitRepository.save(player.get());

        //если все участники сражения к этому времени сделали ходы, завершаем раунд
        if(fight.get().getUnits().stream().allMatch(Unit::isActionEnd)) {
            FIGHT_MAP.get(player.get().getFight().getId()).setEndRoundTimer(Instant.now().plusSeconds(1));
        }

        //устанавливает в ответе текущее время раунда
        fight.get().setEndRoundTimer(FIGHT_MAP.get(fight.get().getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player.get(), fight.get(), player.get().getName() + " применил умение " + ability.get().getName()));
    }

    //TODO сделать метод для массовых умений

    //создание UnitJson для сражения
    private UnitJson setUnitJson(Unit unit) {
        return new UnitJson(
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
        unit.setUnitJson(setUnitJson(unit));
        unitRepository.save(unit);
    }

    //сброс настроек сражения unit при ошибке
    private void resetUnitFight(Unit unit) {
        unit.setActionEnd(false);
        unit.setStatus(Status.ACTIVE);
        unit.setFight(null);
        unit.setTeamNumber(null);
        unit.setAbilityId(null);
        unit.setTargetId(null);
        unit.setUnitJson(null);
        unitRepository.save(unit);
    }



    //заглушка на очистку статуса боя units
    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearFightDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {
            unit.setHp(unit.getHp());
            unit.setActionEnd(false);
            unit.setStatus(Status.ACTIVE);
            unit.setFight(null);
            unit.setUnitJson(null);
            unitRepository.save(unit);
        }
    }
}
