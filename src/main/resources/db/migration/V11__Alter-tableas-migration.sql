CREATE TABLE ticket_comment
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    ticket_id  BIGINT NOT NULL,
    body       TEXT   NOT NULL,
    attachment VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_comment_ticket
        FOREIGN KEY (ticket_id)
            REFERENCES ticket (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;