CREATE TABLE vehicles(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plate_state VARCHAR(2) NOT NULL,
    plate_number VARCHAR(16) NOT NULL UNIQUE
);

CREATE TABLE report_addresses(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    street_address VARCHAR(256) NOT NULL,
    zip_code VARCHAR(10),
    location_notes VARCHAR(128),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TABLE report_images(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(2048) NOT NULL,
    report_id BIGINT NOT NULL
);

CREATE TABLE reports(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL UNIQUE,
    assigned_officer_id BIGINT,
    reporting_user_id BIGINT NOT NULL,
    description VARCHAR(256) NOT NULL,
    status VARCHAR(25) NOT NULL,
    notes VARCHAR(256),
    created_on DATETIME NOT NULL,
    updated_on DATETIME
);

-- adding foreign key constraints

ALTER TABLE report_addresses
ADD COLUMN jurisdiction_id BIGINT NOT NULL,
ADD CONSTRAINT fk_report_address_jurisdiction FOREIGN KEY (jurisdiction_id) REFERENCES jurisdictions (id);

ALTER TABLE reports
ADD CONSTRAINT fk_report_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
ADD CONSTRAINT fk_report_address FOREIGN KEY (address_id) REFERENCES report_addresses (id),
ADD CONSTRAINT fk_report_assigned_officer FOREIGN KEY (assigned_officer_id) REFERENCES users (id),
ADD CONSTRAINT fk_report_reporting_user FOREIGN KEY (reporting_user_id) REFERENCES users (id);

ALTER TABLE report_images
ADD CONSTRAINT fk_report_image_report FOREIGN KEY (report_id) REFERENCES reports (id);
