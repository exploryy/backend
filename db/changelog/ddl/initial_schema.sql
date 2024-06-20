-- liquibase formatted sql

-- changeset gordey_dovydenko:1

create sequence s_quest_id start with 1 increment by 1;

create table quest
(
    quest_id        bigint default nextval('s_quest_id'),
    name            varchar(300)                                                        not null,
    description     varchar                                                             not null,
    difficulty_type varchar(20) check ( difficulty_type in ('EASY', 'MEDIUM', 'HARD') ) not null,
    quest_type      varchar(20) check ( quest_type in ('POINT_TO_POINT', 'DISTANCE') )  not null,
    transport_type  varchar(20) check ( transport_type in ('WALK', 'BICYCLE', 'CAR') )  not null,
    primary key (quest_id)
);

-- rollback DROP TABLE quest;
-- rollback DROP SEQUENCE s_quest_id;

-- changeset gordey_dovydenko:2

create sequence s_route_id start with 1 increment by 1;

create table route
(
    route_id bigint default nextval('s_route_id'),
    distance double precision not null,
    primary key (route_id)
);

create sequence s_point_id start with 1 increment by 1;

CREATE TABLE points
(
    point_id bigint default nextval('s_point_id'),
    lat      varchar(60) NOT NULL,
    lon      varchar(60) NOT NULL,
    PRIMARY KEY (point_id)
);

CREATE TABLE route_points
(
    route_id bigint  NOT NULL,
    point_id bigint  NOT NULL,
    number   INTEGER NOT NULL,
    PRIMARY KEY (route_id, point_id, number),
    FOREIGN KEY (route_id) REFERENCES route (route_id),
    FOREIGN KEY (point_id) REFERENCES points (point_id)
);

-- rollback DROP TABLE points;
-- rollback DROP SEQUENCE s_point_id
-- rollback DROP TABLE route_points;
-- rollback DROP TABLE route;

-- changeset gordey_dovydenko:3

create sequence s_pass_quest_id start with 1 increment by 1;

create table pass_quest
(
    pass_quest_id  bigint default nextval('s_pass_quest_id'),
    quest_id       bigint                                                             not null,
    client_id      varchar(60)                                                        not null,
    start_time     timestamp                                                          not null,
    end_time       timestamp,
    route_id       bigint,
    transport_type varchar(20) check ( transport_type in ('WALK', 'BICYCLE', 'CAR') ) not null,
    primary key (pass_quest_id),
    foreign key (quest_id) references quest (quest_id),
    foreign key (route_id) references route (route_id)
);

-- rollback DROP TABLE pass_quest;
-- rollback DROP SEQUENCE s_quest_point_id;

-- changeset gordey_dovydenko:4

create sequence s_quest_review_id start with 1 increment by 1;

create table quest_review
(
    quest_review_id bigint default nextval('s_quest_review_id'),
    score           integer check ( score >= 1 and score <= 5 ) not null,
    message         varchar(255)                                not null,
    client_id       varchar(60)                                 not null,
    quest_id        bigint                                      not null,
    primary key (quest_review_id),
    foreign key (quest_id) references quest (quest_id)
);

-- rollback DROP TABLE quest_review;
-- rollback DROP SEQUENCE s_quest_review_id;

-- changeset gordey_dovydenko:5

create sequence s_review_photo_id start with 1 increment by 1;

create table review_photo
(
    photo_id  bigint default nextval('s_review_photo_id'),
    review_id bigint not null,
    primary key (photo_id),
    foreign key (review_id) references quest_review (quest_review_id)
);

-- rollback DROP TABLE review_photo;
-- rollback DROP SEQUENCE s_review_photo_id;

-- changeset gordey_dovydenko:6

create sequence s_quest_photo_id start with 1 increment by 1;

create table quest_photo
(
    photo_id bigint default nextval('s_quest_photo_id'),
    quest_id bigint not null,
    primary key (photo_id),
    foreign key (quest_id) references quest (quest_id)
);

-- rollback DROP TABLE quest_photo;
-- rollback DROP SEQUENCE s_quest_photo_id;

-- changeset gordey_dovydenko:7

create table point_to_point_quest
(
    quest_id bigint not null,
    route_id bigint not null,
    primary key (quest_id),
    foreign key (quest_id) references quest (quest_id),
    foreign key (route_id) references route (route_id)
);

-- rollback DROP TABLE point_to_point_quest;

-- changeset gordey_dovydenko:8

create table distance_quest
(
    quest_id       bigint           not null,
    route_distance double precision not null,
    latitude       varchar(60)      not null,
    longitude      varchar(60)      not null,
    primary key (quest_id),
    foreign key (quest_id) references quest (quest_id)
);

-- rollback DROP TABLE distance_quest;

-- changeset gordey_dovydenko:9

create sequence s_achievement_id start with 1 increment by 1;

create table achievement
(
    achievement_id bigint default nextval('s_achievement_id'),
    name           varchar(60)  not null,
    description    varchar(255) not null,
    primary key (achievement_id)
);

-- rollback DROP TABLE achievement;

-- changeset gordey_dovydenko:10

create table client_achievement
(
    client_id        varchar(60)              not null,
    achievement_id   bigint                   not null,
    achievement_date timestamp with time zone not null,
    primary key (client_id, achievement_id),
    foreign key (achievement_id) references achievement (achievement_id)
);

-- rollback DROP TABLE client_achievement;

-- changeset gordey_dovydenko:11

create table friend
(
    client_id   varchar(60) not null,
    friend_id   varchar(60) not null,
    is_favorite boolean     not null,
    primary key (client_id, friend_id)
);

-- rollback DROP TABLE friend;

-- changeset t9404:12

create extension postgis;

create table multipolygon
(
    client_id varchar(255) not null,
    geom      geometry(MultiPolygon, 4326),
    primary key (client_id)
);

-- rollback DROP TABLE multipolygon;
-- rollback DROP EXTENSION postgis;

-- changeset gordey_dovydenko:13

create table friend_request
(
    client_id varchar(60) not null,
    friend_id varchar(60) not null,
    primary key (client_id, friend_id)
);

-- rollback DROP TABLE friend_request;

-- changeset t9404:14

create sequence s_coin_id start with 1 increment by 1;

create table coins
(
    coin_id   bigint               default nextval('s_coin_id'),
    value     integer     not null,
    latitude  varchar(60) not null,
    longitude varchar(60) not null,
    taken     boolean     not null default false,
    client_id varchar(60) not null
);

CREATE INDEX index_client_id_coin ON coins (client_id);

-- rollback DROP TABLE coins;

-- changeset t9404:15

create table user_location
(
    client_id       varchar(60)              not null,
    latitude        varchar(60)              not null,
    longitude       varchar(60)              not null,
    last_visitation timestamp with time zone not null default current_timestamp,
    primary key (client_id)
);

-- rollback DROP TABLE user_location


-- changeset gordey_dovydenko:16

create sequence s_cosmetic_item_id start with 1 increment by 1;

create table cosmetic_item
(
    item_id       bigint default nextval('s_cosmetic_item_id'),
    name          varchar(60)                                                                  not null,
    description   varchar(255)                                                                 not null,
    price         integer                                                                      not null,
    sellable      boolean                                                                      not null,
    rarity_type   varchar(20) check ( rarity_type in ('COMMON', 'RARE', 'EPIC', 'LEGENDARY') ) not null,
    cosmetic_type varchar(20) check ( cosmetic_type in ('FOOTPRINT', 'AVATAR_FRAMES', 'APPLICATION_IMAGE',
                                                        'FOG') )                               not null,
    primary key (item_id)
);

-- rollback DROP TABLE cosmetic_item;
-- rollback DROP SEQUENCE s_cosmetic_item_id;

-- changeset gordey_dovydenko:17

create table client_money
(
    client_id varchar(60) not null,
    money     integer     not null,
    primary key (client_id)
);

-- rollback DROP TABLE client_money;

-- changeset gordey_dovydenko:18

create sequence s_battle_pass_id start with 1 increment by 1;

create table battle_pass
(
    battle_pass_id bigint default nextval('s_battle_pass_id'),
    name           varchar(60)  not null,
    description    varchar(255) not null,
    start_date     timestamp    not null,
    end_date       timestamp    not null,
    primary key (battle_pass_id)
);

create table battle_pass_level
(
    level          integer not null,
    battle_pass_id bigint  not null,
    experience     integer not null,
    primary key (level, battle_pass_id),
    foreign key (battle_pass_id) references battle_pass (battle_pass_id)
);

create table item_battle_pass_level
(
    item_id        bigint  not null,
    level          integer not null,
    battle_pass_id bigint  not null,
    primary key (item_id, level, battle_pass_id),
    foreign key (item_id) references cosmetic_item (item_id),
    foreign key (level, battle_pass_id) references battle_pass_level (level, battle_pass_id)
);

create table client_battle_pass
(
    client_id           varchar(60) not null,
    battle_pass_id      bigint      not null,
    level               integer     not null,
    current_battle_pass boolean     not null,
    current_experience  integer     not null,
    primary key (client_id, battle_pass_id, level),
    foreign key (battle_pass_id, level) references battle_pass_level (battle_pass_id, level)
);

-- rollback DROP TABLE client_battle_pass;
-- rollback DROP TABLE item_battle_pass_level;
-- rollback DROP TABLE battle_pass_level;
-- rollback DROP TABLE battle_pass;
-- rollback DROP SEQUENCE s_battle_pass_id;

-- changeset gordey_dovydenko:19

create table client_item
(
    client_id   varchar(60) not null,
    item_id     bigint      not null,
    is_equipped boolean     not null,
    primary key (client_id, item_id),
    foreign key (item_id) references cosmetic_item (item_id)
);

-- rollback DROP TABLE client_item;

-- changeset t9404:20

create table client_statistic
(
    client_id          varchar(60)              not null,
    experience         integer                  not null default 0,
    web_session_id     varchar(255),
    previous_latitude  varchar(60),
    previous_longitude varchar(60),
    distance           integer                  not null default 0,
    last_update        timestamp with time zone not null default current_timestamp,
    primary key (client_id)
);

-- rollback DROP TABLE client_statistic

-- changeset gordey_dovydenko:21

create sequence s_note_id start with 1 increment by 1;

create table client_notes
(
    note_id    bigint                            default nextval('s_note_id'),
    client_id  varchar(60)              not null,
    note       varchar                  not null,
    created_at timestamp with time zone not null default current_timestamp,
    point_id   bigint                   not null,
    primary key (note_id),
    foreign key (point_id) references points (point_id)
);

create sequence s_note_photo_id start with 1 increment by 1;

create table note_photo
(
    photo_id bigint default nextval('s_note_photo_id'),
    note_id  bigint not null,
    primary key (photo_id),
    foreign key (note_id) references client_notes (note_id)
);

-- rollback DROP TABLE note_photo;

-- changeset t9404:22
create table client_privacy
(
    client_id  varchar(60) not null,
    is_private boolean     not null,
    primary key (client_id)
);

-- rollback DROP TABLE client_privacy;


-- changeset t9404:23

create sequence s_buff_id start with 1 increment by 1;

create table buffs
(
    buff_id      bigint default nextval('s_buff_id'),
    value_factor double precision                                        not null,
    status       varchar(60) check ( status in ('COINS', 'EXPERIENCE') ) not null,
    level_number integer                                                 not null,
    primary key (buff_id)
);

-- rollback DROP TABLE buff;
-- rollback DROP SEQUENCE s_buff_id;

-- changeset t9404:24
create table client_buff
(
    client_id varchar(60) not null,
    buff_id   bigint      not null,
    primary key (client_id, buff_id),
    foreign key (buff_id) references buffs (buff_id)
);

-- rollback DROP TABLE client_buff;
