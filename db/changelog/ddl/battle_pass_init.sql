-- liquibase formatted sql

-- changeset gordey_dovydenko:1

insert into battle_pass(name, description, start_date, end_date)
VALUES ('Первый сезон', 'Первый сезон батл пасса', '2024-06-01 00:00:00.000', '2024-08-01 00:00:00.000');

insert into battle_pass_level (level, experience, battle_pass_id)
VALUES (1, 0, 1),
       (2, 100, 1),
       (3, 200, 1),
       (4, 300, 1),
       (5, 400, 1),
       (6, 500, 1),
       (7, 600, 1),
       (8, 700, 1),
       (9, 800, 1),
       (10, 900, 1);

insert into item_battle_pass_level (item_id, level, battle_pass_id)
VALUES (5, 10, 1),
       (9, 2, 1),
       (11, 5, 1);

-- rollback delete from battle_pass_id = 1;