-- liquibase formatted sql

-- changeset gordey_dovydenko:1

create sequence s_quest_id start with 1 increment by 1;

create table quest
(
    quest_id        bigint default nextval('s_quest_id'),
    name            varchar(60)                                                         not null,
    description     varchar(255)                                                        not null,
    difficulty_type varchar(20) check ( difficulty_type in ('EASY', 'MEDIUM', 'HARD') ) not null,
    quest_type      varchar(20) check ( quest_type in ('POINT_TO_POINT', 'DISTANCE') )  not null,
    transport_type  varchar(20) check ( transport_type in ('WALK', 'BICYCLE') )         not null,
    primary key (quest_id)
);

-- rollback DROP TABLE quest;
-- rollback DROP SEQUENCE s_quest_id;

-- changeset gordey_dovydenko:2

create sequence s_route_id start with 1 increment by 1;

create table point_route
(
    latitude       varchar(60) not null,
    longitude      varchar(60) not null,
    next_latitude  varchar(60),
    next_longitude varchar(60),
    primary key (latitude, longitude),
    foreign key (next_latitude, next_longitude) references point_route (latitude, longitude)
);

create table route
(
    route_id        bigint default nextval('s_route_id'),
    distance        double precision not null,
    point_latitude  varchar(60)      not null,
    point_longitude varchar(60)      not null,
    primary key (route_id),
    foreign key (point_latitude, point_longitude) references point_route (latitude, longitude)
);

-- rollback DROP TABLE route;
-- rollback DROP SEQUENCE s_route_id;
-- rollback DROP TABLE point_route;

-- changeset gordey_dovydenko:3

create sequence s_pass_quest_id start with 1 increment by 1;

create table pass_quest
(
    pass_quest_id  bigint default nextval('s_pass_quest_id'),
    quest_id       bigint                                                      not null,
    client_id      varchar(60)                                                 not null,
    start_time     timestamp                                                   not null,
    end_time       timestamp                                                   not null,
    route_id       bigint                                                      not null,
    transport_type varchar(20) check ( transport_type in ('WALK', 'BICYCLE') ) not null,
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
    primary key (quest_id),
    foreign key (quest_id) references quest (quest_id)
);

-- rollback DROP TABLE distance_quest;

-- changeset gordey_dovydenko:10

create sequence s_achievement_id start with 1 increment by 1;

create table achievement
(
    achievement_id bigint default nextval('s_achievement_id'),
    name           varchar(60)  not null,
    description    varchar(255) not null,
    primary key (achievement_id)
);

-- rollback DROP TABLE achievement;

-- changeset gordey_dovydenko:11

create table client_achievement
(
    client_id        varchar(60)              not null,
    achievement_id   bigint                   not null,
    achievement_date timestamp with time zone not null,
    primary key (client_id, achievement_id),
    foreign key (achievement_id) references achievement (achievement_id)
);

-- rollback DROP TABLE client_achievement;

-- changeset gordey_dovydenko:12

create table friend
(
    client_id varchar(60) not null,
    friend_id varchar(60) not null,
    primary key (client_id, friend_id)
);

-- rollback DROP TABLE friend;