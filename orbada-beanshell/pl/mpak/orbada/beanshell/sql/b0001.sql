create table bshactions (
  bsha_id varchar(40) not null,
  bsha_usr_id varchar(40),
  bsha_dtp_id varchar(40),
  bsha_key varchar(100) not null,
  bsha_title varchar(100) not null,
  bsha_tooltip varchar(1000),
  bsha_shortcut varchar(20),
  bsha_script $(clob)
)
/
alter table bshactions add constraint bshaction_pk primary key (bsha_id)
/
alter table bshactions add constraint bshaction_usr_fk foreign key (bsha_usr_id) references users(usr_id) on delete cascade
/
alter table bshactions add constraint bshaction_dtp_fk foreign key (bsha_dtp_id) references driver_types(dtp_id) on delete set null
/
