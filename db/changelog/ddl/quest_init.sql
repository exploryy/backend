-- liquibase formatted sql

-- changeset gordey_dovydenko:1

insert into point_route (longitude, latitude, next_longitude, next_latitude)
    values ('84.937810', '56.470569', '84.939779', '56.470287'),
           ('84.939779', '56.470287', '84.941356', '56.470157'),
           ('84.941356', '56.470157', '84.940758', '56.467706'),
           ('84.940758', '56.467706', '84.939986', '56.464763'),
           ('84.939986', '56.464763', '84.939546', '56.462596'),
           ('84.939546', '56.462596', null, null);

insert into route (distance, point_latitude, point_longitude)
    values (1000,'56.470569', '84.937810');

insert into quest (name, description, difficulty_type, quest_type, transport_type)
    values ('Пойдем бухнем', 'Давайте сходим и прибухнем', 'EASY', 'POINT_TO_POINT', 'WALK'),
           ('Побег из томска', 'Ран Вася ран ни оставим шанса Томичам', 'HARD', 'DISTANCE', 'BICYCLE');

insert into quest_photo (quest_id)
    values (1),
           (1),
           (1),
           (1),
           (2);

insert into point_to_point_quest (quest_id, route_id)
    values (1, 1);

insert into distance_quest (quest_id, route_distance, longitude, latitude)
    values (2, 1000, '84.937013', '56.470304');

-- rollback delete from route_id = 1;