--locations
with data(id, location_type, name, x, y, ais, units, things)
   as (values
        (11, 'PLAIN', 'Равнина', 1, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (12, 'PLAIN', 'Равнина', 1, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (13, 'PLAIN', 'Равнина', 1, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (14, 'PLAIN', 'Равнина', 1, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (15, 'PLAIN', 'Равнина', 1, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (16, 'PLAIN', 'Равнина', 1, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (17, 'PLAIN', 'Равнина', 1, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (18, 'PLAIN', 'Равнина', 1, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (19, 'PLAIN', 'Равнина', 1, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (21, 'PLAIN', 'Равнина', 2, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (22, 'PLAIN', 'Равнина', 2, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (23, 'PLAIN', 'Равнина', 2, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (24, 'PLAIN', 'Равнина', 2, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (25, 'PLAIN', 'Равнина', 2, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (26, 'PLAIN', 'Равнина', 2, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (27, 'PLAIN', 'Равнина', 2, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (28, 'PLAIN', 'Равнина', 2, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (29, 'PLAIN', 'Равнина', 2, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (31, 'PLAIN', 'Равнина', 3, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (32, 'PLAIN', 'Равнина', 3, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (33, 'PLAIN', 'Равнина', 3, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (34, 'PLAIN', 'Равнина', 3, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (35, 'PLAIN', 'Равнина', 3, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (36, 'PLAIN', 'Равнина', 3, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (37, 'PLAIN', 'Равнина', 3, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (38, 'PLAIN', 'Равнина', 3, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (39, 'PLAIN', 'Равнина', 3, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (41, 'PLAIN', 'Равнина', 4, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (42, 'PLAIN', 'Равнина', 4, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (43, 'PLAIN', 'Равнина', 4, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (44, 'PLAIN', 'Равнина', 4, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (45, 'PLAIN', 'Равнина', 4, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (46, 'PLAIN', 'Равнина', 4, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (47, 'PLAIN', 'Равнина', 4, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (48, 'PLAIN', 'Равнина', 4, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (49, 'PLAIN', 'Равнина', 4, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (51, 'PLAIN', 'Равнина', 5, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (52, 'PLAIN', 'Равнина', 5, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (53, 'PLAIN', 'Равнина', 5, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (54, 'PLAIN', 'Равнина', 5, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (55, 'PLAIN', 'Равнина', 5, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (56, 'PLAIN', 'Равнина', 5, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (57, 'PLAIN', 'Равнина', 5, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (58, 'PLAIN', 'Равнина', 5, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (59, 'PLAIN', 'Равнина', 5, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (61, 'PLAIN', 'Равнина', 6, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (62, 'PLAIN', 'Равнина', 6, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (63, 'PLAIN', 'Равнина', 6, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (64, 'PLAIN', 'Равнина', 6, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (65, 'PLAIN', 'Равнина', 6, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (66, 'PLAIN', 'Равнина', 6, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (67, 'PLAIN', 'Равнина', 6, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (68, 'PLAIN', 'Равнина', 6, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (69, 'PLAIN', 'Равнина', 6, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (71, 'PLAIN', 'Равнина', 7, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (72, 'PLAIN', 'Равнина', 7, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (73, 'PLAIN', 'Равнина', 7, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (74, 'PLAIN', 'Равнина', 7, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (75, 'PLAIN', 'Равнина', 7, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (76, 'PLAIN', 'Равнина', 7, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (77, 'PLAIN', 'Равнина', 7, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (78, 'PLAIN', 'Равнина', 7, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (79, 'PLAIN', 'Равнина', 7, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (81, 'PLAIN', 'Равнина', 8, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (82, 'PLAIN', 'Равнина', 8, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (83, 'PLAIN', 'Равнина', 8, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (84, 'PLAIN', 'Равнина', 8, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (85, 'PLAIN', 'Равнина', 8, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (86, 'PLAIN', 'Равнина', 8, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (87, 'PLAIN', 'Равнина', 8, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (88, 'PLAIN', 'Равнина', 8, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (89, 'PLAIN', 'Равнина', 8, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),

        (91, 'PLAIN', 'Равнина', 9, 1, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (92, 'PLAIN', 'Равнина', 9, 2, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (93, 'PLAIN', 'Равнина', 9, 3, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (94, 'PLAIN', 'Равнина', 9, 4, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (95, 'PLAIN', 'Равнина', 9, 5, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (96, 'PLAIN', 'Равнина', 9, 6, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (97, 'PLAIN', 'Равнина', 9, 7, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (98, 'PLAIN', 'Равнина', 9, 8, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[]),
        (99, 'PLAIN', 'Равнина', 9, 9, ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[])
)
insert into location (id, location_type, name, x, y, ais, units, things)
select d.id, d.location_type, d.name, d.x, d.y, d.ais, d.units, d.things
from data d
where not exists (select 1
                  from location u2
                  where u2.id = d.id);

--unit
with data(id, name, subject_type, status, action_end,
            location_id,
            hp, max_hp, mana, max_mana, damage, defense, evade, max_move_point,
            weapon, head, hand, body, leg,
            power, agility, endurance, magic, free_point,
            unit_skill,
            ability,
            unit_fight_effect)
   as (values
        (1, 'user', 'USER', 'ACTIVE', false,
            22,
            10, 10, 10, 10, 1, 1, 1, 4,
            '{"id": "", "name": "", "hp": "", "mana": "", "damage": "", "defense": "", "duration": ""}',
            '{"id": "", "name": "", "hp": "", "mana": "", "damage": "", "defense": "", "duration": ""}',
            '{"id": "", "name": "", "hp": "", "mana": "", "damage": "", "defense": "", "duration": ""}',
            '{"id": "", "name": "", "hp": "", "mana": "", "damage": "", "defense": "", "duration": ""}',
            '{"id": "", "name": "", "hp": "", "mana": "", "damage": "", "defense": "", "duration": ""}',
            1, 1, 1, 1, 3,
            '{"oneHand": 0, "twoHand": 0, "bow": 0, "stick": 0, "fire": 0, "water": 0, "land": 0, "air": 0, "regeneration": 0, "meditation": 0, "block": 0, "evade": 0}',
            array[1],
            '{}')
)
insert into unit (id, name, subject_type, status, action_end,
                    location_id,
                    hp, max_hp, mana, max_mana, damage, defense, evade, max_move_point,
                    weapon, head, hand, body, leg,
                    power, agility, endurance, magic, free_point,
                    unit_skill,
                    ability,
                    unit_fight_effect)
select d.id, d.name, d.subject_type, d.status, d.action_end,
            d.location_id,
            d.hp, d.max_hp, d.mana, d.max_mana, d.damage, d.defense, d.evade, d.max_move_point,
            d.weapon, d.head, d.hand, d.body, d.leg,
            d.power, d.agility, d.endurance, d.magic, d.free_point,
            d.unit_skill,
            d.ability,
            d.unit_fight_effect
from data d
where not exists (select 1
                  from unit u2
                  where u2.id = d.id);

--subject
with data(id, name,
            subject_type, apply_type, hit_type, range_type,
            hp, damage, defense, mana,
            distance, move_point, duration, description)
   as (values
        (1, 'Одноручный меч', 'WEAPON', 'ONE_HAND', 'NONE', 'ONE', 0, 0, 0, 0, 1, 0, 100, 'Простой одноручный меч'),
        (2, 'Двуручный меч', 'WEAPON', 'TWO_HAND', 'NONE', 'ONE', 0, 0, 0, 0, 2, 0, 100, 'Простой двуручный меч'),
        (3, 'Лук', 'WEAPON', 'BOW', 'NONE', 'ONE', 0, 0, 0, 0, 10, 0, 100, 'Простой лук'),
        (4, 'Посох', 'STICK', 'ONE_HAND', 'NONE', 'ONE', 0, 0, 0, 0, 1, 0, 100, 'Простой посох')
)
insert into subject (id, name,
                        subject_type, apply_type, hit_type, range_type,
                        hp, damage, defense, mana,
                        distance, move_point, duration, description)
select d.id, d.name,
            d.subject_type, d.apply_type, d.hit_type, d.range_type,
            d.hp, d.damage, d.defense, d.mana,
            d.distance, d.move_point, d.duration, d.description
from data d
where not exists (select 1
                  from subject u2
                  where u2.id = d.id);



