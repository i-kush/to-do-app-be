create extension if not exists "uuid-ossp";

create table if not exists tenant (
    id      uuid        not null default uuid_generate_v4(),
    name    varchar(50) not null,
    created timestamp   not null,
    updated timestamp   not null,
    constraint pk_tenant_id primary key (id),
    constraint unq_tenant_name unique (name)
);

create table if not exists task (
    id        uuid        not null default uuid_generate_v4(),
    tenant_id uuid        not null,
    name      varchar(20) not null,
    created   timestamp   not null,
    updated   timestamp   not null,
    constraint pk_task_id primary key (id),
    constraint fk_task_tenant_id foreign key (tenant_id) references tenant (id)
);
