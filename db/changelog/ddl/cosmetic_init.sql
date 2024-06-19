-- liquibase formatted sql

-- changeset gordey_dovydenko:1

insert into cosmetic_item (name, description, price, rarity_type, cosmetic_type, sellable)
values ('Огненые пятки', 'Вы пришли к нам из ада', 1000, 'LEGENDARY', 'FOOTPRINT', true),
       ('Бензокос', 'Вы истребляете траву', 50, 'COMMON', 'FOG', true),
       ('Цыган', 'Пусть все завидуют вашему золоту', 500, 'EPIC', 'AVATAR_FRAMES', true),
       ('Уникум', 'Вау вы имеетее кастомную иконку приложения', 200, 'RARE', 'APPLICATION_IMAGE', true),
       ('Бог', 'Вы ходите по воде', 0, 'LEGENDARY', 'FOG', false),
       ('Божественный дар', 'С такими квестами не далеко и до могилы', 1500, 'EPIC', 'FOG', true),
       ('Ho ho ho', 'Let It Snow! Let It Snow! Let It Snow!', 800, 'RARE', 'FOG', true);

-- rollback delete from cosmetic_item_id in (1, 2, 3, 4, 5, 6, 7);

-- changeset gordey_dovydenko:2

insert into cosmetic_item (name, description, price, rarity_type, cosmetic_type, sellable)
values ('mmorpg рамка', 'Все и так знаю что ты не вылазишь из телефона 24/7', 400, 'LEGENDARY', 'AVATAR_FRAMES', true),
       ('Геншин импакт вумен рамка', 'Look at me... just... once... more...', 600, 'RARE', 'AVATAR_FRAMES', false),
       ('Геншин импакт мэн рамка', 'Блин я вообще не знаю что тут можно написать.', 100, 'COMMON', 'AVATAR_FRAMES', true),
       ('Опять двойка', 'Твоя оценка по матеше, уникум', 800, 'RARE', 'APPLICATION_IMAGE', false),
       ('Главный спонсор', 'Авиасейлс сервис доступных авиабилетов', 1, 'EPIC', 'APPLICATION_IMAGE', true);

-- rollback delete from cosmetic_item_id in (8, 9, 10, 11, 12);