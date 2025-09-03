ALTER TABLE jurisdictions DROP INDEX idx_state_city;

ALTER TABLE reports ADD INDEX idx_status (status);
ALTER TABLE reports ADD INDEX idx_created_on (created_on);