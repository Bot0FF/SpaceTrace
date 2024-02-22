package org.bot0ff.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {

    //дальность действия оружия/умений
    //ONE_HAND - 1 клетка
    //TWO_HAND - 2 клетки
    //BOW - 5 клеток
    //FIRE - 4 клетки
    //WATER - 4 клетки
    //LAND - 4 клетки
    //AIR  - 4 клетки

    //коды ответов
    //1 - успешный ответ, метод отработал полностью
    //2 - успешный ответ, требующий перенаправления на другую страницу
    //3 - не успешный ответ, требующие перенаправления на главную и перезагрузки страницы
    //4 - не успешный ответ, информирующий об ошибке при обработке запроса

    //player
    public static int START_HP = 100;
    public static int START_MANA = 100;
    public static int START_DAMAGE = 4;
    public static int START_DEFENSE = 4;
    public static int START_POS_X = 2;
    public static int START_POS_Y = 2;

    //количество опыта для уровней навыков
    public static List<Integer> SKILL_EXP = Stream.of(
            1000, 5000, 10000, 15000, 20000
    ).sorted().collect(Collectors.toList());

    //максимальное количество существ на локации
    public static int MAX_COUNT_ENEMY_ON_LOCATION = 3;

    //максимальное количество участников сражения в команде
    public static int MAX_COUNT_FIGHT_TEAM = 3;

    //шанс появления противника на локации
    public static int CHANCE_CREATE_ENEMY = 50;

    //длительность раундов
    public static int ROUND_LENGTH_TIME = 60;

    //длина поля сражения (от 0 до ...)
    public static int FIGHT_LINE_LENGTH = 5;

    //количество очков движения, которые затрачиваются на применение оружия
    public static int POINT_ACTION_WEAPON = 1;

    //максимальное количество избранных умений
    public static int CURRENT_ABILITY_COUNT = 6;

    //ширина основной карты
    public static int MAIN_MAP_LENGTH = 10;
}
