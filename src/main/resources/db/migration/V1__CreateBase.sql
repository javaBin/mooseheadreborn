CREATE TABLE workshop(
    id VARCHAR NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    workshop_type VARCHAR NOT NULL,
    capacity INT NOT NULL,
    register_limit INT NOT NULL,
    registration_open TIMESTAMPTZ NOT NULL,
    changes_locked TIMESTAMPTZ NULL
);

CREATE TABLE particiant(
    id VARCHAR NOT NULL PRIMARY KEY,
    email VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    access_key VARCHAR NOT NULL,
    activated_at TIMESTAMPTZ NULL
);

CREATE TABLE participant_registration(
    id VARCHAR NOT NULL,
    particiapant_id VARCHAR NOT NULL,
    register_token VARCHAR NOT NULL,
    created TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ NULL
);

CREATE TABLE registration(
    id VARCHAR NOT NULL PRIMARY KEY,
    status VARCHAR NOT NULL,
    workshop VARCHAR NOT NULL,
    participant VARCHAR NOT NULL,
    participant_count INT NOT NULL,
    registered_at TIMESTAMPTZ NOT NULL,
    cancelled_at TIMESTAMPTZ NULL
);

