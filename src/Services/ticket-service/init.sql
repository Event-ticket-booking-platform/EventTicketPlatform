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

-- Seed sample data
INSERT INTO events (id, name, date) VALUES
  (123, 'Event-kala-1', '2025-08-30')
ON CONFLICT DO NOTHING;

INSERT INTO seats (event_id, seat_number, is_available) VALUES
  (123, 'A1', TRUE),
  (123, 'A2', TRUE),
  (123, 'A3', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO tickets (event_id, seat_number, reserved, confirmed) VALUES
  (123, 'A1', FALSE, FALSE),
  (123, 'A2', FALSE, FALSE),
  (123, 'A3', FALSE, FALSE)
ON CONFLICT DO NOTHING;
