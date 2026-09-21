alter table  users
    add column create_time DATETIME not null default (now());

ALTER TABLE users
    DROP FOREIGN KEY fk_users_team;

alter table users
    modify  column team_id BINARY(16) null default (uuid_to_bin(uuid()));

ALTER TABLE users
    ADD CONSTRAINT fk_users_team_public_id
        FOREIGN KEY (team_id)
            REFERENCES team (public_id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;

alter table users
    modify column client_id BINARY(16) not null  default (uuid_to_bin(uuid()));

ALTER TABLE users
add CONSTRAINT fk_users_client_public_id
FOREIGN KEY (client_id)
REFERENCES client (public_id)
   ON UPDATE RESTRICT;