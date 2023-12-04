--liquibase formatted sql

--changeset Alexey Korotin:0002

CREATE TABLE comments (
    id uuid PRIMARY KEY,
    task_id uuid REFERENCES tasks(id),
    comment TEXT NOT NULL,
    author_id TEXT NOT NULL
);