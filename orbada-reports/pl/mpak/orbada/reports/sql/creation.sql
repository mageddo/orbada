create table orep_groups (
  orepg_id varchar(40) not null,
  orepg_created timestamp not null,
  orepg_updated timestamp not null,
  orepg_orepg_id varchar(40),
  orepg_dtp_id varchar(40) not null,
  orepg_usr_id varchar(40),
  orepg_sch_id varchar(40),
  orepg_shared varchar(1),
  orepg_name varchar(100) not null,
  orepg_description varchar(4000),
  orepg_tooltip varchar(1000)
)
/
alter table orep_groups add constraint orep_group_pk primary key (orepg_id)
/
alter table orep_groups add constraint orep_group_orepg_fk foreign key (orepg_orepg_id) references orep_groups (orepg_id) on delete cascade
/
alter table orep_groups add constraint orep_group_dtp_fk foreign key (orepg_dtp_id) references driver_types (dtp_id) on delete cascade
/
alter table orep_groups add constraint orep_group_usr_fk foreign key (orepg_usr_id) references users (usr_id) on delete cascade
/
alter table orep_groups add constraint orep_group_sch_fk foreign key (orepg_sch_id) references schemas (sch_id) on delete cascade
/
create table orep_reports (
  orep_id varchar(40) not null,
  orep_created timestamp not null,
  orep_updated timestamp not null,
  orep_orepg_id varchar(40),
  orep_orep_id varchar(40),
  orep_name varchar(100) not null,
  orep_description varchar(4000),
  orep_tooltip varchar(1000),
  orep_type varchar(1) default 'T' not null,
  orep_arrange varchar(1) default 'H' not null,
  orep_sql $(clob)
)
/
alter table orep_reports add constraint orep_report_pk primary key (orep_id)
/
alter table orep_reports add constraint orep_report_orepg_fk foreign key (orep_orepg_id) references orep_groups (orepg_id) on delete cascade
/
alter table orep_reports add constraint orep_report_orep_fk foreign key (orep_orep_id) references orep_reports (orep_id) on delete cascade
/
create table orep_users (
  orepu_id varchar(40) not null,
  orepu_orep_id varchar(40) not null,
  orepu_usr_id varchar(40) not null
)
/
alter table orep_users add constraint orep_user_pk primary key (orepu_id)
/
alter table orep_users add constraint orep_user_orep_fk foreign key (orepu_orep_id) references orep_reports (orep_id) on delete cascade
/
alter table orep_users add constraint orep_user_usr_fk foreign key (orepu_usr_id) references users (usr_id) on delete cascade
/
