-- Migration: adicionar identificadores públicos e integridade relacional
-- Compatível com MySQL 8.x.
-- Estratégia:
--   * id BIGINT continua sendo a PK interna e as FKs internas.
--   * public_id BINARY(16) é usado nas APIs e integrações.
--   * public_code é opcionalmente usado na comunicação humana.
--
-- IMPORTANTE:
--   1. Faça backup antes de executar em produção.
--   2. Execute primeiro em homologação.
--   3. Esta migration assume que não existem FKs conflitantes com os nomes abaixo.
--   4. Se as tabelas já tiverem dados, o preenchimento abaixo deve ser validado antes do deploy.

SET NAMES utf8mb4;

-- ============================================================
-- 1. Adiciona identificador público às tabelas principais
-- ============================================================

ALTER TABLE `users`
    ADD COLUMN `public_id` BINARY(16) NULL AFTER `id`;

ALTER TABLE `client`
    ADD COLUMN `public_id` BINARY(16) NULL AFTER `id`;

ALTER TABLE `team`
    ADD COLUMN `public_id` BINARY(16) NULL AFTER `id`;

ALTER TABLE `ticket`
    ADD COLUMN `public_id` BINARY(16) NULL AFTER `id`;

ALTER TABLE `users`
    ADD COLUMN `public_code` VARCHAR(30) NULL AFTER `public_id`;

ALTER TABLE `client`
    ADD COLUMN `public_code` VARCHAR(30) NULL AFTER `public_id`;

ALTER TABLE `team`
    ADD COLUMN `public_code` VARCHAR(30) NULL AFTER `public_id`;

ALTER TABLE `ticket`
    ADD COLUMN `public_code` VARCHAR(30) NULL AFTER `public_id`;

-- ============================================================
-- 2. Preenche registros existentes
-- ============================================================
-- UUID_TO_BIN(uuid()) usa a representação natural dos bytes.
-- Não use o segundo parâmetro 1 para UUIDv7.

UPDATE `users`
SET `public_id` = UUID_TO_BIN(UUID())
WHERE `public_id` IS NULL;

UPDATE `client`
SET `public_id` = UUID_TO_BIN(UUID())
WHERE `public_id` IS NULL;

UPDATE `team`
SET `public_id` = UUID_TO_BIN(UUID())
WHERE `public_id` IS NULL;

UPDATE `ticket`
SET `public_id` = UUID_TO_BIN(UUID())
WHERE `public_id` IS NULL;

UPDATE `users`
SET `public_code` = CONCAT('USR-', LPAD(`id`, 8, '0'))
WHERE `public_code` IS NULL;

UPDATE `client`
SET `public_code` = CONCAT('CLI-', LPAD(`id`, 8, '0'))
WHERE `public_code` IS NULL;

UPDATE `team`
SET `public_code` = CONCAT('TEAM-', LPAD(`id`, 8, '0'))
WHERE `public_code` IS NULL;

UPDATE `ticket`
SET `public_code` = CONCAT('FXG-', YEAR(`created_date`), '-', LPAD(`id`, 6, '0'))
WHERE `public_code` IS NULL;

-- ============================================================
-- 3. Torna os identificadores públicos obrigatórios e únicos
-- ============================================================

ALTER TABLE `users`
    MODIFY COLUMN `public_id` BINARY(16) NOT NULL,
    ADD UNIQUE KEY `uk_users_public_id` (`public_id`);

ALTER TABLE `client`
    MODIFY COLUMN `public_id` BINARY(16) NOT NULL,
    ADD UNIQUE KEY `uk_client_public_id` (`public_id`);

ALTER TABLE `team`
    MODIFY COLUMN `public_id` BINARY(16) NOT NULL,
    ADD UNIQUE KEY `uk_team_public_id` (`public_id`);

ALTER TABLE `ticket`
    MODIFY COLUMN `public_id` BINARY(16) NOT NULL,
    ADD UNIQUE KEY `uk_ticket_public_id` (`public_id`);

ALTER TABLE `users`
    MODIFY COLUMN `public_code` VARCHAR(30) NOT NULL,
    ADD UNIQUE KEY `uk_users_public_code` (`public_code`);

ALTER TABLE `client`
    MODIFY COLUMN `public_code` VARCHAR(30) NOT NULL,
    ADD UNIQUE KEY `uk_client_public_code` (`public_code`);

ALTER TABLE `team`
    MODIFY COLUMN `public_code` VARCHAR(30) NOT NULL,
    ADD UNIQUE KEY `uk_team_public_code` (`public_code`);

ALTER TABLE `ticket`
    MODIFY COLUMN `public_code` VARCHAR(30) NOT NULL,
    ADD UNIQUE KEY `uk_ticket_public_code` (`public_code`);

-- ============================================================
-- 4. Corrige e completa os relacionamentos internos
-- ============================================================

ALTER TABLE `users`
    MODIFY COLUMN `team_id` BIGINT NULL;

ALTER TABLE `team`
    MODIFY COLUMN `manager_id` BIGINT NOT NULL;

ALTER TABLE `ticket`
    MODIFY COLUMN `client_id` BIGINT NOT NULL,
    MODIFY COLUMN `team_id` BIGINT NULL;

-- responsable_id atualmente é VARCHAR(36). Como a estratégia interna usa
-- BIGINT, converta-o somente se todos os valores existentes forem numéricos.
-- Execute a validação antes desta alteração:
-- SELECT responsable_id FROM ticket
-- WHERE responsable_id IS NOT NULL
--   AND responsable_id NOT REGEXP '^[0-9]+$';
--
-- Se a consulta acima não retornar linhas, execute:
ALTER TABLE `ticket`
     MODIFY COLUMN `responsable_id` BIGINT NULL;
--
-- Se a coluna já armazena UUIDs ou outros valores, NÃO faça essa conversão.
-- Nesse caso, mantenha-a separada ou migre os valores para IDs internos.

ALTER TABLE `users`
    ADD CONSTRAINT `fk_users_team`
        FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
            ON UPDATE RESTRICT
            ON DELETE SET NULL;

ALTER TABLE `team`
    ADD CONSTRAINT `fk_team_manager`
        FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;

ALTER TABLE `ticket`
    ADD CONSTRAINT `fk_ticket_client`
        FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT,
    ADD CONSTRAINT `fk_ticket_team`
        FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
            ON UPDATE RESTRICT
            ON DELETE SET NULL;

-- Execute somente se responsable_id tiver sido convertido para BIGINT:
ALTER TABLE `ticket`
     ADD CONSTRAINT `fk_ticket_responsable`
         FOREIGN KEY (`responsable_id`) REFERENCES `users` (`id`)
         ON UPDATE RESTRICT
         ON DELETE SET NULL;

-- ============================================================
-- 5. Índices úteis para consultas do helpdesk
-- ============================================================

ALTER TABLE `ticket`
    ADD KEY `ix_ticket_client` (`client_id`),
    ADD KEY `ix_ticket_team_status` (`team_id`, `status`),
    ADD KEY `ix_ticket_responsable_status` (`responsable_id`, `status`),
    ADD KEY `ix_ticket_created_date` (`created_date`);

-- ============================================================
-- 6. Defaults para novos registros
-- ============================================================
-- UUID() gera UUID textual de 36 caracteres; UUID_TO_BIN o armazena em 16 bytes.
-- A aplicação deve preferencialmente gerar UUIDv7. Estes defaults são fallback
-- para operações SQL que não passarem public_id explicitamente.

ALTER TABLE `users`
    ALTER COLUMN `public_id` SET DEFAULT (UUID_TO_BIN(UUID()));

ALTER TABLE `client`
    ALTER COLUMN `public_id` SET DEFAULT (UUID_TO_BIN(UUID()));

ALTER TABLE `team`
    ALTER COLUMN `public_id` SET DEFAULT (UUID_TO_BIN(UUID()));

ALTER TABLE `ticket`
    ALTER COLUMN `public_id` SET DEFAULT (UUID_TO_BIN(UUID()));

-- ============================================================
-- 7. Views opcionais para inspeção/administração
-- ============================================================
-- Não use estas views necessariamente na API; prefira DTOs explícitos.

CREATE OR REPLACE VIEW `ticket_public_view` AS
SELECT
    BIN_TO_UUID(t.`public_id`) AS `public_id`,
    t.`title`,
    t.`description`,
    t.`priority`,
    t.`status`,
    t.`category`,
    BIN_TO_UUID(c.`public_id`) AS `client_id`,
    CASE WHEN tm.`public_id` IS NULL THEN NULL
         ELSE BIN_TO_UUID(tm.`public_id`) END AS `team_id`,
    t.`sla_expiration`,
    t.`created_date`,
    t.`updated_date`
FROM `ticket` t
         JOIN `client` c ON c.`id` = t.`client_id`
         LEFT JOIN `team` tm ON tm.`id` = t.`team_id`;

-- ============================================================
-- 8. Consultas de exemplo para a aplicação
-- ============================================================
-- Buscar ticket pela referência pública:
-- SELECT t.*
-- FROM ticket t
-- WHERE t.public_id = UUID_TO_BIN(:publicId)
--   AND EXISTS (
--       SELECT 1
--       FROM users u
--       WHERE u.id = :authenticatedUserId
--         AND u.is_active = 1
--   );
--
-- Em sistema multiempresa, acrescente a condição de tenant/organização
-- diretamente na consulta. UUID não substitui autorização por objeto.