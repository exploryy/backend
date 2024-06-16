-- liquibase formatted sql

-- changeset gordey_dovydenko:1

insert into client_money (client_id, money)
values ('70755628-9268-4d4c-926d-8dd7c518187c', 1147483646);

-- rollback delete from client_id = '70755628-9268-4d4c-926d-8dd7c518187c';