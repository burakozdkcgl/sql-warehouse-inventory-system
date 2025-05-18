CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255),
  `username` varchar(50) NOT NULL,
  `email` varchar(255),
  `role` varchar(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
);

CREATE TABLE `warehouses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `location` varchar(255),
  `description` varchar(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
);

CREATE TABLE `items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50),
  `sku` varchar(50) NOT NULL,
  `name` varchar(50),
  `description` varchar(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `sku` (`sku`)
);

CREATE TABLE inventory (
  warehouse_id INT NOT NULL,
  item_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 0,
  reorder_level INT NOT NULL DEFAULT 0,
  PRIMARY KEY (warehouse_id, item_id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

CREATE TABLE orders (
  id INT NOT NULL AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  created_by INT,
  description VARCHAR(255),
  status VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE order_lines (
  id INT NOT NULL AUTO_INCREMENT,
  order_id INT NOT NULL,
  item_id INT NOT NULL,
  from_warehouse_id INT,
  to_warehouse_id INT,
  quantity INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
  FOREIGN KEY (from_warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
  FOREIGN KEY (to_warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE
);

CREATE TABLE `user_passwords` (
  `user_id` int NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

CREATE TABLE `user_managers` (
  `user_id` int NOT NULL,
  `manager_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `manager_id` (`manager_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
);

CREATE TABLE `user_pictures` (
  `user_id` int NOT NULL,
  `user_picture` MEDIUMBLOB DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

INSERT INTO `users` VALUES
(1,'admin','admin','admin@example.com','admin');
INSERT INTO `user_passwords` VALUES
(1,'admin');

