package org.bot0ff.service.fight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.model.InfoResponse;
import org.bot0ff.model.FightResponse;
import org.bot0ff.model.NavigateResponse;
import org.bot0ff.entity.unit.UnitEffect;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.ApplyType;
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

    private final PhysActionHandler physActionHandler;
    private final MagActionHandler magActionHandler;
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

        //находим активные умения unit
        List<Subject> initiatorAbility = subjectRepository.findAllById(initiator.getCurrentAbility());

        //поиск opponent в бд
        var optionalOpponent = unitRepository.findById(targetId);
        if(optionalOpponent.isEmpty()) {
            resetUnitFight(initiator, Status.ACTIVE);
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник уже ушел"));
            log.info("Не найден opponent в БД по запросу targetId: {}", targetId);
            return response;
        }
        Unit opponent = optionalOpponent.get();

        //если противник уже в бою, проверяем есть ли место в бою, для участия
        if(opponent.getStatus().equals(Status.FIGHT)) {
            //ищем количество участников в противоположной противника команде
            List<Unit> initiatorTeam = opponent.getFight().getUnits().stream().filter(unit -> !unit.getTeamNumber().equals(opponent.getTeamNumber())).toList();
            if(initiatorTeam.size() >= 3) {
                resetUnitFight(initiator, Status.ACTIVE);
                var response = jsonProcessor
                        .toJsonInfo(new InfoResponse("В сражении достаточно участников"));
                log.info("Нет места для сражения с противником - opponentId: {}", opponent.getId());
                return response;
            }
            //добавляем нападающего в противоположную команду в случайное место на поле сражения
            else {
                if(opponent.getTeamNumber().equals(1L)) {
                    setUnitFight(initiator, opponent.getFight(), 2L);
                }
                else {
                    setUnitFight(initiator, opponent.getFight(), 1L);
                }
                return jsonProcessor
                        .toJsonFight(new FightResponse(initiator, opponent.getFight(), initiatorAbility, "Загрузка сражения..."));
            }
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
                newFightId,
                unitRepository,
                fightRepository,
                subjectRepository,
                entityGenerator,
                physActionHandler,
                magActionHandler
        ));

        return jsonProcessor
                .toJsonFight(new FightResponse(initiator, newFight, initiatorAbility, "Загрузка сражения..."));
    }

    //текущее состояние сражения
    @Transactional
    public String getRefreshCurrentRound(String name) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //находим активные умения unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.getCurrentAbility());

        //если сражение не найдено в БД, сбрасываем настройки сражения unit
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            resetUnitFight(player, Status.ACTIVE);
            return jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
        }
        Fight fight = optionalFight.get();

        //если сражение завершено, устанавливаем unit статус победителя или проигравшего и возвращаем уведомление
        if(fight.isFightEnd()) {
            if(fight.getUnitsWin().stream().anyMatch(unit -> unit.equals(player.getId()))) {
                resetUnitFight(player, Status.WIN);
            }
            else {
                resetUnitFight(player, Status.LOSS);
            }
            return jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
        }

        //устанавливает в ответе время завершения раунда
        fight.setEndRoundTimer(getEndRoundTimer(fight.getId()));

        return jsonProcessor
                .toJsonFight(new FightResponse(player, fight, unitAbility, null));
    }

    //перемещение по полю сражения
    @Transactional
    public String moveOnFightFiled(String name, String direction) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //находим активные умения unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.getCurrentAbility());

        //если сражение не найдено в БД, сбрасываем настройки сражения unit
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            resetUnitFight(player, Status.ACTIVE);
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Не найдено сражение в БД по запросу player: {}", player.getName());
            return response;
        }
        Fight fight = optionalFight.get();

        //если очков движения нет, отправляем уведомление
        if(player.getPointAction() <= 0) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Не хватает очков действия"));
        }

        //перемещение по полю сражения
        String moveDirection = "";
        switch (direction) {
            case "left" -> {
                if(player.getFightPosition() - 1 >= 1) {
                    player.setPointAction(player.getPointAction() - 1);
                    player.setFightPosition(player.getFightPosition() - 1);
                    unitRepository.save(player);
                    moveDirection = " влево";
                }
                else {
                    return jsonProcessor
                            .toJsonInfo(new InfoResponse("Туда нельзя переместиться"));
                }
            }
            case "right" -> {
                if(player.getFightPosition() + 1 <= 8) {
                    player.setPointAction(player.getPointAction() - 1);
                    player.setFightPosition(player.getFightPosition() + 1);
                    unitRepository.save(player);
                    moveDirection = " вправо";
                }
                else {
                    return jsonProcessor
                            .toJsonInfo(new InfoResponse("Туда нельзя переместиться"));
                }
            }
        }
        return jsonProcessor
                .toJsonFight(new FightResponse(player, fight, unitAbility, player.getName() + " переместился " + moveDirection));
    }

    //атака по выбранному противнику оружием
    @Transactional
    public String setApplyWeapon(String name, Long targetId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //находим активные умения unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.getCurrentAbility());

        //если сражение не найдено, сбрасываем настройки сражения unit
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            resetUnitFight(player, Status.ACTIVE);
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

        //проверка наличия противника
        Optional<Unit> optionalTarget = fight.getUnits().stream().filter(unit -> unit.getId().equals(targetId)).findFirst();
        if(optionalTarget.isEmpty()) {
            log.info("Не найден противник в БД при атаке оружием - targetId: {}", targetId);
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник не найден"));
        }
        Unit target = optionalTarget.get();

        //уведомление при попытке атаковать союзника
        if(player.getTeamNumber().equals(target.getTeamNumber())) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Нельзя атаковать союзников"));
        }

        //проверка достаточности очков действия для нанесения удара
        if(player.getPointAction() < player.getWeapon().getPointAction()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Не хватает очков действия"));
        }

        //сохранение умения и цели, по которой произведено действие
        player.setAbilityId(0L);
        player.setTargetId(targetId);
        player.setActionEnd(true);
        unitRepository.save(player);

        //если все участники сражения к этому времени сделали ходы, завершаем раунд
        if(fight.getUnits().stream().allMatch(Unit::isActionEnd)) {
            FIGHT_MAP.get(player.getFight().getId()).setEndRoundTimer(Instant.now().plusSeconds(3));
        }

        //устанавливает в ответе текущее время раунда
        fight.setEndRoundTimer(FIGHT_MAP.get(fight.getId()).getEndRoundTimer().toEpochMilli());

        if(player.getWeapon().getId().equals(0L)) {
            return jsonProcessor
                    .toJsonFight(new FightResponse(player, fight, unitAbility, "Атака голыми кулаками"));
        }

        return jsonProcessor
                .toJsonFight(new FightResponse(player, fight, unitAbility, "Атака оружием " + player.getWeapon().getName()));
    }

    //атака по выбранному противнику умением
    @Transactional
    public String setApplyAbility(String name, Long abilityId, Long targetId) {
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonInfo(new InfoResponse("Игрок не найден"));
            log.info("Не найден player в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //находим активные умения unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.getCurrentAbility());

        //если сражение не найдено сбрасываем настройки сражения unit, не меняя статус
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            resetUnitFight(player, Status.ACTIVE);
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

        //если недостаточно маны для применения умения, возвращаем уведомление
        if(player.getMana() < ability.getCost()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Не хватает маны для этого умения"));
        }

        //находим выбранного unit
        Optional<Unit> optionalTarget = unitRepository.findById(targetId);
        if(optionalTarget.isEmpty()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Противник не найден"));
        }
        Unit target = optionalTarget.get();

        //уведомление при попытке использовать восстанавливающие умения на противниках
        if((ability.getApplyType().equals(ApplyType.RECOVERY)
                | ability.getApplyType().equals(ApplyType.BOOST))
                & !player.getTeamNumber().equals(target.getTeamNumber())) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Это умение для союзников"));
        }

        //уведомление при попытке использовать понижающие или атакующие умения на союзниках
        if((ability.getApplyType().equals(ApplyType.DAMAGE)
                | ability.getApplyType().equals(ApplyType.LOWER))
                & player.getTeamNumber().equals(target.getTeamNumber())) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Это умение для противников"));
        }

        //если на цели уже применено данное умение, возвращаем уведомление
        if(ability.getApplyType().equals(ApplyType.BOOST)
                | ability.getApplyType().equals(ApplyType.LOWER)) {
            if(target.getFightEffect().stream().anyMatch(unitEffect -> unitEffect.getId().equals(abilityId))) {
                return jsonProcessor
                        .toJsonInfo(new InfoResponse("Умение уже применено"));
            }
        }

        //если очков действия не хватает для применения умения, возвращаем уведомление
        if(player.getPointAction() < ability.getPointAction()) {
            return jsonProcessor
                    .toJsonInfo(new InfoResponse("Не хватает очков действия"));
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
                .toJsonFight(new FightResponse(player, fight, unitAbility, player.getName() + " применил умение " + ability.getName()));
    }

    //завершает ход unit
    public String setActionEnd(String name) {
        //поиск player в бд
        var optionalPlayer = unitRepository.findByName(name);
        if(optionalPlayer.isEmpty()) {
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Игрок не найден"));
            log.info("Не найден unit в БД по запросу username: {}", name);
            return response;
        }
        Unit player = optionalPlayer.get();

        //находим активные умения unit
        List<Subject> unitAbility = subjectRepository.findAllById(player.getCurrentAbility());

        //если сражение не найдено и статус unit WIN, LOSS, сбрасываем настройки сражения unit, не меняя статус
        Optional<Fight> optionalFight = Optional.ofNullable(player.getFight());
        if(optionalFight.isEmpty()) {
            resetUnitFight(player, Status.ACTIVE);
            var response = jsonProcessor
                    .toJsonNavigate(new NavigateResponse("Сражение завершено"));
            log.info("Не найдено сражение в БД по запросу обновления состояния от player: {}", player.getName());
            return response;
        }
        Fight fight = optionalFight.get();

        player.setActionEnd(true);
        unitRepository.save(player);

        //если все участники сражения к этому времени сделали ходы, завершаем раунд
        if(fight.getUnits().stream().allMatch(Unit::isActionEnd)) {
            FIGHT_MAP.get(player.getFight().getId()).setEndRoundTimer(Instant.now().plusSeconds(3));
        }

        //устанавливает в ответе текущее время раунда
        fight.setEndRoundTimer(FIGHT_MAP.get(fight.getId()).getEndRoundTimer().toEpochMilli());

        return jsonProcessor
                .toJsonFight(new FightResponse(player, fight, unitAbility, "Ход завершен"));
    }

    //TODO сделать метод для массовых умений

    //создание нового сражения
    private Fight getNewFight(Unit initiator, Unit opponent) {
        return new Fight(null,
                new ArrayList<>(List.of(initiator, opponent)),
                1, new ArrayList<>(), false, new ArrayList<>(), new ArrayList<>(),
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
        unit.setPointAction(unit.getMaxPointAction());
        unit.setFightPosition((long) randomUtil.getRandomFromTo(1, 8));
        unit.setFightEffect(List.of(new UnitEffect()));
        unitRepository.save(unit);
    }

    //возвращает время завершения раунда
    private Long getEndRoundTimer(Long fightId) {
        return FIGHT_MAP.get(fightId).getEndRoundTimer().toEpochMilli();
    }

    //сброс настроек сражения unit при ошибке
    private void resetUnitFight(Unit unit, Status status) {
        unit.setStatus(status);
        unit.setActionEnd(false);
        unit.setFight(null);
        unit.setFightPosition(null);
        unit.setFightEffect(null);
        unit.setTeamNumber(null);
        unit.setAbilityId(null);
        unit.setTargetId(null);
        unitRepository.save(unit);
    }

    //заглушка на очистку статуса боя units
    @Scheduled(fixedDelay = 3000000)
    @Transactional
    public void clearFightDB() {
        List<Unit> units = unitRepository.findAll();
        for(Unit unit: units) {
            unit.setHp(unit.getHp());
            unit.setFightPosition(null);
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
}
