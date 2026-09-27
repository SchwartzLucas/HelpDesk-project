CREATE TABLE demand_status_history (
   id BIGINT NOT NULL AUTO_INCREMENT,
   demand_id BIGINT NOT NULL,
   previous_status INT NULL,
   new_status INT NOT NULL,
   changed_at TIMESTAMP(6) NOT NULL,
   changed_by BINARY(16) NULL,

   PRIMARY KEY (id),

   CONSTRAINT fk_demand_status_history_demand
       FOREIGN KEY (demand_id)
           REFERENCES demand(id),

   INDEX idx_demand_status_history_demand_time
       (demand_id, changed_at),

   INDEX idx_demand_status_history_status
       (new_status)
);