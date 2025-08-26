insert into tenant (id, name, created, updated)
values (uuid_generate_v4(), 'TestTenant', now(), now());

insert into app_user (id, tenant_id, username, password_hash, email, firstname, lastname, is_locked, is_activated, created, updated)
values (uuid_generate_v4(),
        (select id from tenant where name = 'TestTenant' limit 1),
        'testenko',
        '$2a$10$mz2d/O6XKhkX5FOJBg3FV.m.UtNLA77CkYCOLtINukM5xKdLPHE7i',
        'testenko@test.com',
        'Test',
        'User',
        false,
        true,
        now(),
        now());