alter table demand
    modify column user_id binary(16) not null default (uuid_to_bin(uuid()));