-- liquibase formatted sql

-- changeset gordey_dovydenko:1

insert into cosmetic_item (name, description, price, rarity_type, cosmetic_type, sellable)
    values ('Огненые пятки', 'Вы пришли к нам из ада', 1000, 'LEGENDARY', 'FOOTPRINT', true),
           ('Бензокос', 'Вы истребляете траву', 50, 'COMMON', 'FOG', true),
           ('Цыган', 'Пусть все завидуют вашему золоту', 500, 'EPIC', 'AVATAR_FRAMES', true),
           ('Уникум', 'Вау вы имеетее кастомною эконку приложения', 200, 'RARE', 'APPLICATION_IMAGE', true),
           ('Бог', 'Вы ходите по воде', 0, 'LEGENDARY', 'FOG', false);

-- rollback delete from cosmetic_item_id in (1, 2, 3, 4, 5);
