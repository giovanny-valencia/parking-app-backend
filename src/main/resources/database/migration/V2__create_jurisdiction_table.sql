CREATE TABLE jurisdiction(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    state VARCHAR(2) NOT NULL,
    city VARCHAR(64) NOT NULL,

    INDEX idx_state_city (state, city)
);