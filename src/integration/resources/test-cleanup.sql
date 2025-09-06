truncate table app_user cascade;
truncate table audit;

delete
from tenant t
where t.name != 'system';