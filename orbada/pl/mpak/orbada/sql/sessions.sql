create table orbada_sessions (
  oses_id varchar(40) not null,
  oses_start_time timestamp not null,
  oses_end_time timestamp,
  oses_version varchar(20),
  oses_usr_id varchar(40) not null,
  oses_terminal varchar(100)
)
/
alter table orbada_sessions add constraint orbada_session_pk primary key (oses_id)
/
create table schema_sessions (
  sses_id varchar(40) not null,
  sses_oses_id varchar(40) not null,
  sses_start_time timestamp not null,
  sses_end_time timestamp,
  sses_sch_id varchar(40) not null,
  sses_user varchar(100),
  sses_url varchar(1000)
)
/
alter table schema_sessions add constraint schema_session_pk primary key (sses_id)
/
alter table schema_sessions add constraint schema_session_oses_fk foreign key (sses_oses_id) references orbada_sessions (oses_id) on delete cascade
/
