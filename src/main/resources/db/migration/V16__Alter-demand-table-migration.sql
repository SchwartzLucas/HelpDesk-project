alter table demand
    add column create_time DATETIME not null default (NOW());

alter table demand
    add column finish_time DATETIME null;

alter table demand
    add column stopped_time DATETIME null;

alter table demand
    add column started_time DATETIME null;

alter table demand
    add column demand_status INT NOT NULL COMMENT '0 = created demand | 1 = actual active demand | 2 = stopped demand | 3 = demand finished | 4 = canceled demand';
