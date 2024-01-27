--unit
with data(id, name, unit_type, status, action_end, x, y, location, hp, mana, damage, defense, ability, unit_json)
   as (values
        (1, 'user', 'USER', 'ACTIVE', false, 2, 2, 22, 20, 20, 8, 4, array[1, 2], '{}')
)
insert into unit (id, name, unit_type, status, action_end, x, y, location, hp, mana, damage, defense, ability, unit_json)
select d.id, d.name, d.unit_type, d.status, d.action_end, d.x, d.y, d.location, d.hp, d.mana, d.damage, d.defense, d.ability, d.unit_json
from data d
where not exists (select 1
                  from unit u2
                  where u2.id = d.id);

--subject
with data(id, subject_type, name, hp, damage, defense, mana, description, apply_type, hit_type)
   as (values
        (1, 'ABILITY', 'Обычная атака', 0, 4, 0, 0, 'Обычная атака', 'OPPONENT', 'DAMAGE'),
 	    (2, 'ABILITY', 'Малое лечение', 5, 0, 0, 0, 'Малое лечение', 'ALL_ALLIES', 'RECOVERY')
)
insert into subject (id, subject_type, name, hp, damage, defense, mana, description, apply_type, hit_type)
select d.id, d.subject_type, d.name, d.hp, d.damage, d.defense, d.mana, d.description, d.apply_type, d.hit_type
from data d
where not exists (select 1
                  from subject u2
                  where u2.id = d.id);