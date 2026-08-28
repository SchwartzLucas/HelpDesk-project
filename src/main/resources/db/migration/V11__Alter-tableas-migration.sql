ALTER TABLE team
    MODIFY COLUMN public_code VARCHAR(30) NULL;
ALTER TABLE ticket
    MODIFY COLUMN public_code VARCHAR(30) NULL;
alter table users
    add column team_id bigint null;