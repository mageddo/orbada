create table snippets (
  snp_id varchar(40) not null,
  snp_usr_id varchar(40),
  snp_dtp_id varchar(40),
  snp_name varchar(100) not null,
  snp_code $(clob) not null,
  snp_editor varchar(20) default 'sql' not null,
  snp_active varchar(1) default 'T' not null,
  snp_immediate varchar(1)
)
/
alter table snippets add constraint snippets_pk primary key (snp_id)
/
alter table snippets add constraint snippets_user_fk foreign key (snp_usr_id) references users (usr_id) on delete cascade
/
alter table snippets add constraint snippets_drivers_fk foreign key (snp_dtp_id) references driver_types (dtp_id) on delete set null
/
