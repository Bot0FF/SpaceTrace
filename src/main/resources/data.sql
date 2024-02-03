------locations
--with data(id, location_type, name, x, y)
--   as (values
--        (11, 'PLAIN', 'Равнина', 1, 1),
--        (12, 'PLAIN', 'Равнина', 1, 2),
--        (13, 'PLAIN', 'Равнина', 1, 3),
--        (14, 'PLAIN', 'Равнина', 1, 4),
--        (15, 'PLAIN', 'Равнина', 1, 5),
--        (16, 'PLAIN', 'Равнина', 1, 6),
--        (17, 'PLAIN', 'Равнина', 1, 7),
--        (18, 'PLAIN', 'Равнина', 1, 8),
--        (19, 'PLAIN', 'Равнина', 1, 9),
--
--        (21, 'PLAIN', 'Равнина', 2, 1),
--        (22, 'PLAIN', 'Равнина', 2, 2),
--        (23, 'PLAIN', 'Равнина', 2, 3),
--        (24, 'PLAIN', 'Равнина', 2, 4),
--        (25, 'PLAIN', 'Равнина', 2, 5),
--        (26, 'PLAIN', 'Равнина', 2, 6),
--        (27, 'PLAIN', 'Равнина', 2, 7),
--        (28, 'PLAIN', 'Равнина', 2, 8),
--        (29, 'PLAIN', 'Равнина', 2, 9),
--
--        (31, 'PLAIN', 'Равнина', 3, 1),
--        (32, 'PLAIN', 'Равнина', 3, 2),
--        (33, 'PLAIN', 'Равнина', 3, 3),
--        (34, 'PLAIN', 'Равнина', 3, 4),
--        (35, 'PLAIN', 'Равнина', 3, 5),
--        (36, 'PLAIN', 'Равнина', 3, 6),
--        (37, 'PLAIN', 'Равнина', 3, 7),
--        (38, 'PLAIN', 'Равнина', 3, 8),
--        (39, 'PLAIN', 'Равнина', 3, 9),
--
--        (41, 'PLAIN', 'Равнина', 4, 1),
--        (42, 'PLAIN', 'Равнина', 4, 2),
--        (43, 'PLAIN', 'Равнина', 4, 3),
--        (44, 'PLAIN', 'Равнина', 4, 4),
--        (45, 'PLAIN', 'Равнина', 4, 5),
--        (46, 'PLAIN', 'Равнина', 4, 6),
--        (47, 'PLAIN', 'Равнина', 4, 7),
--        (48, 'PLAIN', 'Равнина', 4, 8),
--        (49, 'PLAIN', 'Равнина', 4, 9),
--
--        (51, 'PLAIN', 'Равнина', 5, 1),
--        (52, 'PLAIN', 'Равнина', 5, 2),
--        (53, 'PLAIN', 'Равнина', 5, 3),
--        (54, 'PLAIN', 'Равнина', 5, 4),
--        (55, 'PLAIN', 'Равнина', 5, 5),
--        (56, 'PLAIN', 'Равнина', 5, 6),
--        (57, 'PLAIN', 'Равнина', 5, 7),
--        (58, 'PLAIN', 'Равнина', 5, 8),
--        (59, 'PLAIN', 'Равнина', 5, 9),
--
--        (61, 'PLAIN', 'Равнина', 6, 1),
--        (62, 'PLAIN', 'Равнина', 6, 2),
--        (63, 'PLAIN', 'Равнина', 6, 3),
--        (64, 'PLAIN', 'Равнина', 6, 4),
--        (65, 'PLAIN', 'Равнина', 6, 5),
--        (66, 'PLAIN', 'Равнина', 6, 6),
--        (67, 'PLAIN', 'Равнина', 6, 7),
--        (68, 'PLAIN', 'Равнина', 6, 8),
--        (69, 'PLAIN', 'Равнина', 6, 9),
--
--        (71, 'PLAIN', 'Равнина', 7, 1),
--        (72, 'PLAIN', 'Равнина', 7, 2),
--        (73, 'PLAIN', 'Равнина', 7, 3),
--        (74, 'PLAIN', 'Равнина', 7, 4),
--        (75, 'PLAIN', 'Равнина', 7, 5),
--        (76, 'PLAIN', 'Равнина', 7, 6),
--        (77, 'PLAIN', 'Равнина', 7, 7),
--        (78, 'PLAIN', 'Равнина', 7, 8),
--        (79, 'PLAIN', 'Равнина', 7, 9),
--
--        (81, 'PLAIN', 'Равнина', 8, 1),
--        (82, 'PLAIN', 'Равнина', 8, 2),
--        (83, 'PLAIN', 'Равнина', 8, 3),
--        (84, 'PLAIN', 'Равнина', 8, 4),
--        (85, 'PLAIN', 'Равнина', 8, 5),
--        (86, 'PLAIN', 'Равнина', 8, 6),
--        (87, 'PLAIN', 'Равнина', 8, 7),
--        (88, 'PLAIN', 'Равнина', 8, 8),
--        (89, 'PLAIN', 'Равнина', 8, 9),
--
--        (91, 'PLAIN', 'Равнина', 9, 1),
--        (92, 'PLAIN', 'Равнина', 9, 2),
--        (93, 'PLAIN', 'Равнина', 9, 3),
--        (94, 'PLAIN', 'Равнина', 9, 4),
--        (95, 'PLAIN', 'Равнина', 9, 5),
--        (96, 'PLAIN', 'Равнина', 9, 6),
--        (97, 'PLAIN', 'Равнина', 9, 7),
--        (98, 'PLAIN', 'Равнина', 9, 8),
--        (99, 'PLAIN', 'Равнина', 9, 9)
--)
--insert into location (id, location_type, name, x, y)
--select d.id, d.location_type, d.name, d.x, d.y
--from data d
--where not exists (select 1
--                  from subject u2
--                  where u2.id = d.id);

--unit
with data(id, name, unit_type, status, action_end, location, hp, max_hp, mana, max_mana, damage, defense, ability, unit_json)
   as (values
        (1, 'user', 'USER', 'ACTIVE', false, 22, 20, 20, 20, 20, 8, 4, array[1, 2, 3],  '{}')
)
insert into unit (id, name, unit_type, status, action_end, location, hp, max_hp, mana, max_mana, damage, defense, ability, unit_json)
select d.id, d.name, d.unit_type, d.status, d.action_end, d.location, d.hp, d.max_hp, d.mana, d.max_mana, d.damage, d.defense, d.ability, d.unit_json
from data d
where not exists (select 1
                  from unit u2
                  where u2.id = d.id);

--subject
with data(id, subject_type, name, hp, damage, defense, mana, duration, description, apply_type, hit_type)
   as (values
        (1, 'ABILITY', 'Обычная атака', 0, 4, 0, 0, 0, 'Простая атака, наносящая урон, равный базовому урону игрока', 'SINGLE', 'DAMAGE'),
 	    (2, 'ABILITY', 'Малое лечение +5', 5, 0, 0, 0, 0, 'Разовое восстановление здоровья на 5 единиц', 'SINGLE', 'RECOVERY'),
 	    (3, 'ABILITY', 'Повышение здоровья +10', 100, 0, 0, 0, 3, 'Повышение максимального уровня здоровья на 10 единиц на 3 раунда', 'SINGLE', 'BOOST')
)
insert into subject (id, subject_type, name, hp, damage, defense, mana, duration, description, apply_type, hit_type)
select d.id, d.subject_type, d.name, d.hp, d.damage, d.defense, d.mana, d.duration, d.description, d.apply_type, d.hit_type
from data d
where not exists (select 1
                  from subject u2
                  where u2.id = d.id);



