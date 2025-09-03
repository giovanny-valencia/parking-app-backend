-- adds jurisdictions

-- Insert Jurisdictions (using ON DUPLICATE KEY UPDATE to prevent errors on re-run)
-- If a jurisdiction already exists (e.g., during development re-runs), it won't throw an error.
INSERT INTO jurisdictions (state, city) VALUES
('NJ', 'Union City'),
('NJ', 'Hoboken'),
('NY', 'New York City'),
('CA', 'Los Angeles'),
('TX', 'Houston')
ON DUPLICATE KEY UPDATE id = id; -- A dummy update to make ON DUPLICATE KEY UPDATE work

-- adds a test user
INSERT INTO users (
    first_name,
    last_name,
    date_of_birth,
    email,
    hashed_password,
    account_type,
    created_on,
    updated_on
)
VALUES (
    'Test',
    'User',
    '2000-01-01', -- Proper date format: YYYY-MM-DD
    'testuser@example.com', -- A more representative email
    'passwordUserEx',
    'USER', -- Assuming 'USER' is a valid enum value for account_type
    NOW(), -- Use NOW() for current timestamp
    NULL  -- Initially null, or you can use NOW() as well
);

-- Another test user for officers
INSERT INTO users (
    first_name,
    last_name,
    date_of_birth,
    email,
    hashed_password,
    account_type, -- This column expects a string
    created_on,
    updated_on
)
VALUES (
    'Officer',
    'Smith',
    '1985-05-15',
    'testofficer@example.com',
    'passwordOfficerEx',
    'OFFICER', -- Directly use the string "OFFICER"
    NOW(),
    NULL
);