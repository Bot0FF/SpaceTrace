--locations
with data(id, location_type, name, coordinate, ais, units, things, is_world, locality_id, door_id, locality_name)
   as (values
        (11, 'plain', 'Равнина', 'X:1/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (12, 'plain', 'Равнина', 'X:1/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (13, 'plain', 'Равнина', 'X:1/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (14, 'plain', 'Равнина', 'X:1/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (15, 'plain', 'Равнина', 'X:1/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (16, 'plain', 'Равнина', 'X:1/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (17, 'plain', 'Равнина', 'X:1/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (18, 'plain', 'Равнина', 'X:1/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (19, 'plain', 'Равнина', 'X:1/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (21, 'plain', 'Равнина', 'X:2/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (22, 'plain', 'Равнина', 'X:2/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (23, 'plain', 'Равнина', 'X:2/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (24, 'plain', 'Равнина', 'X:2/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (25, 'plain', 'Равнина', 'X:2/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (26, 'plain', 'Равнина', 'X:2/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (27, 'plain', 'Равнина', 'X:2/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (28, 'plain', 'Равнина', 'X:2/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (29, 'plain', 'Равнина', 'X:2/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (31, 'plain', 'Равнина', 'X:3/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (32, 'plain', 'Равнина', 'X:3/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (33, 'plain', 'Равнина', 'X:3/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (34, 'plain', 'Равнина', 'X:3/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (35, 'plain', 'Равнина', 'X:3/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (36, 'plain', 'Равнина', 'X:3/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (37, 'plain', 'Равнина', 'X:3/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (38, 'plain', 'Равнина', 'X:3/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (39, 'plain', 'Равнина', 'X:3/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (41, 'plain', 'Равнина', 'X:4/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (42, 'plain', 'Равнина', 'X:4/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (43, 'plain', 'Равнина', 'X:4/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (44, 'plain', 'Равнина', 'X:4/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (45, 'plain', 'Равнина', 'X:4/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (46, 'plain', 'Равнина', 'X:4/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (47, 'plain', 'Равнина', 'X:4/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (48, 'plain', 'Равнина', 'X:4/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (49, 'plain', 'Равнина', 'X:4/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (51, 'plain', 'Равнина', 'X:5/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (52, 'plain', 'Равнина', 'X:5/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (53, 'plain', 'Равнина', 'X:5/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (54, 'plain', 'Равнина', 'X:5/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (55, 'plain', 'Равнина', 'X:5/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 1011, 0, 'Город'),
        (56, 'plain', 'Равнина', 'X:5/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (57, 'plain', 'Равнина', 'X:5/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (58, 'plain', 'Равнина', 'X:5/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (59, 'plain', 'Равнина', 'X:5/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (61, 'plain', 'Равнина', 'X:6/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (62, 'plain', 'Равнина', 'X:6/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (63, 'plain', 'Равнина', 'X:6/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (64, 'plain', 'Равнина', 'X:6/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (65, 'plain', 'Равнина', 'X:6/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (66, 'plain', 'Равнина', 'X:6/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (67, 'plain', 'Равнина', 'X:6/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (68, 'plain', 'Равнина', 'X:6/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (69, 'plain', 'Равнина', 'X:6/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (71, 'plain', 'Равнина', 'X:7/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (72, 'plain', 'Равнина', 'X:7/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (73, 'plain', 'Равнина', 'X:7/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (74, 'plain', 'Равнина', 'X:7/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (75, 'plain', 'Равнина', 'X:7/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (76, 'plain', 'Равнина', 'X:7/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (77, 'plain', 'Равнина', 'X:7/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (78, 'plain', 'Равнина', 'X:7/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (79, 'plain', 'Равнина', 'X:7/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (81, 'plain', 'Равнина', 'X:8/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (82, 'plain', 'Равнина', 'X:8/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (83, 'plain', 'Равнина', 'X:8/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (84, 'plain', 'Равнина', 'X:8/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (85, 'plain', 'Равнина', 'X:8/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (86, 'plain', 'Равнина', 'X:8/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (87, 'plain', 'Равнина', 'X:8/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (88, 'plain', 'Равнина', 'X:8/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (89, 'plain', 'Равнина', 'X:8/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        (91, 'plain', 'Равнина', 'X:9/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (92, 'plain', 'Равнина', 'X:9/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (93, 'plain', 'Равнина', 'X:9/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (94, 'plain', 'Равнина', 'X:9/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (95, 'plain', 'Равнина', 'X:9/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (96, 'plain', 'Равнина', 'X:9/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (97, 'plain', 'Равнина', 'X:9/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (98, 'plain', 'Равнина', 'X:9/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),
        (99, 'plain', 'Равнина', 'X:9/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], true, 0, 0, ''),

        --Город
        (1011, 'urban', 'Город', 'X:1/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 55, 0, 'Равнина'),
        (1012, 'church', 'Город', 'X:1/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 1, 'Храм'),
        (1013, 'urban', 'Город', 'X:1/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1014, 'urban', 'Город', 'X:1/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1015, 'urban', 'Город', 'X:1/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1016, 'urban', 'Город', 'X:1/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1017, 'urban', 'Город', 'X:1/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1018, 'urban', 'Город', 'X:1/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1019, 'urban', 'Город', 'X:1/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),

        (1021, 'urban', 'Город', 'X:2/Y:1', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1022, 'urban', 'Город', 'X:2/Y:2', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1023, 'urban', 'Город', 'X:2/Y:3', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1024, 'urban', 'Город', 'X:2/Y:4', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1025, 'urban', 'Город', 'X:2/Y:5', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1026, 'urban', 'Город', 'X:2/Y:6', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1027, 'urban', 'Город', 'X:2/Y:7', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1028, 'urban', 'Город', 'X:2/Y:8', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, ''),
        (1029, 'urban', 'Город', 'X:2/Y:9', ARRAY[]::bigint[], ARRAY[]::bigint[], ARRAY[]::bigint[], false, 0, 0, '')
)
insert into location (id, location_type, name, coordinate, ais, units, things, is_world, locality_id, door_id, locality_name)
select d.id, d.location_type, d.name, d.coordinate, d.ais, d.units, d.things, d.is_world, d.locality_id, d.door_id, d.locality_name
from data d
where not exists (select 1
                  from location u2
                  where u2.id = d.id);

--unit
with data(id, name, unit_type, status, action_end,
            location_id,
            hp, mana, point_action, max_point_action,
            strength, intelligence, dexterity, endurance, luck, bonus_point,
            unit_skill,
            current_ability, all_ability,
            weapon, head, hand, body, leg)
   as (values
        (1, 'user', 'ADMIN', 'ACTIVE', false,
            22,
            7, 7, 2, 2,
            1, 1, 1, 1, 1, 0,
            '{"oneHand": 1, "twoHand": 1, "shoot": 1, "fire": 1, "water": 1, "land": 1, "air": 1, "vitality": 1, "spirituality": 1, "regeneration": 1, "meditation": 1, "block": 1, "evade": 1}',
            ARRAY[]::integer[], ARRAY[]::integer[],
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}'
)
)
insert into unit (id, name, unit_type, status, action_end,
            location_id,
            hp, mana, point_action, max_point_action,
            strength, intelligence, dexterity, endurance, luck, bonus_point,
            unit_skill,
            current_ability, all_ability,
            weapon, head, hand, body, leg)
select d.id, d.name, d.unit_type, d.status, d.action_end,
            d.location_id,
            d.hp, d.mana, d.point_action, d.max_point_action,
            d.strength, d.intelligence, d.dexterity, d.endurance, d.luck, d.bonus_point,
            d.unit_skill,
            d.current_ability, d.all_ability,
            d.weapon, d.head, d.hand, d.body, d.leg
from data d
where not exists (select 1
                  from unit u2
                  where u2.id = d.id);

--базовые aiUnit
with data(id, name, unit_type, status, action_end,
            location_id,
            hp, mana, point_action, max_point_action,
            strength, intelligence, dexterity, endurance, luck, bonus_point,
            unit_skill,
            current_ability, all_ability,
            weapon, head, hand, body, leg)
   as (values
        (1, 'Гусеница', 'AI', 'ACTIVE', false,
            0,
            20, 200, 2, 2,
            1, 1, 1, 1, 1, 0,
            '{"oneHand": 1, "twoHand": 1, "shoot": 1, "fire": 1, "water": 1, "land": 1, "air": 1, "vitality": 1, "spirituality": 1, "regeneration": 1, "meditation": 1, "block": 1, "evade": 1}',
            ARRAY[]::integer[], ARRAY[]::integer[],
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}',
            '{"id": "0", "name": "", "objectType": "", "skillType": "", "physDamage": "0", "magModifier": "0", "hp": "0", "mana": "0", "physDefense": "0", "magDefense": "0", "strength": 0, "intelligence": 0, "dexterity": 0, "endurance": 0, "luck": 0, "distance": "0", "condition": "0"}'
)
)
insert into ai (id, name, unit_type, status, action_end,
            location_id,
            hp, mana, point_action, max_point_action,
            strength, intelligence, dexterity, endurance, luck, bonus_point,
            unit_skill,
            current_ability, all_ability,
            weapon, head, hand, body, leg)
select d.id, d.name, d.unit_type, d.status, d.action_end,
            d.location_id,
            d.hp, d.mana, d.point_action, d.max_point_action,
            d.strength, d.intelligence, d.dexterity, d.endurance, d.luck, d.bonus_point,
            d.unit_skill,
            d.current_ability, d.all_ability,
            d.weapon, d.head, d.hand, d.body, d.leg
from data d
where not exists (select 1
                  from ai u2
                  where u2.id = d.id);

--базовые объекты
with data(id, name, object_type, skill_type,
            phys_damage, mag_modifier, hp, mana, phys_defense, mag_defense, strength, intelligence, dexterity, endurance, luck, distance, condition,
            one_hand, two_hand, shoot, fire, water, land, air, vitality, spirituality, regeneration, meditation, evade, block,
            price, description)
   as (values
        (1, 'Одноручный меч', 'WEAPON', 'ONE_HAND',
        10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 100,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 'Простой одноручный меч'),
        (2, 'Лук', 'WEAPON', 'SHOOT',
        20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 100,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 'Простой лук')
)
insert into objects (id, name, object_type, skill_type,
            phys_damage, mag_modifier, hp, mana, phys_defense, mag_defense, strength, intelligence, dexterity, endurance, luck, distance, condition,
            one_hand, two_hand, shoot, fire, water, land, air, vitality, spirituality, regeneration, meditation, evade, block,
            price, description)
select d.id, d.name, d.object_type, d.skill_type,
            d.phys_damage, d.mag_modifier, d.hp, d.mana, d.phys_defense, d.mag_defense, d.strength, d.intelligence, d.dexterity, d.endurance, d.luck, d.distance, d.condition,
            d.one_hand, d.two_hand, d.shoot, d.fire, d.water, d.land, d.air, d.vitality, d.spirituality, d.regeneration, d.meditation, d.evade, d.block,
            d.price, d.description
from data d
where not exists (select 1
                  from objects u2
                  where u2.id = d.id);

--ability
with data(id, name, skill_type, apply_type, range_type,
            mag_damage, phys_effect, mag_effect, hp, mana, phys_defense, mag_defense,
            strength, intelligence, dexterity, endurance, luck, initiative, block, evade,
            distance, point_action, duration, mana_cost,
            price, description)
   as (values
            (1, 'макс хп +10', 'FIRE', 'BOOST', 'ONE',
        0, 0, 0, 10, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (2, 'макс хп -10', 'FIRE', 'LOWER', 'ONE',
        0, 0, 0, -10, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (3, 'физ урон +10', 'FIRE', 'BOOST', 'ONE',
        0, 10, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (4, 'физ урон -10', 'FIRE', 'LOWER', 'ONE',
        0, -10, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (5, 'физ защита +10', 'FIRE', 'BOOST', 'ONE',
        0, 0, 10, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (6, 'физ защита -10', 'FIRE', 'LOWER', 'ONE',
        0, 0, -10, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, '')
)
insert into ability (id, name, skill_type, apply_type, range_type,
            mag_damage, phys_effect, mag_effect, hp, mana, phys_defense, mag_defense,
            strength, intelligence, dexterity, endurance, luck, initiative, block, evade,
            distance, point_action, duration, mana_cost,
            price, description)
select d.id, d.name, d.skill_type, d.apply_type, d.range_type,
            d.mag_damage, d.phys_effect, d.mag_effect, d.hp, d.mana, d.phys_defense, d.mag_defense,
            d.strength, d.intelligence, d.dexterity, d.endurance, d.luck, d.initiative, d.block, d.evade,
            d.distance, d.point_action, d.duration, d.mana_cost,
            d.price, description
from data d
where not exists (select 1
                  from ability u2
                  where u2.id = d.id);


