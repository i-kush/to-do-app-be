create extension if not exists "uuid-ossp";

create table if not exists tenant (
    id      uuid        not null default uuid_generate_v4(),
    name    varchar(50) not null,
    created timestamp   not null,
    updated timestamp   not null,
    constraint pk_tenant_id primary key (id),
    constraint unq_tenant_name unique (name)
);

create table if not exists app_user (
    id            uuid         not null default uuid_generate_v4(),
    tenant_id     uuid         not null,
    username      varchar(15) not null,
    password_hash varchar(255) not null,
    email         varchar(50) not null,
    firstname     varchar(50) not null,
    lastname      varchar(50) not null,
    is_locked     boolean,
    is_activated  boolean      not null default false,
    created       timestamp    not null,
    updated       timestamp    not null,
    constraint pk_user_id primary key (id),
    constraint unq_user_tenant_id_username unique (tenant_id, username),
    constraint unq_user_tenant_id_email unique (tenant_id, email),
    constraint fk_user_tenant_id foreign key (tenant_id) references tenant (id)
);

create table if not exists role (
    id          uuid         not null default uuid_generate_v4(),
    tenant_id   uuid         not null,
    name        varchar(100) not null,
    description varchar(255),
    created     timestamp    not null,
    updated     timestamp    not null,
    constraint pk_role_id primary key (id),
    constraint unq_role_tenant_id_name unique (tenant_id, name),
    constraint fk_role_tenant_id foreign key (tenant_id) references tenant (id)
);

create table if not exists permission (
    id          uuid         not null default uuid_generate_v4(),
    name        varchar(100) not null,
    description varchar(255),
    created     timestamp    not null,
    updated     timestamp    not null,
    constraint pk_permission_id primary key (id),
    constraint unq_permission_name unique (name)
);

create table if not exists role_permission (
    role_id       uuid not null,
    permission_id uuid not null,
    constraint pk_role_permission_id primary key (role_id, permission_id),
    constraint fk_role_permission_role_id foreign key (role_id) references role (id),
    constraint fk_role_permission_permission_id foreign key (permission_id) references permission (id)
);

create table if not exists user_role (
    user_id uuid not null,
    role_id uuid not null,
    constraint pk_user_role_id primary key (user_id, role_id),
    constraint fk_user_role_user_id foreign key (user_id) references app_user (id),
    constraint fk_user_role_role_id foreign key (role_id) references role (id)
);

create table if not exists project (
    id          uuid         not null default uuid_generate_v4(),
    tenant_id   uuid         not null,
    name        varchar(50)  not null,
    description varchar(255) not null,
    status      varchar(10)  not null,
    created     timestamp    not null,
    updated     timestamp    not null,
    constraint pk_project_id primary key (id),
    constraint fk_project_tenant_id foreign key (tenant_id) references tenant (id)
);

create table if not exists task (
    id               uuid         not null default uuid_generate_v4(),
    tenant_id        uuid         not null,
    project_id       uuid         not null,
    name             varchar(50)  not null,
    description      varchar(255) not null,
    assigned_user_id uuid,
    status           varchar(10)  not null,
    created          timestamp    not null,
    updated          timestamp    not null,
    constraint pk_task_id primary key (id),
    constraint fk_task_tenant_id foreign key (tenant_id) references tenant (id),
    constraint fk_task_user_id foreign key (assigned_user_id) references app_user (id),
    constraint fk_task_project_id foreign key (project_id) references project (id)
);
