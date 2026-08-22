
create table ticket (
                        id bigint primary key not null,
                        title varchar(250) not null,
                        description varchar(5000) not null,
                        priority tinyint not null COMMENT '0 - sem prioridade, 1 - normal, 2 - média, 3 - urgente, 4 - crítica' default 0,
                        status tinyint not null COMMENT '0 - não iniciado, 1 - em análise, 2 - respondido, 3 - resolvido, 4 - contestado,
5 - fechado, 6 - reaberto' default 0,
                        category varchar(100) default 'SEM CATEGORIA',
                        client_id varchar(36) not null,
                        team_id varchar(36) not null,
                        responsable_id varchar(36) null,
                        sla_expiration datetime not null,
                        created_date datetime not null,
                        updated_date datetime not null
);