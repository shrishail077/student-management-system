CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Default admin user: username=admin | password=Admin@123
INSERT INTO users (username, password, role) VALUES
('admin', '$2b$10$vrfLtOz9BAlfvRrai40ERe0JdjlcHKPpl45DMB8x9BmSMuIZiteSy', 'ROLE_ADMIN');
