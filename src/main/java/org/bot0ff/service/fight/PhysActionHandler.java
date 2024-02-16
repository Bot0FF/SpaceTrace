package org.bot0ff.service.fight;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.enums.Status;
import org.bot0ff.util.RandomUtil;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class PhysActionHandler {
    private final RandomUtil randomUtil;

    //рассчитываем нанесенный физический урон
    //TODO настроить блок и уворот, расход маны
    public StringBuilder calculateDamageWeapon(Unit unit, Unit target) {
        //расчет блока
        if(randomUtil.getDoubleChance() <= target.getChanceBlock()) {
            return new StringBuilder()
                    .append("[")
                    .append(target.getName())
                    .append(" заблокировал удар ")
                    .append(unit.getName())
                    .append("]");
        }

        //расчет уворота
        if(randomUtil.getDoubleChance() <= target.getChanceEvade())  {
            return new StringBuilder()
                    .append("[")
                    .append(target.getName())
                    .append(" уклонился от удара ")
                    .append(unit.getName())
                    .append("]");
        }

        //рассчитываем урон, который нанес текущий unit противнику
        double unitHit = (unit.getPhysDamage()) * 1.0;
        double targetDefense = (target.getPhysDefense()) * 1.0;

        if (unitHit <= 1) unitHit = 1;
        if (targetDefense <= 1) targetDefense = 1;
        double damageMultiplier = unitHit / (unitHit + targetDefense);
        int totalDamage = (int) Math.round(unitHit * damageMultiplier);
        int result = randomUtil.getRNum30(totalDamage);
        if (result <= 0) result = 0;
        System.out.println("unit " + unit.getName() + " нанес урон равный " + result);
        target.setHp(target.getHp() - result);

        //устанавливаем target параметры по результатам расчета
        if (target.getHp() <= 0) {
            target.setActionEnd(false);
            target.setStatus(Status.LOSS);
            System.out.println(target.getName() + " ход отменен. HP <= 0");
        }

        if(unit.getWeapon().getId().equals(0L)) {
            return new StringBuilder()
                    .append("[")
                    .append(unit.getName())
                    .append(" нанес ")
                    .append(result)
                    .append(" урона противнику ")
                    .append(target.getName())
                    .append(" голыми руками")
                    .append("]");
        }

        return new StringBuilder()
                .append("[")
                .append(unit.getName())
                .append(" нанес ")
                .append(result)
                .append(" урона противнику ")
                .append(target.getName())
                .append(" оружием ")
                .append(unit.getWeapon().getName())
                .append("]");
    }
}
