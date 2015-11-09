# --- !Ups
CREATE TABLE account (
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    email      varchar NOT NULL UNIQUE,
    password   varchar NOT NULL,
    name       varchar NOT NULL,
    role       varchar NOT NULL
);

CREATE TABLE client (
    id          SERIAL NOT NULL PRIMARY KEY,
    name        varchar NOT NULL UNIQUE,
    ats         varchar NOT NULL,
    "displayName" varchar NOT NULL,
    "feedId"      varchar NOT NULL,
    "sourceId"    varchar NOT NULL,
    host        varchar NOT NULL
);

# --- !Downs
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS client;