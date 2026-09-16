create table demand (
    id BIGINT primary key auto_increment,
    user_id bigint not null,
    user_demand_id BIGINT not null,
    public_code VARCHAR(30),
    public_id BINARY(16) default (uuid_to_bin(uuid())),
    title varchar(250) not null,
    description TEXT not null,
    attachments varchar(150),
    index idx_demand_user (user_id, user_demand_id),
    CONSTRAINT uq_user_demand_id UNIQUE(user_id, user_demand_id)
);