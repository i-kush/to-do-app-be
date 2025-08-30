delete
from role_permission
where name in ('GLOBAL_ADMIN', 'TENANT_ADMIN', 'MANAGER', 'USER');

delete
from permission
where name in ('TENANT_READ',
               'TENANT_WRITE',
               'USER_READ',
               'USER_WRITE',
               'PROJECT_READ',
               'PROJECT_WRITE',
               'TASK_READ',
               'TASK_WRITE');

delete
from app_user au using tenant t
where au.tenant_id = t.id
  and t.name = 'system';

delete
from tenant
where name = 'system';

delete
from role
where name in ('GLOBAL_ADMIN', 'TENANT_ADMIN', 'MANAGER', 'USER');