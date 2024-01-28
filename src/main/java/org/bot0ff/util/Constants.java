package org.bot0ff.util;

public class Constants {

    //коды ответов
    //1 - успешный ответ, метод отработал полностью
    //2 - не найдена entity в БД
    //3 - завершенные действия, которые требуют перезагрузки страницы

    //player
    public static int START_HP = 100;
    public static int START_MANA = 100;
    public static int START_DAMAGE = 4;
    public static int START_DEFENSE = 4;
    public static int START_POS_X = 2;
    public static int START_POS_Y = 2;


    //Размеры карты
    public static int MAX_MAP_LENGTH = 9;

    //максимальное количество существ на локации
    public static int MAX_COUNT_ENEMY_ON_LOCATION = 3;

    //шанс появления противника на локации
    public static int CHANCE_CREATE_ENEMY = 50;

    //длительность раундов
    public static  int ROUND_LENGTH_TIME = 10;

    //значения в бд

}
