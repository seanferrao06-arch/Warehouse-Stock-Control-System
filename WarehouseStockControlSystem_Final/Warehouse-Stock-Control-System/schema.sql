-- Warehouse Stock Control System | MySQL 8.x
-- Run this file in MySQL Workbench, or: mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS warehouse_stock
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE warehouse_stock;

CREATE TABLE IF NOT EXISTS category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS product (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    category_id INT NOT NULL,
    reorder_level INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_product_reorder CHECK (reorder_level >= 0),
    CONSTRAINT chk_product_price CHECK (unit_price >= 0),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES category(category_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS supplier (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    contact_info VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS warehouse_location (
    location_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS `user` (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL
);

CREATE TABLE IF NOT EXISTS stock_movement (
    movement_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    location_id INT NOT NULL,
    supplier_id INT NULL,
    user_id INT NOT NULL,
    type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_movement_quantity CHECK (quantity > 0),
    CONSTRAINT chk_in_needs_supplier CHECK ((type = 'OUT' AND supplier_id IS NULL) OR (type = 'IN' AND supplier_id IS NOT NULL)),
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id)
        REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movement_location FOREIGN KEY (location_id)
        REFERENCES warehouse_location(location_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movement_supplier FOREIGN KEY (supplier_id)
        REFERENCES supplier(supplier_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movement_user FOREIGN KEY (user_id)
        REFERENCES `user`(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_movement_timestamp (`timestamp`),
    INDEX idx_movement_product (product_id)
);

CREATE TABLE IF NOT EXISTS current_stock (
    product_id INT NOT NULL,
    location_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id, location_id),
    CONSTRAINT chk_current_stock_quantity CHECK (quantity >= 0),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id)
        REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_stock_location FOREIGN KEY (location_id)
        REFERENCES warehouse_location(location_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Seed master data. Passwords are hashed with SHA-256, matching PasswordUtil.
-- Demo login: admin / admin123     staff / staff123
INSERT INTO category (name) VALUES ('Beverages'), ('Office Supplies')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO product (name, category_id, reorder_level, unit_price)
SELECT 'Iced Tea 500ml', category_id, 30, 25.00 FROM category WHERE name = 'Beverages'
ON DUPLICATE KEY UPDATE reorder_level = VALUES(reorder_level), unit_price = VALUES(unit_price);
INSERT INTO product (name, category_id, reorder_level, unit_price)
SELECT 'A4 Paper Ream', category_id, 20, 285.00 FROM category WHERE name = 'Office Supplies'
ON DUPLICATE KEY UPDATE reorder_level = VALUES(reorder_level), unit_price = VALUES(unit_price);

INSERT INTO supplier (name, contact_info) VALUES
    ('Cool Drinks Distributors', 'orders@cooldrinks.example | +91 90000 10001'),
    ('PaperPoint Traders', 'sales@paperpoint.example | +91 90000 10002')
ON DUPLICATE KEY UPDATE contact_info = VALUES(contact_info);

INSERT INTO warehouse_location (name) VALUES ('Main Warehouse'), ('Front Store')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO `user` (username, password_hash, role) VALUES
    ('admin', SHA2('admin123', 256), 'ADMIN'),
    ('staff', SHA2('staff123', 256), 'STAFF')
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), role = VALUES(role);

-- Bootstrap balances accompany the seeded, append-only history below.
INSERT INTO current_stock (product_id, location_id, quantity)
SELECT p.product_id, l.location_id, 25
FROM product p CROSS JOIN warehouse_location l
WHERE p.name = 'Iced Tea 500ml' AND l.name = 'Main Warehouse'
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity);
INSERT INTO current_stock (product_id, location_id, quantity)
SELECT p.product_id, l.location_id, 100
FROM product p CROSS JOIN warehouse_location l
WHERE p.name = 'A4 Paper Ream' AND l.name = 'Front Store'
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity);

-- A couple of historic movements make the report useful immediately.
INSERT INTO stock_movement (product_id, location_id, supplier_id, user_id, type, quantity, `timestamp`)
SELECT p.product_id, l.location_id, s.supplier_id, u.user_id, 'IN', 60, '2026-08-25 09:30:00'
FROM product p CROSS JOIN warehouse_location l CROSS JOIN supplier s CROSS JOIN `user` u
WHERE p.name='Iced Tea 500ml' AND l.name='Main Warehouse' AND s.name='Cool Drinks Distributors' AND u.username='admin'
  AND NOT EXISTS (SELECT 1 FROM stock_movement WHERE product_id=p.product_id AND location_id=l.location_id AND type='IN' AND quantity=60);
INSERT INTO stock_movement (product_id, location_id, supplier_id, user_id, type, quantity, `timestamp`)
SELECT p.product_id, l.location_id, NULL, u.user_id, 'OUT', 35, '2026-08-29 16:10:00'
FROM product p CROSS JOIN warehouse_location l CROSS JOIN `user` u
WHERE p.name='Iced Tea 500ml' AND l.name='Main Warehouse' AND u.username='staff'
  AND NOT EXISTS (SELECT 1 FROM stock_movement WHERE product_id=p.product_id AND location_id=l.location_id AND type='OUT' AND quantity=35);
INSERT INTO stock_movement (product_id, location_id, supplier_id, user_id, type, quantity, `timestamp`)
SELECT p.product_id, l.location_id, s.supplier_id, u.user_id, 'IN', 100, '2026-08-26 11:00:00'
FROM product p CROSS JOIN warehouse_location l CROSS JOIN supplier s CROSS JOIN `user` u
WHERE p.name='A4 Paper Ream' AND l.name='Front Store' AND s.name='PaperPoint Traders' AND u.username='admin'
  AND NOT EXISTS (SELECT 1 FROM stock_movement WHERE product_id=p.product_id AND location_id=l.location_id AND type='IN' AND quantity=100);
