CREATE TABLE workshop(
    id VARCHAR NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    workshop_type VARCHAR NOT NULL,
    capacity INT NOT NULL,
    register_limit INT NOT NULL,
    registration_open TIMESTAMPTZ NOT NULL,
    changes_locked TIMESTAMPTZ NULL
);

