--liquibase formatted sql

--changeset kmaximk:1
--comment: initializing tables
CREATE TABLE IF NOT EXISTS link
(
    id              bigint generated always as identity,
    url             text                     not null,

    last_check_time timestamp with time zone not null,

    updated_at      timestamp with time zone not null,

    primary key (id),
    unique (url)
);

CREATE TABLE IF NOT EXISTS chat
(
    id      bigint primary key
);

CREATE TABLE IF NOT EXISTS assignment
(
    chat_id         bigint not null references chat(id),
    link_id       bigint not null references link(id)
);
