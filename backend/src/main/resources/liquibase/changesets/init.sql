-- liquibase formatted sql

-- changeset ethanmcgee:1
-- validCheckSum: ANY
CREATE TABLE todo (
    id bigserial PRIMARY KEY,
    title varchar(255) not null,
    description varchar(2048) not null,
    status varchar(50) not null default 'PENDING',
    type varchar(50) not null default 'UNCLASSIFIED',
    created_by varchar(255),
    updated_by varchar(255)
);