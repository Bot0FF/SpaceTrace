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
            7, 7, 4, 4,
            1, 1, 1, 1, 1, 0,
            '{"oneHand": 1, "twoHand": 1, "bow": 1, "fire": 1, "water": 1, "land": 1, "air": 1, "vitality": 1, "spirituality": 1, "regeneration": 1, "meditation": 1, "block": 1, "evade": 1}',
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
            20, 200, 4, 4,
            1, 1, 1, 1, 1, 0,
            '{"oneHand": 1, "twoHand": 1, "bow": 1, "fire": 1, "water": 1, "land": 1, "air": 1, "vitality": 1, "spirituality": 1, "regeneration": 1, "meditation": 1, "block": 1, "evade": 1}',
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
            one_hand, two_hand, bow, fire, water, land, air, vitality, spirituality, regeneration, meditation, evade, block,
            price, description)
   as (values
        (1, 'Одноручный меч', 'WEAPON', 'ONE_HAND',
        10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 100,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 'Простой одноручный меч'),
        (2, 'Лук', 'WEAPON', 'BOW',
        20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 100,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 'Простой лук')
)
insert into objects (id, name, object_type, skill_type,
            phys_damage, mag_modifier, hp, mana, phys_defense, mag_defense, strength, intelligence, dexterity, endurance, luck, distance, condition,
            one_hand, two_hand, bow, fire, water, land, air, vitality, spirituality, regeneration, meditation, evade, block,
            price, description)
select d.id, d.name, d.object_type, d.skill_type,
            d.phys_damage, d.mag_modifier, d.hp, d.mana, d.phys_defense, d.mag_defense, d.strength, d.intelligence, d.dexterity, d.endurance, d.luck, d.distance, d.condition,
            d.one_hand, d.two_hand, d.bow, d.fire, d.water, d.land, d.air, d.vitality, d.spirituality, d.regeneration, d.meditation, d.evade, d.block,
            d.price, d.description
from data d
where not exists (select 1
                  from objects u2
                  where u2.id = d.id);

--ability
with data(id, name, skill_type, apply_type, range_type,
            phys_damage, mag_damage,
            phys_effect, mag_effect, hp, mana, phys_defense, mag_defense,
            strength, intelligence, dexterity, endurance, luck, initiative, block, evade,
            distance, point_action, duration, mana_cost,
            price, description)
   as (values
            (1, 'макс хп +10', 'FIRE', 'BOOST', 'ONE',
        0, 0,
        0, 0, 10, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (2, 'макс хп -10', 'FIRE', 'LOWER', 'ONE',
        0, 0,
        0, 0, -10, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (3, 'физ урон +10', 'FIRE', 'BOOST', 'ONE',
        0, 0,
        10, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (4, 'физ урон -10', 'FIRE', 'LOWER', 'ONE',
        0, 0,
        -10, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (5, 'физ защита +10', 'FIRE', 'BOOST', 'ONE',
        0, 0,
        0, 10, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, ''),
            (6, 'физ защита -10', 'FIRE', 'LOWER', 'ONE',
        0, 0,
        0, -10, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 2, 2, 5,
        0, '')
)
insert into ability (id, name, skill_type, apply_type, range_type,
            phys_damage, mag_damage,
            phys_effect, mag_effect, hp, mana, phys_defense, mag_defense,
            strength, intelligence, dexterity, endurance, luck, initiative, block, evade,
            distance, point_action, duration, mana_cost,
            price, description)
select d.id, d.name, d.skill_type, d.apply_type, d.range_type,
            d.phys_damage, d.mag_damage,
            d.phys_effect, d.mag_effect, d.hp, d.mana, d.phys_defense, d.mag_defense,
            d.strength, d.intelligence, d.dexterity, d.endurance, d.luck, d.initiative, d.block, d.evade,
            d.distance, d.point_action, d.duration, d.mana_cost,
            d.price, description
from data d
where not exists (select 1
                  from ability u2
                  where u2.id = d.id);


