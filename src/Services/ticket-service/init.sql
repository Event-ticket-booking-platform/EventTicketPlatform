-- Table for events
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL
);

-- Table for seats
CREATE TABLE IF NOT EXISTS seats (
    id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(id),
    seat_number VARCHAR(10) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- Table for tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    reserved BOOLEAN NOT NULL DEFAULT FALSE,
    reserved_at TIMESTAMP NULL,
    user_id BIGINT NULL,
    UNIQUE (event_id, seat_number)
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

INSERT INTO tickets (event_id, seat_number, reserved) VALUES
  (123, 'A1', FALSE),
  (123, 'A2', FALSE),
  (123, 'A3', FALSE)
ON CONFLICT DO NOTHING;
