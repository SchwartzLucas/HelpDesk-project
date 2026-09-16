-- ============================================================
-- Migration V14: sistema de demandas/equipe
--   1. users.name -- nome real da pessoa
--   2. ticket.deadline -- prazo (opcional) para visões dia/semana/mês
--   3. ticket_time_entry -- cronometragem (start/pause/stop) por usuário
-- ============================================================

SET NAMES utf8mb4;

-- 1. Nome do usuário
ALTER TABLE `users`
    ADD COLUMN `name` VARCHAR(250) NULL AFTER `login`;

UPDATE `users`
SET `name` = `login`
WHERE `name` IS NULL;

ALTER TABLE `users`
    MODIFY COLUMN `name` VARCHAR(250) NOT NULL;

-- 2. Prazo opcional no ticket
ALTER TABLE `ticket`
    ADD COLUMN `deadline` DATETIME NULL AFTER `sla_expiration`;

-- 3. Tabela de apontamento de tempo
CREATE TABLE `ticket_time_entry` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `ticket_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME NULL,
    `created_date` DATETIME NOT NULL,
    `updated_date` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tte_public_id` (`public_id`),
    KEY `ix_tte_user` (`user_id`),
    KEY `ix_tte_ticket` (`ticket_id`),
    CONSTRAINT `fk_tte_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `ticket` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_tte_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;