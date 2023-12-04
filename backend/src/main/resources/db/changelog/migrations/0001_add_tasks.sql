--liquibase formatted sql

--changeset Alexey Korotin:0001

CREATE TABLE tasks (
    id uuid PRIMARY KEY,
    heading TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    priority TEXT NOT NULL,
    author_id TEXT NOT NULL,
    assignee_id TEXT NOT NULL
);