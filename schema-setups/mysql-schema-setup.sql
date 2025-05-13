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









INSERT INTO `users` VALUES
(2,'Ferhunde Güven','ferhundee','benferhunde@example.com','admin'),
(3,'Oğuz Ayhan','oguz','oguz@example.com','manager'),
(4,'Sedef Turan','sedef','bucurkiz@example.com','manager'),
(5,'Hayriye Tekin','hayriye','amanagzimizintadi@example.com','manager'),
(6,'Şevket Tekin','sevket','sevket@example.com','employee'),
(7,'Cevriye Başsoy','cevriyehanim','ethan@example.com','employee'),
(8,'Ceyda Ayhan','ceyda','ceydaa@example.com','employee'),
(9,'Fikret Tekin','fiko','fiko@example.com','employee'),
(10,'Neyyir Genç','nehirhanim','nehrr@example.com','guest');

INSERT INTO `user_passwords` VALUES
(2,'password'),
(3,'password'),
(4,'password'),
(5,'password'),
(6,'password'),
(7,'password'),
(8,'password'),
(9,'password'),
(10,'password');

INSERT INTO user_managers (user_id, manager_id) VALUES
(6, 3),
(8, 3),
(9, 5);

INSERT INTO `warehouses` (`name`, `location`, `description`) VALUES
('Central Depot', '123 Main St, Springfield', 'Main storage facility for regional distribution'),
('West Hub', '456 Industrial Rd, Shelbyville', 'Warehouse for western area logistics'),
('East Storage', '789 Warehouse Ln, Capital City', 'Storage for surplus inventory and seasonal items');

INSERT INTO `items` (`category`, `sku`, `name`, `description`) VALUES
('Electronics', 'E-001', 'Wireless Mouse', 'A responsive wireless mouse with ergonomic design and long battery life.'),
('Electronics', 'E-002', 'Bluetooth Headphones', 'Over-ear Bluetooth headphones with rich sound and noise isolation.'),
('Electronics', 'E-003', 'Smartphone Charger', 'A fast-charging USB wall adapter compatible with most mobile devices.'),
('Electronics', 'E-004', 'Portable Speaker', 'Compact Bluetooth speaker with powerful sound and built-in mic.'),
('Electronics', 'E-005', 'LED Monitor', 'A 24-inch LED monitor with full HD resolution and low blue light mode.'),
('Electronics', 'E-006', 'Gaming Keyboard', 'Mechanical RGB keyboard with customizable keys for gaming and work.'),
('Electronics', 'E-007', 'USB Flash Drive', 'A 64GB USB 3.0 flash drive for high-speed data transfer and storage.'),
('Electronics', 'E-008', 'Webcam', 'HD webcam with built-in microphone for clear video calls.'),
('Electronics', 'E-009', 'Power Bank', '10,000mAh portable power bank with dual USB output.'),
('Electronics', 'E-010', 'Smartwatch', 'A sleek smartwatch with fitness tracking and phone notifications.'),
('Electronics', 'E-011', 'Noise Cancelling Earbuds', 'Wireless earbuds with active noise cancellation and touch controls.'),
('Electronics', 'E-012', 'External Hard Drive', 'A 1TB external hard drive for secure backups and file storage.'),
('Electronics', 'E-013', 'Wi-Fi Router', 'Dual-band Wi-Fi router offering fast and stable home internet coverage.'),
('Electronics', 'E-014', 'Action Camera', 'Waterproof action cam with 4K video and wide-angle lens.'),
('Electronics', 'E-015', 'Bluetooth Tracker', 'A compact tracker that helps locate lost keys, bags, or devices.'),
('Electronics', 'E-016', 'Laptop Stand', 'Adjustable aluminum stand for laptops to improve posture and cooling.'),
('Electronics', 'E-017', 'Wireless Charging Pad', 'Qi-enabled charging pad for wirelessly powering compatible smartphones.'),
('Electronics', 'E-018', 'HDMI Cable', 'High-speed HDMI cable with support for 4K resolution and Ethernet.'),
('Electronics', 'E-019', 'Streaming Stick', 'Plug-and-play streaming stick with access to major media platforms.'),
('Electronics', 'E-020', 'VR Headset', 'Immersive virtual reality headset compatible with PC and mobile games.'),
('Clothing', 'C-001', 'Cotton T-Shirt', 'A soft, breathable cotton t-shirt ideal for casual, everyday wear.'),
('Clothing', 'C-002', 'Denim Jeans', 'Classic blue denim jeans with a comfortable fit and timeless style.'),
('Clothing', 'C-003', 'Hoodie', 'A warm, fleece-lined hoodie perfect for layering and cold weather.'),
('Clothing', 'C-004', 'Jacket', 'A lightweight, zip-up jacket suitable for transitional seasons.'),
('Clothing', 'C-005', 'Sneakers', 'Comfortable and stylish sneakers made for all-day walking.'),
('Clothing', 'C-006', 'Baseball Cap', 'An adjustable baseball cap with a curved brim and breathable fabric.'),
('Clothing', 'C-007', 'Socks', 'Soft cotton socks designed for daily comfort and durability.'),
('Clothing', 'C-008', 'Sweatpants', 'Relaxed-fit sweatpants made from cozy, stretchable fabric.'),
('Clothing', 'C-009', 'Scarf', 'A lightweight scarf perfect for layering in cool weather.'),
('Clothing', 'C-010', 'Belt', 'A durable leather belt with a classic metal buckle.'),
('Clothing', 'C-011', 'Polo Shirt', 'A short-sleeve polo shirt with a button collar and athletic fit.'),
('Clothing', 'C-012', 'Windbreaker', 'A water-resistant windbreaker with breathable lining and front pockets.'),
('Clothing', 'C-013', 'Cargo Pants', 'Multi-pocket cargo pants designed for comfort and functionality.'),
('Clothing', 'C-014', 'Tank Top', 'A sleeveless cotton tank top ideal for warm days or layering.'),
('Clothing', 'C-015', 'Wool Sweater', 'A heavyweight wool sweater that provides warmth and classic style.'),
('Clothing', 'C-016', 'Leggings', 'High-stretch leggings perfect for workouts or casual wear.'),
('Clothing', 'C-017', 'Raincoat', 'A waterproof raincoat with a hood and full-length coverage.'),
('Clothing', 'C-018', 'Sports Bra', 'A supportive sports bra with moisture-wicking fabric and soft padding.'),
('Clothing', 'C-019', 'Flip Flops', 'Comfortable rubber flip flops ideal for the beach or poolside.'),
('Clothing', 'C-020', 'Dress Shirt', 'A formal button-up dress shirt with a tailored fit for professional wear.'),
('Kitchenware', 'K-001', 'Blender', 'A high-powered blender ideal for making smoothies, soups, and sauces with ease.'),
('Kitchenware', 'K-002', 'Toaster', 'A two-slice toaster with adjustable settings for perfect browning every time.'),
('Kitchenware', 'K-003', 'Non-stick Pan', 'A durable non-stick pan designed for easy cooking and quick cleanup.'),
('Kitchenware', 'K-004', 'Knife Set', 'A complete set of stainless steel knives for precise cutting, chopping, and slicing.'),
('Kitchenware', 'K-005', 'Cutting Board', 'A sturdy cutting board made of BPA-free plastic for safe food preparation.'),
('Kitchenware', 'K-006', 'Measuring Cups', 'A nested set of measuring cups for accurate ingredient portioning.'),
('Kitchenware', 'K-007', 'Mixing Bowl', 'A multi-purpose mixing bowl made of non-slip, dishwasher-safe material.'),
('Kitchenware', 'K-008', 'Spatula', 'A heat-resistant silicone spatula perfect for flipping, folding, and scraping.'),
('Kitchenware', 'K-009', 'Coffee Maker', 'A drip-style coffee maker that brews rich, aromatic coffee in minutes.'),
('Kitchenware', 'K-010', 'Dish Rack', 'A compact dish rack with a built-in utensil holder and water-drain tray.'),
('Cleaning Supplies', 'CL-001', 'All-Purpose Cleaner', 'A powerful all-purpose cleaner that effectively removes dirt, grease, and grime from most surfaces.'),
('Cleaning Supplies', 'CL-002', 'Dish Soap', 'A gentle yet effective dish soap that cuts through grease while being soft on hands.'),
('Cleaning Supplies', 'CL-003', 'Sponge', 'An absorbent, durable sponge designed for everyday kitchen and bathroom cleaning.'),
('Cleaning Supplies', 'CL-004', 'Mop', 'A high-quality mop with a sturdy handle and microfiber head for efficient floor cleaning.'),
('Cleaning Supplies', 'CL-005', 'Broom', 'A lightweight broom with angled bristles for sweeping up dust and debris in tight corners.'),
('Cleaning Supplies', 'CL-006', 'Dustpan', 'A wide-mouth dustpan that pairs with any broom for easy collection of swept-up debris.'),
('Cleaning Supplies', 'CL-007', 'Scrub Brush', 'A durable scrub brush with stiff bristles for removing tough stains and grime.'),
('Cleaning Supplies', 'CL-008', 'Glass Cleaner', 'A streak-free glass cleaner that leaves windows, mirrors, and glass surfaces crystal clear.'),
('Cleaning Supplies', 'CL-009', 'Disinfectant Wipes', 'Pre-moistened disinfectant wipes for killing germs and bacteria on hard, non-porous surfaces.'),
('Cleaning Supplies', 'CL-010', 'Toilet Brush', 'A toilet brush with a sturdy handle and tough bristles for effective bowl cleaning.'),
('Toys', 'T-001', 'Action Figure', 'A poseable action figure perfect for imaginative play.'),
('Toys', 'T-002', 'Puzzle Set', 'A challenging puzzle set that stimulates critical thinking.'),
('Toys', 'T-003', 'Board Game', 'A classic board game for family fun nights.'),
('Toys', 'T-004', 'Doll', 'A soft and cuddly doll with detailed clothing.'),
('Toys', 'T-005', 'Toy Car', 'A miniature toy car built for high-speed adventures.'),
('Toys', 'T-006', 'Building Blocks', 'Colorful building blocks for creative construction.'),
('Toys', 'T-007', 'Plush Bear', 'A plush teddy bear made for hugs and comfort.'),
('Toys', 'T-008', 'Yo-Yo', 'A classic yo-yo for hours of skill-based fun.'),
('Toys', 'T-009', 'RC Helicopter', 'A remote-controlled helicopter with easy controls.'),
('Toys', 'T-010', 'Magic Kit', 'A magic kit with props and instructions for beginners.'),
('Office Supplies', 'O-001', 'Notebook', 'A durable notebook for note-taking and journaling.'),
('Office Supplies', 'O-002', 'Pen Set', 'A premium pen set for smooth and professional writing.'),
('Office Supplies', 'O-003', 'Stapler', 'A compact stapler ideal for office and home use.'),
('Office Supplies', 'O-004', 'Desk Organizer', 'A desk organizer to keep supplies neat and accessible.'),
('Office Supplies', 'O-005', 'Mouse Pad', 'A comfortable mouse pad with a non-slip base.'),
('Office Supplies', 'O-006', 'Paper Clips', 'A pack of sturdy paper clips for binding documents.'),
('Office Supplies', 'O-007', 'File Folder', 'A file folder to store and organize important papers.'),
('Office Supplies', 'O-008', 'Highlighters', 'A set of highlighters for marking important text.'),
('Office Supplies', 'O-009', 'Sticky Notes', 'Sticky notes for quick reminders and organization.'),
('Office Supplies', 'O-010', 'Whiteboard Marker', 'A whiteboard marker that writes clearly and erases cleanly.'),
('Sports', 'S-001', 'Yoga Mat', 'A cushioned yoga mat for comfort and grip during workouts.'),
('Sports', 'S-002', 'Dumbbells', 'A pair of dumbbells for strength training at home or the gym.'),
('Sports', 'S-003', 'Resistance Bands', 'Elastic resistance bands to add intensity to workouts.'),
('Sports', 'S-004', 'Jump Rope', 'A lightweight jump rope for cardio and endurance training.'),
('Sports', 'S-005', 'Water Bottle', 'A reusable water bottle to stay hydrated on the go.'),
('Sports', 'S-006', 'Fitness Tracker', 'A digital fitness tracker to monitor activity and heart rate.'),
('Sports', 'S-007', 'Kettlebell', 'A cast-iron kettlebell for strength and conditioning.'),
('Sports', 'S-008', 'Foam Roller', 'A foam roller for post-workout muscle recovery.'),
('Sports', 'S-009', 'Pull-Up Bar', 'A pull-up bar for upper body workouts at home.'),
('Sports', 'S-010', 'Exercise Ball', 'A stability ball for core exercises and balance training.'),
('Furniture', 'F-001', 'Sofa', 'A modern three-seater sofa with plush cushions and a sturdy wooden frame.'),
('Furniture', 'F-002', 'Dining Table', 'A sleek wooden dining table that comfortably seats six people.'),
('Furniture', 'F-003', 'Office Chair', 'An ergonomic office chair with adjustable height and lumbar support.'),
('Furniture', 'F-004', 'Bookshelf', 'A five-tier bookshelf made of oak wood for organized book storage.'),
('Furniture', 'F-005', 'Coffee Table', 'A glass-top coffee table with a metal frame and lower shelf.'),
('Furniture', 'F-006', 'Bed Frame', 'A queen-sized bed frame with a high headboard and under-bed storage.'),
('Furniture', 'F-007', 'Wardrobe', 'A spacious two-door wardrobe with hanging space and built-in drawers.'),
('Furniture', 'F-008', 'TV Stand', 'A low-profile TV stand with open shelves and cable management system.'),
('Furniture', 'F-009', 'Recliner Chair', 'A comfortable recliner chair with padded armrests and footrest.'),
('Furniture', 'F-010', 'Nightstand', 'A compact nightstand with a drawer and open shelf for bedside essentials.'),
('Furniture', 'F-011', 'Bar Stool', 'A high-back bar stool with a cushioned seat and footrest.'),
('Furniture', 'F-012', 'Desk', 'A minimalist writing desk with a drawer and sturdy metal legs.'),
('Furniture', 'F-013', 'Dresser', 'A six-drawer dresser with a modern design and smooth sliding drawers.'),
('Furniture', 'F-014', 'Accent Chair', 'A stylish accent chair with velvet upholstery and angled legs.'),
('Furniture', 'F-015', 'Console Table', 'A narrow console table ideal for entryways or behind sofas.'),
('Furniture', 'F-016', 'Ottoman', 'A round ottoman with tufted top and hidden interior storage.'),
('Furniture', 'F-017', 'Vanity Table', 'A compact vanity table with mirror and multiple compartments.'),
('Furniture', 'F-018', 'Shoe Rack', 'A three-tier shoe rack made of bamboo with slatted shelves.'),
('Furniture', 'F-019', 'Filing Cabinet', 'A two-drawer metal filing cabinet with lock for secure storage.'),
('Furniture', 'F-020', 'Room Divider', 'A folding room divider with decorative panels for added privacy.'),
('White Goods', 'W-001', 'Refrigerator', 'A double-door refrigerator with frost-free technology and ample storage.'),
('White Goods', 'W-002', 'Washing Machine', 'A front-load washing machine with multiple wash cycles and energy efficiency.'),
('White Goods', 'W-003', 'Dishwasher', 'A built-in dishwasher with adjustable racks and eco-friendly operation.'),
('White Goods', 'W-004', 'Chest Freezer', 'A spacious chest freezer ideal for bulk food storage.'),
('White Goods', 'W-005', 'Electric Oven', 'A convection electric oven with multiple cooking modes and a timer.'),
('White Goods', 'W-006', 'Air Conditioner', 'A split AC unit with inverter technology and smart temperature control.'),
('White Goods', 'W-007', 'Dryer', 'An electric clothes dryer with moisture sensor and anti-wrinkle setting.'),
('White Goods', 'W-008', 'Water Heater', 'A tankless water heater with instant hot water and temperature regulation.'),
('White Goods', 'W-009', 'Range Hood', 'A wall-mounted range hood with strong suction and LED lighting.'),
('White Goods', 'W-010', 'Dehumidifier', 'A high-capacity dehumidifier with auto-shutoff and continuous drain option.');

INSERT INTO inventory (warehouse_id, item_id, quantity, reorder_level) VALUES
(1, 82, 39, 15),
(1, 15, 21, 25),
(1, 32, 74, 20),
(1, 29, 84, 20),
(1, 18, 17, 25),
(1, 14, 34, 15),
(1, 70, 64, 25),
(1, 55, 9, 25),
(1, 28, 0, 20),
(1, 30, 63, 25),
(1, 65, 94, 25),
(1, 72, 58, 15),
(1, 26, 55, 25),
(1, 92, 2, 15),
(1, 90, 97, 25),
(1, 54, 82, 15),
(1, 58, 5, 15),
(1, 106, 7, 20),
(1, 44, 40, 25),
(1, 86, 38, 25),
(2, 4, 46, 25),
(2, 95, 100, 25),
(2, 29, 13, 15),
(2, 117, 57, 25),
(2, 112, 55, 25),
(2, 12, 21, 25),
(2, 76, 16, 25),
(2, 55, 7, 20),
(2, 5, 9, 15),
(2, 78, 7, 25),
(2, 103, 10, 15),
(2, 26, 14, 15),
(2, 84, 29, 15),
(2, 54, 21, 25),
(2, 114, 4, 20),
(2, 58, 90, 25),
(2, 1, 72, 15),
(2, 21, 10, 15),
(2, 105, 13, 20),
(2, 86, 31, 20),
(3, 36, 42, 25),
(3, 117, 63, 15),
(3, 14, 3, 15),
(3, 87, 84, 25),
(3, 118, 3, 15),
(3, 107, 90, 15),
(3, 28, 8, 15),
(3, 30, 50, 15),
(3, 65, 81, 15),
(3, 72, 27, 15),
(3, 108, 6, 15),
(3, 114, 83, 20),
(3, 116, 96, 20),
(3, 1, 49, 25),
(3, 21, 0, 15),
(3, 105, 37, 25);

INSERT INTO orders (created_at, created_by, description, status) VALUES
('2025-05-04 09:15:00', 6, '', 'Approved'),
('2025-05-04 16:45:00', 7, '', 'Pending'),
('2025-05-05 08:50:00', 8, '', 'Pending'),
('2025-05-05 13:30:00', 9, '', 'Canceled'),
('2025-05-06 15:10:00', 3, '', 'Pending');

INSERT INTO order_lines (order_id, item_id, from_warehouse_id, to_warehouse_id, quantity) VALUES
(1, 105, 3, 1, 20),
(1, 107, 3, 1, 10),
(1, 108, 3, 1, 8),
(2, 6, 2, 2, 7),
(2, 7, 2, 2, 12),
(2, 8, 2, 2, 9),
(3, 95, 2, 1, 15),
(3, 87, 2, 1, 5),
(3, 76, 2, 1, 6),
(4, 14, 1, 3, 10),
(4, 4, 1, 3, 13),
(4, 5, 1, 3, 5),
(5, 112, 2, 3, 11),
(5, 114, 2, 3, 7),
(5, 116, 2, 3, 9);

