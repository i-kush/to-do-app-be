truncate table app_user cascade;

delete
from tenant t
where t.name != 'system';