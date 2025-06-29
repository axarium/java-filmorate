CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genre (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS friendship_status (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email TEXT NOT NULL,
    login TEXT NOT NULL,
    name TEXT NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mpa_id BIGINT REFERENCES mpa(id),
    name TEXT NOT NULL,
    description TEXT,
    release_date DATE,
    duration INTEGER
);

CREATE TABLE IF NOT EXISTS user_film_like (
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    film_id BIGINT NOT NULL REFERENCES film(id),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL REFERENCES film(id),
    genre_id BIGINT NOT NULL REFERENCES genre(id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    friend_id BIGINT NOT NULL REFERENCES app_user(id),
    friendship_status_id BIGINT NOT NULL REFERENCES friendship_status(id),
    PRIMARY KEY (user_id, friend_id)
);