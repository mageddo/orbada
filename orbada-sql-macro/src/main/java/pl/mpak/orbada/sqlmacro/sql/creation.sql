create table osqlmacros (
  osm_id varchar(40) not null,
  osm_usr_id varchar(40),
  osm_name varchar(200) not null,
  osm_regexp varchar(1000) not null,
  osm_resolve varchar(4000) not null,
  osm_order integer
)
/
alter table osqlmacros add constraint osqlmacro_pk primary key (osm_id)
/
alter table osqlmacros add constraint osqlmacro_usr_fk foreign key (osm_usr_id) references users (usr_id) on delete cascade
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C501', 'mailto', '^*((\w+)@(\w+\.)(\w+)(\.\w+)*)^*', 'select ''<html><a href="mailto:$1">$1</a>'' email from dual', 1)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C502', 'SYSDATE', '^*(SYSDATE)^*', 'select $1 from dual', 2)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C503', 'Simple Math Expression', '^*((\(*\s*(\d+|[a-zA-Z_0-9]+)\s*\)*)(\s*[+-/\*]\s*(\(*\s*(\d+|[a-zA-Z_0-9]+)+\s*\)*))+)^*', 'select $1 from dual', 3)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C504', '<table> ORDER BY ...', '^*([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")(\s+ORDER\s+BY\s+(.*))', 'select * from $1$2', 4)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C505', '<table> WHERE ...[ ORDER BY ...]', '^*([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")\s+WHERE\s+(.*)?(\s+ORDER BY\s+(.*))?', 'select * from $1 where $2$3', 5)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C506', 'MAX N<table>[ WHERE ...]', '^*MAX\s+([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")?(\s+WHERE\s+(.*))?', 'select max( N$1 ) from $1$2', 6)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C507', 'MAX <table> BY <column_name>[ WHERE ...]', '^*MAX\s+([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")\s+BY\s+([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")?(\s+WHERE\s+(.*))?', 'select max( $2 ) from $1$3', 7)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C508', 'COUNT <table>[ WHERE ...]', '^*COUNT\s+([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")?(\s+WHERE\s+(.*))?', 'select /*+INDEX_FFS( $1 )*/ count( 0 ) from $1$2', 8)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C509', 'DISTINCT <column_name [, column_name]> FROM <table>[ WHERE ...]', '^*DISTINCT\s+(([a-zA-Z_0-9$#]+|".+")?(\s*,\s*[a-zA-Z_0-9$#]+|".+")*?)\s+FROM\s+([a-zA-Z_0-9$#]+|".+")?(\s+WHERE\s+(.*))?', 'select count( 0 ) "COUNT", $1 from $4$5 group by $1 order by 1 desc', 9)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C510', 'LAST <column_name> FROM <table_name>[ WHERE ...]', '^*LAST\s+([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")\s+FROM\s+([a-zA-Z_0-9$#]+|"[a-zA-Z_0-9$#]+")?(\s+WHERE\s+(.*))?', 'select * from $2 where $1 = (select max( $1 ) from $2$3)', 10)
/
INSERT INTO OSQLMACROS (OSM_ID, OSM_NAME, OSM_REGEXP, OSM_RESOLVE, OSM_ORDER) VALUES ('20081101135749-00000B06CC6FA943-5D83C511', 'uniqueid', '^*UNIQUEID^*', 'select ''$(orbada.unique.id)'' from dual', 11)
/
commit
/
