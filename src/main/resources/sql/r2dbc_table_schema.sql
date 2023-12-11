CREATE TABLE IF NOT EXISTS `content` (
	`idx` BIGINT NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(255) NOT NULL,
	`content` TEXT NULL,
	`create_dt` TIMESTAMP NULL,
	`edit_dt` TIMESTAMP NULL,
	PRIMARY KEY (`idx`)
)
COLLATE='utf8mb4_general_ci'
;