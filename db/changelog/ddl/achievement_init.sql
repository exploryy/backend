-- liquibase formatted sql

-- changeset gordey_dovydenko:1

insert into achievement (name, description)
    values ('Великий заходитель в приложение', 'Зайти в приложение');

-- rollback delete from achievement_id = 1;