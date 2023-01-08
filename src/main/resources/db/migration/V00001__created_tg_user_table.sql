DROP TABLE IF EXISTS tg_user;

-- Create tg_user table
CREATE TABLE tg_user
(
    chat_id BIGINT NOT NULL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    username VARCHAR(100),
    updated_at TIMESTAMP,
    registered_at TIMESTAMP,
    requests_count INT,
    last_request VARCHAR(25),
    selected_city VARCHAR(25)
);