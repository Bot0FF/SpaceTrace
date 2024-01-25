--unit
with data(id, name, unit_type, status, x, y, location, hp, mana, damage, ability, action_end)
   as (values
        (1, 'user', 'USER', 'ACTIVE', 2, 2, 22, 20, 20, 8, array[1, 2], false)
)
insert into unit (id, name, unit_type, status, x, y, location, hp, mana, damage, ability, action_end)
select d.id, d.name, d.unit_type, d.status, d.x, d.y, d.location, d.hp, d.mana, d.damage, d.ability, d.action_end
from data d
where not exists (select 1
                  from unit u2
                  where u2.id = d.id);

--subject
with data(id, subject_type, name, hp, damage, defense, mana, description, apply_type)
   as (values
        (1, 'ABILITY', 'Обычная атака', 0, 4, 0, 0, 'Обычная атака', 'OPPONENT'),
 	    (2, 'ABILITY', 'Малое лечение', 5, 0, 0, 0, 'Малое лечение', 'ALL_ALLIES')
)
insert into subject (id, subject_type, name, hp, damage, defense, mana, description, apply_type)
select d.id, d.subject_type, d.name, d.hp, d.damage, d.defense, d.mana, d.description, d.apply_type
from data d
where not exists (select 1
                  from subject u2
                  where u2.id = d.id);