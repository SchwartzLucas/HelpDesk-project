update users set users.role = 1;
update ticket set ticket.category = 1;
alter table users
    modify column role INT not null;

alter table ticket
    modify column category INT not null;