DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    email           VARCHAR(254) NOT NULL
);

CREATE UNIQUE INDEX idx_users_email ON users (email);

CREATE TABLE IF NOT EXISTS requests (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description     VARCHAR(1024) NOT NULL,
    requestor_id    BIGINT NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_to_users
                    FOREIGN KEY (requestor_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
);

CREATE INDEX idx_requests_requestor_id ON requests (requestor_id);

CREATE TABLE IF NOT EXISTS items (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(1024) NOT NULL,
    is_available    BOOL,
    owner_id        BIGINT NOT NULL,
    request_id      BIGINT,
    CONSTRAINT fk_items_to_users
                    FOREIGN KEY (owner_id)
                    REFERENCES users(id),
    CONSTRAINT fk_items_to_requests
                    FOREIGN KEY (request_id)
                    REFERENCES requests(id)
                    ON DELETE SET NULL
);

CREATE INDEX idx_items_owner_id ON items (owner_id);
CREATE INDEX idx_items_request_id ON items (request_id);

CREATE TABLE IF NOT EXISTS bookings (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id         BIGINT NOT NULL,
    booker_id       BIGINT NOT NULL,
    status          VARCHAR(64) NOT NULL,
    CONSTRAINT fk_bookings_to_items
                    FOREIGN KEY (item_id)
                    REFERENCES items(id)
                    ON DELETE CASCADE,
    CONSTRAINT fk_bookings_to_users
                    FOREIGN KEY (booker_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
);

CREATE INDEX idx_bookings_start_date ON bookings (start_date);
CREATE INDEX idx_bookings_end_date ON bookings (end_date);
CREATE INDEX idx_bookings_item_id ON bookings (item_id);
CREATE INDEX idx_bookings_booker_id ON bookings (booker_id);

CREATE TABLE IF NOT EXISTS comments (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text            TEXT NOT NULL,
    item_id         BIGINT NOT NULL,
    author_id       BIGINT NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_to_items
                    FOREIGN KEY (item_id)
                    REFERENCES items(id)
                    ON DELETE CASCADE,
    CONSTRAINT fk_comments_to_users
                    FOREIGN KEY (author_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
);

CREATE INDEX idx_comments_item_id ON comments (item_id);
CREATE INDEX idx_comments_author_id ON comments (author_id);
