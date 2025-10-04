CREATE TABLE if not exists roles
(
    id   UUID         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE if not exists users
(
    id         UUID NOT NULL,
    username   VARCHAR(255),
    email      VARCHAR(255),
    password   VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    role_id    UUID,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE if exists users
    ADD CONSTRAINT FK_USER_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

