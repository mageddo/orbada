create table osqlscripts (
  oss_id varchar(40) not null,
  oss_usr_id varchar(40) not null,
  oss_name varchar(100) not null,
  oss_dtp_id varchar(40) not null,
  oss_script $(clob)
)
/
alter table osqlscripts add constraint osqlscript_pk primary key (oss_id)
/
alter table osqlscripts add constraint osqlscript_dtp_fk foreign key (oss_dtp_id) references driver_types (dtp_id)
/
alter table osqlscripts add constraint osqlscript_usr_fk foreign key (oss_usr_id) references users (usr_id)
/
create index osqlscript_name_i on osqlscripts (oss_name)
/
