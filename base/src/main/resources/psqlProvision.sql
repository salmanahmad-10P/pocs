-- bplane provisioning script of postgresql RDBMS
-- JA Bride :  2 March 2011

-- sample usage :  
--      (as postgres)   :   createdb test
--      (as postgres)   :   createdb test2
--      (as postgres)   :   psql -f $HOME/development/conf/psqlProvision.sql

create user test with password 'test';
alter user test with password 'test';

grant all privileges on database test to test;
grant all privileges on database test2 to test;
