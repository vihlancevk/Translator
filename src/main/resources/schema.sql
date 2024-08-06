create table if not exists Response (
    id bigint not null generated always as identity primary key,
    ip_address varchar(100) not null,
    original_text varchar(500) not null,
    translated_text varchar(500) not null
);