drop table IF EXISTS comments;
drop table IF EXISTS bookings;
drop table IF EXISTS items;
drop table IF EXISTS requests;
drop table IF EXISTS users;

create TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS requests
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000),
    requester_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requester FOREIGN KEY(requester_id) REFERENCES users(id)
);

create TABLE IF NOT EXISTS items
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT fk_owner FOREIGN KEY(owner_id) REFERENCES users(id),
    CONSTRAINT fk_request FOREIGN KEY(request_id) REFERENCES requests(id)
);

create TABLE IF NOT EXISTS bookings
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    status VARCHAR(100),
    booker_id BIGINT,
    CONSTRAINT fk_item FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_booker FOREIGN KEY(booker_id) REFERENCES users(id)
);

create TABLE IF NOT EXISTS comments
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_item_comment FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_author FOREIGN KEY(author_id) REFERENCES users(id)
);