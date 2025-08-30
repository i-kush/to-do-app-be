insert into role (name)
values ('GLOBAL_ADMIN'),
       ('TENANT_ADMIN'),
       ('MANAGER'),
       ('USER');

insert into permission (name)
values ('TENANT_READ'),
       ('TENANT_WRITE'),
       ('USER_READ'),
       ('USER_WRITE'),
       ('PROJECT_READ'),
       ('PROJECT_WRITE'),
       ('TASK_READ'),
       ('TASK_WRITE');

-- All permissions to the global admin
insert into role_permission (role_id, permission_id)
select 'GLOBAL_ADMIN', p.name
from permission p;

-- Granulated permissions for other roles
insert into role_permission (role_id, permission_id)
values ('TENANT_ADMIN', 'USER_READ'),
       ('TENANT_ADMIN', 'USER_WRITE'),
       ('TENANT_ADMIN', 'PROJECT_READ'),
       ('TENANT_ADMIN', 'PROJECT_WRITE'),
       ('TENANT_ADMIN', 'TASK_READ'),
       ('TENANT_ADMIN', 'TASK_WRITE'),
       ('MANAGER', 'PROJECT_READ'),
       ('MANAGER', 'PROJECT_WRITE'),
       ('MANAGER', 'TASK_READ'),
       ('MANAGER', 'TASK_WRITE'),
       ('USER', 'PROJECT_READ'),
       ('USER', 'TASK_READ'),
       ('USER', 'TASK_WRITE');

insert into tenant(name, created_at, updated_at)
values ('system', now(), now());

insert into app_user(tenant_id,
                     role_id,
                     username,
                     password_hash,
                     email,
                     firstname,
                     lastname,
                     is_locked,
                     created_at,
                     updated_at)
values ((select t.id from tenant t where t.name = 'system'),
        'GLOBAL_ADMIN', 'global-admin',
        '$2a$10$S81SqYD3/ElUNRr8KU0NH.84M0I0eKW2J8bsODidc5GqsXXNAZ0n2',
        'system-admin@kush-to-do.com',
        'global',
        'admin',
        false,
        now(),
        now());
