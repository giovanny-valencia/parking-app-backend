-- Adds a new column to the `users` table to store the user's agreement to terms.

ALTER TABLE users
ADD COLUMN agreed_to_terms BOOLEAN NOT NULL;
