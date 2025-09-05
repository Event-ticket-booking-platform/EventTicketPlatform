DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(255) PRIMARY KEY,  -- Provided via message
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
    event_id VARCHAR(255) NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    seat_count INT NOT NULL,   
    reserved BOOLEAN NOT NULL DEFAULT FALSE,
    reserved_at TIMESTAMP NULL,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_at TIMESTAMP NULL,
    user_id VARCHAR(255) NULL,
    price DECIMAL(10,2) NOT NULL
);
