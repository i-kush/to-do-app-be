create table task (
    id   serial primary key,
    name varchar(20) not null unique
);