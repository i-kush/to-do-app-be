truncate table app_user cascade;
truncate table audit;
truncate table project cascade;

delete
from tenant t
where t.name != 'system';