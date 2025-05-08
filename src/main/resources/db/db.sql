CREATE TABLE user (
                      user_id  VARCHAR(36) PRIMARY KEY,
                      username VARCHAR(255) UNIQUE NOT NULL,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
                            category_id VARCHAR(36) PRIMARY KEY,
                            code VARCHAR(50) NOT NULL UNIQUE,
                            name VARCHAR(100) NOT NULL,
                            parent_category VARCHAR(50) CHECK (parent_category IN ('Inventory Items', 'Sales Items')),
                            tax_type VARCHAR(50),
                            description TEXT,
                            base_price DECIMAL(18, 2) DEFAULT 0.0,
                            price_increase DECIMAL(18, 2) DEFAULT 0.0,
                            is_hidden BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);