alter table users add column isActive tinyint not null default 1;
alter table users add column team_id int null;