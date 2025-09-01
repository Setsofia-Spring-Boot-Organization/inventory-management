-- data.sql
-- Insert sample users (password is 'admin123' encoded with BCrypt)
INSERT INTO users (username, password, email, first_name, last_name, role, created_at, updated_at, enabled) VALUES
                                                                                                                ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@inventorymanager.com', 'System', 'Administrator', 'ADMIN', NOW(), NOW(), true),
                                                                                                                ('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'manager@inventorymanager.com', 'John', 'Manager', 'MANAGER', NOW(), NOW(), true),
                                                                                                                ('user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'user@inventorymanager.com', 'Jane', 'User', 'USER', NOW(), NOW(), true);

-- Insert sample categories
INSERT INTO categories (name, description, created_at, updated_at) VALUES
                                                                       ('Electronics', 'Electronic devices and accessories', NOW(), NOW()),
                                                                       ('Furniture', 'Office and home furniture', NOW(), NOW()),
                                                                       ('Stationery', 'Office supplies and stationery', NOW(), NOW()),
                                                                       ('Appliances', 'Kitchen and home appliances', NOW(), NOW()),
                                                                       ('Accessories', 'Various accessories and gear', NOW(), NOW());

-- Insert sample suppliers
INSERT INTO suppliers (name, contact_person, email, phone, address, created_at, updated_at) VALUES
                                                                                                ('TechCorp', 'Michael Johnson', 'contact@techcorp.com', '+1-555-0101', '123 Tech Street, Silicon Valley, CA', NOW(), NOW()),
                                                                                                ('DisplayTech', 'Sarah Wilson', 'sales@displaytech.com', '+1-555-0102', '456 Display Ave, Austin, TX', NOW(), NOW()),
                                                                                                ('ComfortSeats', 'David Brown', 'orders@comfortseats.com', '+1-555-0103', '789 Furniture Blvd, Grand Rapids, MI', NOW(), NOW()),
                                                                                                ('LightUp', 'Emma Davis', 'info@lightup.com', '+1-555-0104', '321 Lighting Way, Portland, OR', NOW(), NOW()),
                                                                                                ('PaperPlus', 'Robert Miller', 'sales@paperplus.com', '+1-555-0105', '654 Paper Street, New York, NY', NOW(), NOW()),
                                                                                                ('WriteWell', 'Lisa Garcia', 'contact@writewell.com', '+1-555-0106', '987 Pen Avenue, Chicago, IL', NOW(), NOW()),
                                                                                                ('BrewMaster', 'James Wilson', 'orders@brewmaster.com', '+1-555-0107', '159 Coffee Lane, Seattle, WA', NOW(), NOW()),
                                                                                                ('HydroGear', 'Amanda Taylor', 'info@hydrogear.com', '+1-555-0108', '753 Water Street, Denver, CO', NOW(), NOW());

-- Insert sample inventory items
INSERT INTO inventory_items (name, category, quantity, price, supplier, min_stock_level, max_stock_level, status, created_at, updated_at, created_by, updated_by) VALUES
                                                                                                                                                                      ('Laptop Pro', 'Electronics', 15, 1299.99, 'TechCorp', 10, 50, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Wireless Mouse', 'Electronics', 45, 29.99, 'TechCorp', 20, 100, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Monitor 4K', 'Electronics', 8, 399.99, 'DisplayTech', 10, 30, 'LOW_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Office Chair', 'Furniture', 25, 249.99, 'ComfortSeats', 15, 40, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Desk Lamp', 'Furniture', 30, 79.99, 'LightUp', 10, 50, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Notebook Set', 'Stationery', 5, 12.99, 'PaperPlus', 10, 100, 'LOW_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Pen Pack', 'Stationery', 60, 8.99, 'WriteWell', 20, 200, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Coffee Maker', 'Appliances', 12, 159.99, 'BrewMaster', 10, 25, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Water Bottle', 'Accessories', 3, 19.99, 'HydroGear', 10, 75, 'LOW_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Keyboard', 'Electronics', 20, 89.99, 'TechCorp', 15, 60, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Wireless Headphones', 'Electronics', 0, 199.99, 'TechCorp', 5, 25, 'OUT_OF_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Standing Desk', 'Furniture', 7, 549.99, 'ComfortSeats', 5, 20, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Printer Paper', 'Stationery', 150, 24.99, 'PaperPlus', 50, 500, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('Desk Organizer', 'Accessories', 35, 34.99, 'WriteWell', 15, 80, 'IN_STOCK', NOW(), NOW(), 'admin', 'admin'),
                                                                                                                                                                      ('USB Cable', 'Electronics', 2, 14.99, 'TechCorp', 10, 100, 'LOW_STOCK', NOW(), NOW(), 'admin', 'admin');