CREATE TABLE `users` (
  `user_id` int unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `givenname` varchar(45) NOT NULL,
  `familyname` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(45) NOT NULL,
  `isGoogleLogin` tinyint unsigned NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
)

CREATE TABLE `google_users` (
  `google_user_id` varchar(45) NOT NULL,
  `email` varchar(100) NOT NULL,
  `email_verified` tinyint NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL,
  `picture` varchar(200) NOT NULL,
  `family_name` varchar(45) NOT NULL,
  `given_name` varchar(45) NOT NULL,
  PRIMARY KEY (`google_user_id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `userEmail_idx` (`email`),
  CONSTRAINT `userEmail` FOREIGN KEY (`email`) REFERENCES `users` (`email`)
)

CREATE TABLE `stores` (
  `store_id` varchar(8) NOT NULL,
  `store_name` varchar(45) NOT NULL,
  `creator_id` int unsigned NOT NULL,
  PRIMARY KEY (`store_id`),
  UNIQUE KEY `store_id_UNIQUE` (`store_id`),
  KEY `createdBy_idx` (`creator_id`),
  CONSTRAINT `createdBy` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
)

CREATE TABLE `managers` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `manager_id` int unsigned NOT NULL,
  `store_id` varchar(8) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `manager_id_idx` (`manager_id`),
  KEY `store_id_idx` (`store_id`),
  CONSTRAINT `managedStore` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `storeManagedBy` FOREIGN KEY (`manager_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
)

CREATE TABLE `instruments` (
  `instrument_id` varchar(8) NOT NULL,
  `instrument_type` varchar(45) NOT NULL,
  `brand` varchar(45) NOT NULL,
  `model` varchar(45) NOT NULL,
  `serial_number` varchar(45) NOT NULL,
  `store_id` varchar(8) NOT NULL,
  `user_id` int unsigned DEFAULT NULL,
  `isRepairing` tinyint NOT NULL,
  PRIMARY KEY (`instrument_id`),
  UNIQUE KEY `instrument_id_UNIQUE` (`instrument_id`),
  KEY `holder_id_idx` (`user_id`),
  KEY `room_id_idx` (`store_id`),
  CONSTRAINT `borrowedBy` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `storedIn` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE
)