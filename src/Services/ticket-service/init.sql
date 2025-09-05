DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE IF NOT EXISTS events (
    id BIGINT PRIMARY KEY,  -- Provided via message
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    start_utc TIMESTAMP NOT NULL,
    end_utc TIMESTAMP NOT NULL,
    organizer_id VARCHAR(255) NOT NULL,
    total_seats INT NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    seat_number INT NOT NULL,
    reserved BOOLEAN NOT NULL DEFAULT FALSE,
    reserved_at TIMESTAMP NULL,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_at TIMESTAMP NULL,
    user_id BIGINT NULL,
    order_id VARCHAR(255) NULL,
    UNIQUE(event_id, seat_number)
);
