create table olhobjects (
  olho_id varchar(40) not null,
  olho_sch_id varchar(40) not null,
  olho_created timestamp not null,
  olho_object_schema varchar(100),
  olho_object_type varchar(100) not null,
  olho_object_name varchar(100) not null,
  olho_source $(clob)
)
/
alter table olhobjects add constraint olhobject_pk primary key (olho_id)
/
alter table olhobjects add constraint olhobject_sch_fk foreign key (olho_sch_id) references schemas (sch_id) on delete cascade
/
create index olhobject_object_i on olhobjects (olho_sch_id, olho_object_schema, olho_object_type, olho_object_name, olho_created)
/
CREATE INDEX OLHOOBJECT_CREATED_I ON OLHOBJECTS (OLHO_CREATED)
/
alter table olhobjects add olho_description varchar(1000)
/
