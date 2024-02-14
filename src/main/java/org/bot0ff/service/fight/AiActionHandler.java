package org.bot0ff.service.fight;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Ability;
import org.bot0ff.entity.Fight;
import org.bot0ff.entity.Objects;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.ApplyType;
import org.bot0ff.entity.enums.UnitType;
import org.bot0ff.repository.AbilityRepository;
import org.bot0ff.repository.FightRepository;
import org.bot0ff.repository.UnitRepository;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Slf4j
@Service
public class AiActionHandler {
    private final UnitRepository unitRepository;
    private final FightRepository fightRepository;
    private final AbilityRepository abilityRepository;

    private final RandomUtil randomUtil;

    public void setAiAction(Long fightId) throws InterruptedException {
        System.out.println("------------------------");
        System.out.println("Старт обработки действий AI");
        Optional<Fight> fight = fightRepository.findById(fightId);
        if(fight.isEmpty()) {
            System.out.println("Не найдено сражение с fightId " + fightId);
            return;
        }

        //делим на команды всех unit
        List<Unit> teamOne = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.getTeamNumber() == 1).toList());
        List<Unit> teamTwo = new ArrayList<>(fight.get().getUnits().stream().filter(unit -> unit.getTeamNumber() == 2).toList());

        //все aiUnit первой команды применяют атаку на любом unit из второй команды
        for(Unit aiUnit : teamOne){
            System.out.println("---------------------");
            System.out.println("Ход AI первой команды");
            if(aiUnit.getUnitType().equals(UnitType.AI)) {
                //определяем противника
                Unit target = getRandomUser(teamTwo);
                //передвигаем AI в случайное место на линии сражения
                do{
                    int changeAiAction = randomUtil.getRandomFromTo(1, 4);
                    switch (changeAiAction){
                        case 1 -> setAiMove(aiUnit);
                        case 2 -> setAiApplyWeapon(aiUnit, target);
                        case 3 -> setAiApplyBow(aiUnit, target);
                        case 4 -> setAiApplyAbility(aiUnit, target);
                    }
                } while (aiUnit.getPointAction() > 0);
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }

        //все aiUnit второй команды применяют атаку на любом unit из первой команды
        for(Unit aiUnit : teamTwo){
            System.out.println("---------------------");
            System.out.println("Ход AI второй команды");
            if(aiUnit.getUnitType().equals(UnitType.AI)) {
                //определяем противника
                Unit target = getRandomUser(teamOne);
                //передвигаем AI в случайное место на линии сражения
                do{
                    int changeAiAction = randomUtil.getRandomFromTo(0, 3);
                    switch (changeAiAction){
                        case 1 -> setAiMove(aiUnit);
                        case 2 -> setAiApplyWeapon(aiUnit, target);
                        case 3 -> setAiApplyBow(aiUnit, target);
                        case 4 -> setAiApplyAbility(aiUnit, target);
                    }

                } while (aiUnit.getPointAction() > 0);
                aiUnit.setActionEnd(true);
                unitRepository.save(aiUnit);
            }
        }
        System.out.println("Ходы aiUnit завершены");
    }

    //расчет случайного противника
    public Unit getRandomUser(List<Unit> team) {
        Unit target;
        do{
            target = team.get(randomUtil.getRandomFromTo(0, team.size() - 1));
        } while (target.getUnitType().equals(UnitType.AI));

        return target;
    }

    //перемещение aiUnit
    private void setAiMove(Unit aiUnit) {
        int direction = randomUtil.getRandom1or2();
        switch (direction) {
            case 1 -> {
                if(aiUnit.getLinePosition() != 1) {
                    aiUnit.setLinePosition(aiUnit.getLinePosition() - 1);
                    aiUnit.setPointAction(aiUnit.getPointAction() - 1);
                    unitRepository.save(aiUnit);
                    System.out.println(aiUnit.getName() + " переместился на 1 влево");
                }
                else {
                    aiUnit.setLinePosition(aiUnit.getLinePosition() + 1);
                    aiUnit.setPointAction(aiUnit.getPointAction() - 1);
                    unitRepository.save(aiUnit);
                    System.out.println(aiUnit.getName() + " переместился на 1 вправо");
                }
            }
            case 2 -> {
                if(aiUnit.getLinePosition() != 8) {
                    aiUnit.setLinePosition(aiUnit.getLinePosition() + 1);
                    aiUnit.setPointAction(aiUnit.getPointAction() - 1);
                    unitRepository.save(aiUnit);
                    System.out.println(aiUnit.getName() + " переместился на 1 вправо");
                }
                else {
                    aiUnit.setLinePosition(aiUnit.getLinePosition() - 1);
                    aiUnit.setPointAction(aiUnit.getPointAction() - 1);
                    unitRepository.save(aiUnit);
                    System.out.println(aiUnit.getName() + " переместился на 1 влево");
                }
            }
        }
    }

    //применение оружия
    private void setAiApplyWeapon(Unit aiUnit, Unit target) {
        if(aiUnit.getWeapon().getSkillType().equals("BOW")) {
            System.out.println("Тип оружия не соответствует типу одноручное или двуручное. Возврат к выбору действия");
            return;
        }
        if(!aiUnit.getTargetId().equals(0L)) {
            System.out.println("Противник уже выбран. Возврат к выбору действия");
            return;
        }
        if(aiUnit.getPointAction() < 2) {
            System.out.println("Недостаточно очков действия для нанесения удара оружием. Возврат к выбору действия");
            return;
        }

        if((aiUnit.getLinePosition() - target.getLinePosition()) >= -1
                | (aiUnit.getLinePosition() - target.getLinePosition()) <= 1) {
            aiUnit.setTargetId(target.getId());
            aiUnit.setPointAction(aiUnit.getPointAction() - 2);
            aiUnit.setHitPosition(aiUnit.getLinePosition());
            aiUnit.setTargetPosition(target.getLinePosition());
            unitRepository.save(aiUnit);
            System.out.println("Выбран противник для нанесения удара оружием");
        }
        else {
            setAiMove(aiUnit);
            System.out.println("Превышение расстояния применения оружия. Возврат к выбору действия");
        }
    }

    //применение оружия
    private void setAiApplyBow(Unit aiUnit, Unit target) {
        if(!aiUnit.getWeapon().getSkillType().equals("BOW")) {
            System.out.println("Тип оружия не соответствует типу лук. Возврат к выбору действия");
            return;
        }
        if(!aiUnit.getTargetId().equals(0L)) {
            System.out.println("Противник уже выбран. Возврат к выбору действия");
            return;
        }
        if(aiUnit.getPointAction() < 2) {
            System.out.println("Недостаточно очков действия для выстрела из лука. Возврат к выбору действия");
            return;
        }

        if((aiUnit.getLinePosition() - target.getLinePosition()) >= -5
                | (aiUnit.getLinePosition() - target.getLinePosition()) <= 5) {
            aiUnit.setTargetId(target.getId());
            aiUnit.setPointAction(aiUnit.getPointAction() - 2);
            aiUnit.setHitPosition(aiUnit.getLinePosition());
            aiUnit.setTargetPosition(target.getLinePosition());
            unitRepository.save(aiUnit);
            System.out.println("Выбран противник для выстрела из лука");
        }
        else {
            setAiMove(aiUnit);
            System.out.println("Превышение расстояния применения лука. Возврат к выбору действия");
        }
    }

    //применение умения
    private void setAiApplyAbility(Unit aiUnit, Unit target) {
        if(aiUnit.getCurrentAbility().isEmpty()) {
            System.out.println("aiUnit не имеет умений. Возврат к выбору действия");
            return;
        }
        if(!aiUnit.getAbilityId().equals(0L)) {
            System.out.println("aiUnit уже применил умение. Возврат к выбору действия");
            return;
        }
        Long numberAbility = (long) randomUtil.getRandomFromTo(0, aiUnit.getCurrentAbility().size() - 1);
        Optional<Ability> optionalAbility = abilityRepository.findById(numberAbility);
        if(optionalAbility.isEmpty()) return;
        Ability ability = optionalAbility.get();

        if(aiUnit.getMana() < ability.getManaCost()) {
            System.out.println("Недостаточно маны для применения умения. Возврат к выбору действия");
            return;
        }

        if(ability.getApplyType().equals(ApplyType.DAMAGE)) {
            if((aiUnit.getLinePosition() - target.getLinePosition()) >= -6
                    | (aiUnit.getLinePosition() - target.getLinePosition()) <= 6) {
                aiUnit.setMana(aiUnit.getMana() - ability.getManaCost());
                aiUnit.setAbilityId(numberAbility);
                aiUnit.setTargetId(target.getId());
                aiUnit.setHitPosition(aiUnit.getLinePosition());
                aiUnit.setTargetPosition(target.getLinePosition());
                aiUnit.setPointAction(aiUnit.getPointAction() - ability.getPointAction());
                unitRepository.save(aiUnit);
                System.out.println("Выбрано атакующее умение. Возврат к выбору действия");
            }
            else {
                setAiMove(aiUnit);
                System.out.println("Превышение расстояния применения умения. Возврат к выбору действия");
            }
        }
        else {
            aiUnit.setMana(aiUnit.getMana() - ability.getManaCost());
            aiUnit.setAbilityId(numberAbility);
            aiUnit.setHitPosition(aiUnit.getLinePosition());
            aiUnit.setTargetPosition(target.getLinePosition());
            aiUnit.setPointAction(aiUnit.getPointAction() - ability.getPointAction());
            System.out.println("Выбрано не атакующее умение.");
        }
    }
}
