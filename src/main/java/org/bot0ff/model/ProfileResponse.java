package org.bot0ff.model;

import lombok.Data;
import org.bot0ff.entity.Thing;
import org.bot0ff.entity.Unit;
import org.bot0ff.entity.unit.UnitSkill;
import org.bot0ff.util.Constants;

import java.util.List;
import java.util.TreeSet;

/** Класс ответа, с информацией о профиле игрока */

@Data
public class ProfileResponse {
    private Unit player;
    private UnitSkillExp unitSkill;
    private List<Thing> things;
    private String info;
    private int status;

    public ProfileResponse(Unit player, List<Thing> things, String info) {
        this.player = player;
        this.unitSkill = new UnitSkillExp(player.getUnitSkill());
        this.things = things;
        this.info = info;
        this.status = 1;
    }

    //Вспомогательный класс, преобразует UnitSkill в строки с количеством опыта
    @Data
    static class UnitSkillExp {
        private String oneHand;
        private String twoHand;
        private String bow;

        private String fire;
        private String water;
        private String land;
        private String air;

        //живучесть
        private String vitality;
        //духовность
        private String spirituality;
        //регенерация
        private String regeneration;
        //медитация
        private String meditation;
        //блокирование
        private String block;
        //уклонение
        private String evade;

        public UnitSkillExp(UnitSkill unitSkill) {
            this.oneHand = getSkillInfo(unitSkill.getOneHand());
            this.twoHand = getSkillInfo(unitSkill.getTwoHand());
            this.bow = getSkillInfo(unitSkill.getBow());
            this.fire = getSkillInfo(unitSkill.getFire());
            this.water = getSkillInfo(unitSkill.getWater());
            this.land = getSkillInfo(unitSkill.getLand());
            this.air = getSkillInfo(unitSkill.getAir());
            this.vitality = getSkillInfo(unitSkill.getVitality());
            this.spirituality = getSkillInfo(unitSkill.getSpirituality());
            this.regeneration = getSkillInfo(unitSkill.getRegeneration());
            this.meditation = getSkillInfo(unitSkill.getMeditation());
            this.block = getSkillInfo(unitSkill.getBlock());
            this.evade = getSkillInfo(unitSkill.getEvade());
        }

        private String getSkillInfo(int skill) {
            int level = 1;
            int skillExp = skill;
            int needSkillExp = 0;
            for(Integer levelExp: Constants.SKILL_EXP) {
                skill -= levelExp;
                if(skill < 0) {
                    needSkillExp = levelExp;
                    break;
                }
                level++;
            }

            return level + " (" + skillExp + "/" + needSkillExp + ")";
        }
    }
}
