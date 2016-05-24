create table og_queryinfos (
  gqi_id varchar(40) not null,
  gqi_usr_id varchar(40),
  gqi_name varchar(100) not null,
  gqi_dtp_id varchar(40),
  gqi_sql $(clob) not null
)
/
alter table og_queryinfos add constraint og_queryinfo_pk primary key (gqi_id)
/
alter table og_queryinfos add constraint og_queryinfo_usr_fk foreign key (gqi_usr_id) references users (usr_id) on delete cascade
/
alter table og_queryinfos add constraint og_queryinfo_dtp_fk foreign key (gqi_dtp_id) references driver_types (dtp_id) on delete set null
/
create table og_queryinfo_perspectives (
  qip_id varchar(40) not null,
  qip_gqi_id varchar(40) not null,
  qip_pps_id varchar(40) not null,
  qip_interval_s integer default 60 not null,
  qip_order integer default 1
)
/
alter table og_queryinfo_perspectives add constraint og_queryinfo_perspective_pk primary key (qip_id)
/
alter table og_queryinfo_perspectives add constraint og_queryinfo_persp_usr_fk foreign key (qip_gqi_id) references og_queryinfos (gqi_id) on delete cascade
/
alter table og_queryinfo_perspectives add constraint og_queryinfo_persp_pps_fk foreign key (qip_pps_id) references perspectives (pps_id) on delete cascade
/
insert into og_queryinfos (gqi_id, gqi_usr_id, gqi_name, gqi_dtp_id, gqi_sql) values ('20081014175904-00000323EFC72840-A5D0F901', null, 'SYSDATE', (select dtp_id from driver_types where upper(dtp_name) = 'ORACLE'), 'select ''Data i godzina'' title, ''<b>''||to_char(SYSDATE, ''yyyy-mm-dd hh24:mi:ss'')||''</b>'' value from dual')
/
insert into og_queryinfos (gqi_id, gqi_usr_id, gqi_name, gqi_dtp_id, gqi_sql) values ('20081014175904-00000323EFC72840-A5D0F902', null, 'SID, SERIAL', (select dtp_id from driver_types where upper(dtp_name) = 'ORACLE'), 'select ''SID, SERIAL'' title, cast((select sid||'', ''||serial# from v$session where audsid = userenv( ''SESSIONID'' )) as varchar(40)) value from dual')
/
insert into og_queryinfos (gqi_id, gqi_usr_id, gqi_name, gqi_dtp_id, gqi_sql) values ('20081014175904-00000323EFC72840-A5D0F903', null, 'Iloœæ sesji', (select dtp_id from driver_types where upper(dtp_name) = 'ORACLE'), 'select ''Il. sesji/aktyw'' title, count( 0 )||''/''||(count( case when username is not null and status = ''ACTIVE'' then 0 else null end ) -1) value, case when count( case when username is not null and status = ''ACTIVE'' then 0 else null end ) -1 >= 5 then ''bgcolor=red'' else null end attr from v$session')
/
insert into og_queryinfos (gqi_id, gqi_usr_id, gqi_name, gqi_dtp_id, gqi_sql) values ('20081014175904-00000323EFC72840-A5D0F904', null, 'CURRENT_TIME', (select dtp_id from driver_types where upper(dtp_name) = 'HSQLDB'), 'select ''Data i godzina'' title, ''<b>''||current_date||'' ''||current_time||''</b>'' value from dual')
/
COMMIT
/
