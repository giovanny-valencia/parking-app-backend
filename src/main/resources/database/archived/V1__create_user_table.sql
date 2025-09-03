CREATE TABLE user(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(320) UNIQUE NOT NULL,
    hashed_password VARCHAR(256) NOT NULL,
    account_type ENUM("USER", "OFFICER") NOT NULL DEFAULT "USER",
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_on TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_account_type (account_type)
);
