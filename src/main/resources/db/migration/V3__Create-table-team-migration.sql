create table team (
    id bigint primary key not null,
    name varchar(250) not null,
    description varchar(250) not null,
    manager_id varchar(36) not null
);