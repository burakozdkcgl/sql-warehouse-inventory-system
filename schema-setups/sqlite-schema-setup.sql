PRAGMA foreign_keys = ON;

CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  username TEXT NOT NULL UNIQUE,
  email TEXT,
  role TEXT
);

CREATE TABLE warehouses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE,
  location TEXT,
  description TEXT
);

CREATE TABLE items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  category TEXT,
  sku TEXT NOT NULL UNIQUE,
  name TEXT,
  description TEXT
);

CREATE TABLE inventory (
  warehouse_id INTEGER NOT NULL,
  item_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL DEFAULT 0,
  reorder_level INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (warehouse_id, item_id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

CREATE TABLE orders (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  created_at DATETIME NOT NULL,
  created_by INTEGER,
  description TEXT,
  status TEXT NOT NULL,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE order_lines (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER NOT NULL,
  item_id INTEGER NOT NULL,
  from_warehouse_id INTEGER,
  to_warehouse_id INTEGER,
  quantity INTEGER NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
  FOREIGN KEY (from_warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
  FOREIGN KEY (to_warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE
);

CREATE TABLE user_passwords (
  user_id INTEGER NOT NULL,
  password TEXT NOT NULL,
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_managers (
  user_id INTEGER NOT NULL,
  manager_id INTEGER,
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE user_pictures (
  user_id INTEGER NOT NULL,
  user_picture BLOB DEFAULT NULL,
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (id, name, username, email, role) VALUES
(1, 'admin', 'admin', 'admin@example.com', 'admin');
INSERT INTO user_passwords (user_id, password) VALUES
(1, 'admin');