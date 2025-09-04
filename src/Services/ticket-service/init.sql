-- Drop tables if they already exist (for fresh setup)
DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS seats CASCADE;
DROP TABLE IF EXISTS events CASCADE;

-- Table for events
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    start_utc TIMESTAMP NOT NULL,
    end_utc TIMESTAMP NOT NULL,
    organizer_id VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS event_shows (
    id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(id) ON DELETE CASCADE,
    show_number INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    UNIQUE (event_id, show_number)
);

-- Table for seats (20 per show)
CREATE TABLE IF NOT EXISTS seats (
    id SERIAL PRIMARY KEY,
    show_id INT REFERENCES event_shows(id) ON DELETE CASCADE,
    seat_number VARCHAR(10) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    UNIQUE (show_id, seat_number)
);

-- Table for tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    show_id BIGINT NOT NULL REFERENCES event_shows(id) ON DELETE CASCADE,
    seat_number VARCHAR(20) NOT NULL,
    reserved BOOLEAN NOT NULL DEFAULT FALSE,
    reserved_at TIMESTAMP NULL,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_at TIMESTAMP NULL,
    user_id BIGINT NULL,
    UNIQUE (show_id, seat_number)
);
