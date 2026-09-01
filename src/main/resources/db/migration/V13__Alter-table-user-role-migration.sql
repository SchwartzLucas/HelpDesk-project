update users set role = 1;
alter table users
    modify column role INT not null;