CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255),
  `username` varchar(50) NOT NULL,
  `email` varchar(255),
  `role` varchar(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
);

INSERT INTO `users` VALUES (1,'Alice Johnson','alicej','alice@example.com','admin'),(2,'Bob Smith','bobsmith','bob@example.com','employee'),(3,'Charlie Brown','charlieb','charlie@example.com','manager'),(4,'Diana Prince','dianap','diana@example.com','employee'),(5,'Ethan Hunt','ethanh','ethan@example.com','employee');


CREATE TABLE `user_managers` (
  `user_id` int NOT NULL,
  `manager_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `manager_id` (`manager_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
);

INSERT INTO `user_managers` VALUES (1,NULL),(3,NULL),(2,3),(4,3),(5,3);

CREATE TABLE `user_passwords` (
  `user_id` int NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

INSERT INTO `user_passwords` VALUES (1,'password123'),(2,'qwerty456'),(3,'charlie789'),(4,'wonderwoman'),(5,'mission123');

CREATE TABLE `user_pictures` (
  `user_id` int NOT NULL,
  `user_picture` MEDIUMBLOB DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

INSERT INTO `user_pictures` VALUES (1,NULL),(2,NULL),(3,NULL),(4,NULL);


