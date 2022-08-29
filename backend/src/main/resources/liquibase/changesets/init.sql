-- liquibase formatted sql

-- changeset ethanmcgee:1
-- validCheckSum: ANY
CREATE TABLE user (
    id bigserial PRIMARY KEY,
    username varchar(255),
    fusion_auth_user_id varchar(255)
);

-- changeset ethanmcgee:2
-- validCheckSum: ANY
CREATE TABLE todo (
    id bigserial PRIMARY KEY,
    title varchar(255) not null,
    description varchar(2048) not null,
    status varchar(50) not null default 'PENDING',
    type varchar(50) not null default 'UNCLASSIFIED',
    created_by bigint references user,
    updated_by bigint references user,
);