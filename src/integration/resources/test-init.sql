insert into tenant (id, name, created_at, updated_at)
values (uuid_generate_v4(), 'TestTenant', now(), now());

insert into app_user (id, tenant_id, role_id, username, password_hash, email, firstname, lastname, is_locked, created_at, updated_at)
values (uuid_generate_v4(),
        (select id from tenant where name = 'TestTenant' limit 1),
        'TENANT_ADMIN',
        'testenko',
        '$2a$10$mz2d/O6XKhkX5FOJBg3FV.m.UtNLA77CkYCOLtINukM5xKdLPHE7i',
        'testenko@test.com',
        'Test',
        'User',
        false,
        now(),
        now());