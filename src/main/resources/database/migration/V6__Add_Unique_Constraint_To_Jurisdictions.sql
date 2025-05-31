ALTER TABLE jurisdictions
ADD CONSTRAINT UQ_jurisdictions_state_city UNIQUE (state, city);