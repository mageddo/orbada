/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public static String getTableList(String filter, boolean outerJoin) {
    return String.format(
      "select tbl.owner schema_name, tbl.table_name, tcm.comments as remarks, tbl.cluster_name, tbl.tablespace_name,\n" +
      "       case when tbl.temporary = 'Y' then 'YES' else 'NO' end temporary,\n" +
      "       case when trim(tbl.cache) = 'Y' then 'YES' else 'NO' end cache,\n" +
      "       case when (select 'Y' from all_external_tables where owner = tbl.owner and table_name = tbl.table_name) = 'Y' then 'EXTERNAL' else 'TABLE' end table_type\n" +
      "  from all_tables tbl, all_tab_comments tcm\n" +
      " where tbl.table_name = tcm.table_name" +(outerJoin ? "(+)" : "") +"\n" +
      "   and tbl.owner = :SCHEMA_NAME\n" +
      "   and tcm.owner" +(outerJoin ? "(+)" : "") +" = tbl.owner\n" +
      "%1$s" +
      "union all\n" +
      "select tbl.owner schema_name, tbl.table_name, tcm.comments as remarks, tbl.cluster_name, tbl.tablespace_name,\n" +
      "       case when tbl.temporary = 'Y' then 'YES' else 'NO' end temporary,\n" +
      "       case when trim(tbl.cache) = 'Y' then 'YES' else 'NO' end cache,\n" +
      "       'OBJECT' table_type\n" +
      "  from all_object_tables tbl, all_tab_comments tcm\n" +
      " where tbl.table_name = tcm.table_name" +(outerJoin ? "(+)" : "") +"\n" +
      "   and tbl.owner = :SCHEMA_NAME\n" +
      "   and tcm.owner" +(outerJoin ? "(+)" : "") +" = tbl.owner\n" +
      "%1$s" +
      " order by schema_name, table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableTypeInfo() {
    return
      "select nvl((select 'EXTERNAL' from all_external_tables where owner = t.owner and table_name = t.table_name), 'TABLE') table_type\n" +
      "  from all_tables t\n" +
      " where t.owner = :SCHEMA_NAME\n" +
      "   and t.table_name = :TABLE_NAME\n" +
      "union all\n" +
      "select 'OBJECT' table_type from all_object_tables\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME";
  }
  
  public static String getTableInfo() {
    return
      "select * from all_tables\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME";
  }
  
  public static String getTableInfo(String schemaName, String tableName) {
    return
      "select * from all_tables\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and table_name = '" +tableName +"'";
  }
  
  public static String getObjectTableInfo(String schemaName, String tableName) {
    return
      "select * from all_object_tables\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and table_name = '" +tableName +"'";
  }
  
  public static String getExternalTableInfo(String schemaName, String tableName) {
    return
      "select * from all_external_tables\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and table_name = '" +tableName +"'";
  }
  
  public static String getTableComment() {
    return "select comments from all_tab_comments where owner = :SCHEMA_NAME and table_name = :TABLE_NAME";
  }

  public static String getTableColumnComment() {
    return "select comments from all_col_comments where owner = :SCHEMA_NAME and table_name = :TABLE_NAME and column_name = :COLUMN_NAME";
  }
  
  public static String getTablePrimaryKey() {
    return
      "select cons.constraint_name, ccol.position, ccol.column_name\n" +
      "  from all_constraints cons, all_cons_columns ccol\n" +
      " where cons.table_name = :TABLE_NAME\n" +
      "   and cons.owner = :SCHEMA_NAME\n" +
      "   and ccol.table_name = cons.table_name\n" +
      "   and ccol.constraint_name = cons.constraint_name \n" +
      "   and ccol.owner = cons.owner \n" +
      "   and cons.constraint_type = 'P'\n" +
      " order by ccol.position";
  }
  
  public static String getColumnList(String filter) {
    return String.format(
      "select cols.owner schema_name, cols.table_name, cols.column_id, cols.column_name, data_type, data_length, data_scale, data_precision,\n" +
      "       data_type||\n" +
      "       decode( data_precision,\n" +
      "         null, decode( data_type,\n" +
      "           'DATE', '', 'NUMBER', '', 'BLOB', '', 'CLOB', '', 'XMLTYPE', '', decode(cols.data_type_owner, null, null, data_type), '',\n" +
      "           decode( data_length, 0, '', '('||data_length||decode(char_used, 'B', ' BYTE', 'C', 'CHAR', NULL)||')' ) ),\n" +
      "         '('||data_precision||decode( nvl( data_scale, 0 ), 0, ')', ','||data_scale||')' ) ) as display_type,\n" +
      "       case when nullable = 'Y' then 'YES' else 'NO' end nullable, data_default, comments remarks, cc.position pk_position\n" +
      "  from all_col_comments coms, all_tab_columns cols,\n" +
      "       (select acc.column_name, acc.position, c.constraint_type\n" +
      "          from all_cons_columns acc,\n" +
      "               (select constraint_name, constraint_type\n" +
      "                  from all_constraints cons\n" +
      "                 where cons.table_name = :TABLE_NAME\n" +
      "                   and cons.owner = :SCHEMA_NAME\n" +
      "                   and cons.constraint_type = 'P') c\n" +
      "         where acc.owner = :SCHEMA_NAME\n" +
      "           and acc.table_name = :TABLE_NAME\n" +
      "           and acc.constraint_name = c.constraint_name) cc\n" +
      " where cols.table_name = :TABLE_NAME\n" +
      "   and cols.owner = :SCHEMA_NAME\n" +
      "   and cols.owner = coms.owner\n" +
      "   and coms.table_name = cols.table_name\n" +
      "   and coms.column_name = cols.column_name\n" +
      "   and coms.column_name = cc.column_name(+)\n" +
      "%s" +
      " order by schema_name, table_name, column_id",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getColumnNameList() {
    return
      "select column_name\n" +
      "  from all_tab_columns\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      " order by 1";
  }

  public static String getIndexList(String filter, boolean withExpression) {
    return String.format(
      "select uidx.owner schema_name, uic.table_name, uidx.index_name, decode( uidx.uniqueness, 'UNIQUE', 'YES', 'NO' ) as uniqueness,\n" +
      "       uic.column_name, " +(withExpression ? "ie.column_expression, " : "") +"uic.column_position, uidx.table_owner, uidx.status, uidx.tablespace_name,\n" +
      "       uidx.initial_extent, uidx.next_extent, uidx.min_extents, \n" +
      "       uidx.max_extents, uidx.pct_increase, uidx.distinct_keys, \n" +
      "       uidx.pct_free, uidx.index_type||case when uidx.ityp_owner is not null then ' (\"'||uidx.ityp_owner||'\".\"'||uidx.ityp_name||'\")' end index_type\n" +
      "  from all_indexes uidx, all_ind_columns uic" +(withExpression ? ", all_ind_expressions ie" : "") +"\n" +
      " where uic.index_owner = :SCHEMA_NAME\n" +
      "   and (uic.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "   and uic.index_name = uidx.index_name\n" +
      "   and uic.index_owner = uidx.owner\n" +
      "   and uic.table_name = uidx.table_name\n" +
      (withExpression ?
        "   and ie.index_owner(+) = uic.index_owner\n" +
        "   and ie.index_name(+) = uic.index_name\n" +
        "   and ie.column_position(+) = uic.column_position\n" :
        "") +
      "%s" +
      " order by schema_name, table_name, index_name, column_position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getAllIndexList(String filter) {
    return String.format(
      "select i.owner schema_name, i.index_name, i.index_type, i.table_owner, i.table_name, i.table_type, \n" +
      "       decode(i.uniqueness, 'UNIQUE', 'YES', 'NO') uniqueness, i.status, i.tablespace_name,\n" +
      "       (select decode(c.constraint_type, 'P', 'PK', 'U', 'UK', 'V', 'CV', null)||' '||c.status from all_constraints c where c.owner = i.table_owner and c.table_name = i.table_name and c.constraint_name = i.index_name) constraint,\n" +
      "       i.initial_extent, i.next_extent, i.min_extents, i.max_extents, i.pct_increase, i.distinct_keys, i.pct_free\n" +
      "  from all_indexes i\n" +
      " where i.owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by schema_name, index_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getIndexColumnList(String filter) {
    return String.format(
      "select uic.table_name, uic.column_name, ie.column_expression, uic.column_position, uic.descend\n" +
      "  from all_ind_columns uic, all_ind_expressions ie\n" +
      " where uic.table_owner = :SCHEMA_NAME\n" +
      "   and uic.index_name = :INDEX_NAME\n" +
      "   and ie.index_owner(+) = uic.index_owner\n" +
      "   and ie.index_name(+) = uic.index_name\n" +
      "   and ie.column_position(+) = uic.column_position\n" +
      "%s" +
      " order by column_position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getIndexInfo() {
    return
      "select *\n" +
      "  from all_indexes\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and index_name = :INDEX_NAME";
  }

  public static String getIndexInfo(String schemaName, String indexName) {
    return
      "select *\n" +
      "  from all_indexes\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and index_name = '" +indexName +"'";
  }

  public static String getConstraintList(String filter) {
    return String.format(
      "select cons.owner schema_name, cons.table_name, cons.constraint_name,\n" +
      "       decode( cons.constraint_type,\n" +
      "         'C', 'Check',\n" +
      "         'P', 'Primary Key',\n" +
      "         'R', 'Foreign Key',\n" +
      "         'U', 'Unique Key',\n" +
      "         'V', 'Check on view') as constraint_type,\n" +
      "       cons.r_owner r_schema_name, cons.r_constraint_name, cons.status, cons.delete_rule,\n" +
      "       ccol.column_name column_name, ccol.position, cons.deferrable, cons.deferred,\n" +
      "       cons.search_condition, cons.validated, cons.generated\n" +
      "  from all_constraints cons, all_cons_columns ccol\n" +
      " where cons.owner = :SCHEMA_NAME\n" +
      "   and (cons.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "   and ccol.table_name(+) = cons.table_name\n" +
      "   and ccol.constraint_name(+) = cons.constraint_name\n" +
      "   and ccol.owner(+) = :SCHEMA_NAME\n" +
      "%s" +
      " order by schema_name, table_name, constraint_name, position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getConstraintListForScript() {
    return
      "select owner, constraint_name, constraint_type, r_constraint_name, delete_rule,\n" +
      "       search_condition, status, deferrable, deferred, validated, r_owner\n" +
      "  from all_constraints cons\n" +
      " where cons.table_name = :TABLE_NAME\n" +
      "   and cons.owner = :SCHEMA_NAME\n" +
      "   and cons.generated <> 'GENERATED NAME'\n" +
      " order by decode( constraint_type, 'C', 3, 'R', 2, 'U', 1, 'P', 0 ), cons.constraint_name";
  }

  public static String getConstraintColumnListForScript() {
    return
      "select owner, table_name, column_name\n" +
      "  from all_cons_columns\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and constraint_name = :CONSTRAINT_NAME\n" +
      " order by position";
  }

  public static String getIndexListForScript() {
    return
      "select i.owner, i.index_name, i.uniqueness, i.table_owner, i.table_name\n" +
      "  from all_indexes i\n" +
      " where i.owner = :SCHEMA_NAME\n" +
      "   and i.table_name = :TABLE_NAME\n" +
      "   and not exists\n" +
      "       (select constraint_type\n" +
      "          from all_constraints\n" +
      "         where owner = i.owner\n" +
      "           and constraint_name = i.index_name)\n" +
      " order by decode( uniqueness, 'UNIQUE', 0, 1 ), i.index_name";
  }

  public static String getIndexColumnListForScript() {
    return
      "select substr( column_name, 1, 250 ) as column_name, descend\n" +
      "  from all_ind_columns\n" +
      " where index_owner = :SCHEMA_NAME\n" +
      "   and index_name = :INDEX_NAME\n" +
      " order by column_position";
  }

  public static String getPrivilegeList(String filter) {
    return String.format(
      "select table_schema, table_name, privilege, grantee, grantable, grantor\n" +
      "  from all_tab_privs\n" +
      " where table_name = :TABLE_NAME\n" +
      "   and table_schema = :SCHEMA_NAME\n" +
      "%s" +
      " order by table_schema, table_name, grantee",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getColumnPrivilegeList(String filter) {
    return String.format(
      "select table_schema, table_name, column_name, privilege, grantee, grantable, grantor\n" +
      "  from all_col_privs\n" +
      " where table_schema = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      "   and (column_name = :COLUMN_NAME or :COLUMN_NAME is null)\n" +
      "%s" +
      " order by table_schema, table_name, column_name, grantee",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableTriggerList(String filter) {
    return String.format(
      "select allt.owner schema_name, allt.trigger_name, allt.trigger_type, allt.triggering_event, \n" +
      "       allt.when_clause, allt.status as enabled, allo.status, allt.table_owner table_schema, allt.table_name\n" +
      "  from all_objects allo, all_triggers allt\n" +
      " where allo.object_type = 'TRIGGER' \n" +
      "   and allt.table_owner = :SCHEMA_NAME\n" +
      "   and (allt.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "   and allo.owner = allt.owner \n" +
      "   and allo.object_name = allt.trigger_name\n" +
      "%s",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTriggerSource() {
    return 
      "select trigger_name, trigger_body, description, replace( when_clause, chr( 0 ) ) when_clause\n" +
      "  from all_triggers\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and trigger_name = :TRIGGER_NAME";
  }

  public static String getTableReferencesWithList(String filter) {
    return String.format(
      "select b1.owner schema_name, b1.table_name, c1.column_name, b1.constraint_name, \n" +
      "       a1.owner as r_schema_name, a1.table_name r_table_name, d1.column_name as r_column_name, a1.constraint_name r_constraint_name,\n" +
      "       b1.status, c1.position\n" +
      "  from all_constraints a1, all_constraints b1, all_cons_columns c1, all_cons_columns d1\n" +
      " where a1.constraint_name = b1.r_constraint_name \n" +
      "   and a1.owner = b1.r_owner\n" +
      "   and a1.constraint_type in ('P', 'U')\n" +
      "   and c1.table_name = b1.table_name\n" +
      "   and c1.constraint_name = b1.constraint_name\n" +
      "   and c1.owner = b1.owner\n" +
      "   and b1.table_name = :TABLE_NAME\n" +
      "   and b1.owner = :SCHEMA_NAME\n" +
      "   and d1.constraint_name = a1.constraint_name\n" +
      "   and d1.table_name = a1.table_name\n" +
      "   and d1.owner = a1.owner\n" +
      "   and nvl( c1.position, 0 ) = nvl( d1.position, 0 )\n" +
      "%s" +
      " order by schema_name, table_name, constraint_name, position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableReferencedFromList(String filter) {
    return String.format(
      "select b.owner schema_name, b.table_name, b.column_name column_name, b.constraint_name,\n" +
      "       a.owner r_schema_name, a.table_name r_table_name, c.column_name r_column_name, a.constraint_name r_constraint_name,\n" +
      "       a.status, c.position\n" +
      "  from all_constraints a, all_cons_columns b, all_cons_columns c\n" +
      " where a.r_constraint_name = b.constraint_name\n" +
      "   and a.r_owner = b.owner\n" +
      "   and a.constraint_type = 'R'\n" +
      "   and b.table_name = :TABLE_NAME\n" +
      "   and b.owner = :SCHEMA_NAME\n" +
      "   and c.table_name = a.table_name\n" +
      "   and c.owner = a.owner\n" +
      "   and c.constraint_name = a.constraint_name\n" +
      "   and nvl( b.position, 0 ) = nvl( c.position, 0 )\n" +
      "%s" +
      " order by r_schema_name, r_table_name, r_constraint_name, position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getUsedByList(String filter) {
    return String.format(
      "select owner schema_name, name object_name, type object_type, dependency_type,\n" +
      "       (select status from all_objects where owner = d.owner and object_type = d.type and object_name = d.name) status\n" +
      "  from all_dependencies d\n" +
      " where referenced_owner = :SCHEMA_NAME\n" +
      "   and referenced_name = :OBJECT_NAME\n" +
      "   and referenced_type = :OBJECT_TYPE\n" +
      "   and referenced_type <> 'NON-EXISTENT'\n" +
      "   and owner <> 'SYS'\n" +
      "   and name <> 'DUAL'\n" +
      "%s" +
      " order by schema_name, object_name, object_type",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getUsingList(String filter) {
    return String.format(
      "select referenced_owner schema_name, referenced_name object_name, referenced_type object_type, dependency_type,\n" +
      "       (select status from all_objects where owner = d.referenced_owner and object_type = d.referenced_type and object_name = d.referenced_name) status\n" +
      "  from all_dependencies d\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and type = :OBJECT_TYPE\n" +
      "   and name = :OBJECT_NAME\n" +
      "   and referenced_owner <> 'SYS'\n" +
      "   and referenced_type not in ('NON-EXISTENT', 'SYNONYM')\n" +
      "   and referenced_name <> 'DUAL'\n" +
      "%s" +
      " order by schema_name, object_name, object_type",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableModificationList(String filter) {
    return String.format(
      "select table_owner schema_name, table_name, partition_name, subpartition_name, inserts, updates, deletes, timestamp, truncated, drop_segments\n" +
      "  from all_tab_modifications\n" +
      " where table_name = :TABLE_NAME\n" +
      "   and table_owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by schema_name, table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableModificationList(String schemaName, String tableName) {
    return 
      "select table_owner schema_name, table_name, partition_name, subpartition_name, inserts, updates, deletes, timestamp, truncated, drop_segments\n" +
      "  from all_tab_modifications\n" +
      " where table_name = '" +tableName +"'\n" +
      "   and table_owner = '" +schemaName +"'";
  }

  public static String getSegmentSizeList() {
    return 
      "select segment_name, partition_name, tablespace_name,\n" +
      "       sum(bytes) SIZE_IN_BYTES,\n" +
      "       round(sum(bytes)/1024/1024,2) SIZE_IN_MB,\n" +
      "       sum(decode(extent_id, 0, bytes, 0)) INITIAL_EXTENTS,\n" +
      "       sum(decode(extent_id, 1, bytes, 0)) NEXT_EXTENTS,\n" +
      "       max(extent_id)+1 NUMBER_EXTENTS\n" +
      "  from user_extents\n" +
      " where segment_type = :SEGMENT_TYPE\n" +
      "   and segment_name = :SEGMENT_NAME\n" +
      " group by segment_name, partition_name, tablespace_name";
  }

  public static String getDbaSegmentSizeList() {
    return 
      "select segment_name, partition_name, tablespace_name,\n" +
      "       sum(bytes) SIZE_IN_BYTES,\n" +
      "       round(sum(bytes)/1024/1024,2) SIZE_IN_MB,\n" +
      "       sum(decode(extent_id, 0, bytes, 0)) INITIAL_EXTENTS,\n" +
      "       sum(decode(extent_id, 1, bytes, 0)) NEXT_EXTENTS,\n" +
      "       max(extent_id)+1 NUMBER_EXTENTS\n" +
      "  from dba_extents\n" +
      " where owner = :SEGMENT_OWNER\n" +
      "   and segment_type = :SEGMENT_TYPE\n" +
      "   and segment_name = :SEGMENT_NAME\n" +
      " group by segment_name, partition_name, tablespace_name";
  }

  public static String getSegmentSizeList(String segmentType, String segmentName) {
    return 
      "select segment_name, partition_name, tablespace_name,\n" +
      "       sum(bytes) SIZE_IN_BYTES,\n" +
      "       round(sum(bytes)/1024/1024,2) SIZE_IN_MB,\n" +
      "       sum(decode(extent_id, 0, bytes, 0)) INITIAL_EXTENTS,\n" +
      "       sum(decode(extent_id, 1, bytes, 0)) NEXT_EXTENTS,\n" +
      "       max(extent_id)+1 NUMBER_EXTENTS\n" +
      "  from user_extents\n" +
      " where segment_type = '" +segmentType +"'\n" +
      "   and segment_name = '" +segmentName +"'\n" +
      " group by segment_name, partition_name, tablespace_name";
  }

  public static String getDbaSegmentSizeList(String segmentType, String segmentOwner, String segmentName) {
    return 
      "select segment_name, partition_name, tablespace_name,\n" +
      "       sum(bytes) SIZE_IN_BYTES,\n" +
      "       round(sum(bytes)/1024/1024,2) SIZE_IN_MB,\n" +
      "       sum(decode(extent_id, 0, bytes, 0)) INITIAL_EXTENTS,\n" +
      "       sum(decode(extent_id, 1, bytes, 0)) NEXT_EXTENTS,\n" +
      "       max(extent_id)+1 NUMBER_EXTENTS\n" +
      "  from dba_extents\n" +
      " where owner = '" +segmentOwner +"'\n" +
      "   and segment_type = '" +segmentType +"'\n" +
      "   and segment_name = '" +segmentName +"'\n" +
      " group by segment_name, partition_name, tablespace_name";
  }
  
  public static String getTablePartitionList(String filter) {
    return String.format(
      "select table_owner, table_name, partition_name, high_value, tablespace_name, pct_free, pct_used, pct_increase,\n" +
      "       initial_extent, next_extent, min_extent, max_extent\n" +
      "  from all_tab_partitions\n" +
      " where table_owner = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      "%s" +
      " order by partition_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getIndexPartitionList(String filter) {
    return String.format(
      "select index_owner, index_name, partition_name, high_value, status, tablespace_name, \n" +
      "       pct_free, pct_increase, initial_extent, next_extent, min_extent, max_extent\n" +
      "  from all_ind_partitions\n" +
      " where index_owner = :SCHEMA_NAME\n" +
      "   and index_name = :INDEX_NAME\n" +
      "%s" +
      " order by index_owner, index_name, partition_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getDictionaryList(String filter) {
    return String.format(
      "select *\n" +
      "  from dict\n" +
      "%s" +
      " order by table_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getDictionaryColumnList(String filter) {
    return String.format(
      "select *\n" +
      "  from dict_columns\n" +
      " where table_name = :TABLE_NAME\n" +
      "%s" +
      " order by table_name, column_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getAllObjectList() {
    return
      "select *\n" +
      "  from (select owner schema_name, object_name, subobject_name, object_type, created, last_ddl_time, timestamp, status, temporary, generated\n" +
      "          from all_objects)\n";
  }
  
  public static String getObjectsType() {
    return getObjectsType(null);
  }
  
  public static String getObjectsType(String filter) {
    return String.format(
      getAllObjectList() +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and object_name = :OBJECT_NAME\n" +
      "%s" +
      " order by schema_name, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectsSearch(String filter) {
    return getAllObjectList() +String.format(
      " where (schema_name = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (object_name = :OBJECT_NAME or :OBJECT_NAME is null or object_name like '%%'||upper(:OBJECT_NAME)||'%%')\n" +
      "%s" +
      " order by schema_name, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getSourceSearch(String filter) {
    return String.format(
      "select s.owner schema_name, s.name object_name, s.type object_type, o.status, s.line, instr( upper( s.text ), upper( :USER_TEXT ) ) as position, \n" +
      "       '<html>'||substr(substr( s.text, 1, instr( upper( s.text ), upper( :USER_TEXT ) ) -1 ), 1, 1900)||'<b>'||substr( s.text, instr( upper( s.text ), upper( :USER_TEXT ) ), length( :USER_TEXT ) )||'</b>'||substr(substr( s.text, instr( upper( s.text ), upper( :USER_TEXT ) ) +length( :USER_TEXT ) ), 1, 1900) text\n" +
      "  from all_source s, all_objects o\n" +
      " where (s.owner = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and upper( s.text ) like '%%'|| upper( :USER_TEXT ) ||'%%' escape '\\'\n" +
      "   and o.owner = s.owner\n" +
      "   and o.object_name = s.name\n" +
      "   and o.object_type = s.type\n" +
      "%s" +
      " order by object_type, object_name, line",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getSourceSearchRegExpr(String filter) {
    return String.format(
      "select s.owner schema_name, s.name object_name, s.type object_type, o.status, s.line, REGEXP_INSTR(s.text, :USER_TEXT) as position,\n" +
      "       '<html>'||substr(substr(s.text, 1, REGEXP_INSTR(s.text, :USER_TEXT)), 1, 1900)||'<b>'||REGEXP_SUBSTR(s.text, :USER_TEXT)||'</b>'||substr(substr(s.text, REGEXP_INSTR(s.text, :USER_TEXT) +length(REGEXP_SUBSTR(s.text, :USER_TEXT))), 1, 1900) text\n" +
      "  from all_source s, all_objects o\n" +
      " where (s.owner = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and REGEXP_LIKE( s.text, :USER_TEXT )\n" +
      "   and o.owner = s.owner\n" +
      "   and o.object_name = s.name\n" +
      "   and o.object_type = s.type\n" +
      "%s" +
      " order by object_type, object_name, line",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectsName() {
    return getAllObjectList() +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and object_type = :OBJECT_TYPE\n" +
      " order by schema_name, object_name";
  }
  
  public static String getCurrentSchema() {
    return "select user schema_name from dual";
  }
  
  public static String getSchemaList() {
    return 
      "select username schema_name from all_users\n" +
      "union select 'PUBLIC' from dual\n" +
      "union select 'CONNECT' from dual\n" +
      "union select 'RESOURCE' from dual\n" +
      "union select 'PLUSTRACE' from dual\n" +
      "order by 1";
  }

  public static String getFullSchemaList() {
    return "select username schema_name, user_id, created from all_users union all select 'PUBLIC', null, null from dual order by schema_name";
  }

  public static String getBannerList() {
    return "select banner from v$version";
  }
  
  public static String getDbmsOutputEnable() {
    return "begin dbms_output.enable( :BYTES ); end;";
  }

  public static String getDbmsOutputDisable() {
    return "begin dbms_output.disable; end;";
  }
  
  public static String getDbmsOutputLine() {
    return "begin SYS.DBMS_OUTPUT.GET_LINE(:TEXT, :STATUS); end;";
  }
  
  public static String getDbaRole() {
    return "select role from session_roles where role='DBA' union select 'DBA' from session_privs where privilege='SYSDBA'";
  }
  
  public static String getSetModule() {
    return "begin sys.dbms_application_info.set_module( 'Orbada Oracle Plugin', null ); end;";
  }
  
  public static String getSequenceList(String filter) {
    return String.format(
      "select sequence_owner schma_name, sequence_name, min_value, max_value, increment_by,\n" +
      "       decode( cycle_flag, 'Y', 'YES', 'NO' ) as cycle_flag,\n" +
      "       decode( order_flag, 'Y', 'YES', 'NO' ) as order_flag,\n" +
      "       cache_size, last_number\n" +
      "  from all_sequences\n" +
      " where sequence_owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by sequence_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getRecyclebinList(String filter) {
    return String.format(
      "select *\n" +
      "  from recyclebin\n" +
      "%s" +
      " order by original_name, droptime desc",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }
  
  public static String getDbaRecyclebinList(String filter) {
    return String.format(
      "select *\n" +
      "  from dba_recyclebin\n" +
      " where (owner = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "%s" +
      " order by owner, original_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectDDL() {
    return "select dbms_metadata.get_ddl(:OBJECT_TYPE, :OBJECT_NAME, :SCHEMA_NAME, :VERSION) source from dual";
  }
  
  public static String getObjectPlSql() {
    return
      "select case when instr(text, ' wrapped'||chr(10)) > 0 then substr(text, 1, instr(text, ' wrapped'||chr(10)) +7) else text end source from all_source\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and type = :OBJECT_TYPE\n" +
      "   and name = :OBJECT_NAME\n" +
      " order by line";
  }
  
  public static String getTableComments() {
    return
      "select 'COMMENT ON TABLE \"'||owner||'\".\"'||table_name||'\" IS '''||comments||'''' source\n" +
      "  from all_tab_comments\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      "   and comments is not null\n" +
      "union all\n" +
      "select 'COMMENT ON COLUMN \"'||owner||'\".\"'||table_name||'\".\"'||column_name||'\" IS '''||comments||'''' source\n" +
      "  from all_col_comments\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      "   and comments is not null";
  }
  
  public static String getIndexSource() {
    return
      "select dbms_metadata.get_ddl('INDEX', i.index_name, i.owner, :VERSION) source\n" +
      "  from all_indexes i\n" +
      " where i.table_owner = :SCHEMA_NAME\n" +
      "   and i.table_name = :TABLE_NAME\n" +
      "   and not exists (select * from all_constraints c where c.owner = i.owner and c.table_name = i.table_name and c.constraint_name = i.index_name and c.constraint_type = 'P')";
  }
  
  public static String getAllTriggerList(String filter, int oraVer) {
    return String.format(
      "SELECT ALLT.OWNER SCHEMA_NAME, ALLT.TRIGGER_NAME, ALLT.TRIGGER_TYPE, ALLT.TABLE_OWNER, ALLT.TABLE_NAME, ALLT.TRIGGERING_EVENT, ALLO.CREATED, ALLO.LAST_DDL_TIME,\n" +
      "       SUBSTR( ALLT.WHEN_CLAUSE, 1, 254 ) WHEN_CLAUSE,\n" +
      "       ALLT.STATUS AS ENABLED,\n" +
      "       DECODE(ALLO.STATUS, 'INVALID', 'INVALID', DECODE(APOS.PARAM_VALUE, 'DEBUG', 'DEBUG', 'VALID')) STATUS, ALLT.TABLE_OWNER\n" +
      "  FROM (SELECT STATUS, OBJECT_NAME, CREATED, LAST_DDL_TIME\n" +
      "          FROM ALL_OBJECTS\n" +
      "         WHERE OBJECT_TYPE = 'TRIGGER'\n" +
      "           AND OWNER = :SCHEMA_NAME) ALLO, ALL_TRIGGERS ALLT,\n" +
      "       (SELECT 'DEBUG' PARAM_VALUE, OBJECT_NAME\n" +
      "          FROM ALL_STORED_SETTINGS\n" +
      "         WHERE %2$s\n" +
      "           AND OBJECT_TYPE = 'TRIGGER'\n" +
      "           AND OWNER = :SCHEMA_NAME) APOS\n" +
      " WHERE ALLT.OWNER = :SCHEMA_NAME\n" +
      "   AND ALLO.OBJECT_NAME = ALLT.TRIGGER_NAME\n" +
      "   AND ALLT.TRIGGER_NAME = APOS.OBJECT_NAME(+)    \n" +
      "%1$s" +
      " order by SCHEMA_NAME, trigger_name",
      new Object[]{
        (filter != null ? "   and " + filter + "\n" : ""),
        (oraVer >= 10 ? "PARAM_NAME = 'plsql_debug' and PARAM_VALUE = 'TRUE'" : "PARAM_NAME = 'plsql_compiler_flags' AND INSTR(PARAM_VALUE, 'NON_DEBUG') = 0")
      });
  }

  public static String getTriggerInfo() {
    return
      "select * from all_triggers\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and trigger_name = :TRIGGER_NAME";
  }
  
  public static String getTriggerInfo(String schemaName, String triggerName) {
    return
      "select * from all_triggers\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and trigger_name = '" +triggerName +"'";
  }
  
  public static String getTriggerStatus() {
    return
      "select status, case when status = 'ENABLED' then 'DISABLE' else 'ENABLE' end nstatus from all_triggers\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and trigger_name = :TRIGGER_NAME";
  }
  
  public static String getTriggerColumnList(String filter) {
    return String.format(
      "select trigger_owner schema_name, trigger_name, table_owner, table_name, column_name, column_list, column_usage\n" +
      "  from all_trigger_cols\n" +
      " where trigger_owner = :SCHEMA_NAME\n" +
      "   and trigger_name = :TRIGGER_NAME\n" +
      "%s" +
      " order by column_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectErrorList() {
    return
      "select e.line, e.position, e.text\n" +
      "  from all_errors e\n" +
      " where e.owner = :SCHEMA_NAME\n" +
      "   and e.name = :OBJECT_NAME\n" +
      "   and e.type = :OBJECT_TYPE\n" +
      " order by sequence";
  }
  
  public static String getTriggerErrorList() {
    return
      "select e.line, e.position, e.text, (select t.description from all_triggers t where t.owner = e.owner and t.trigger_name = e.name and e.type = 'TRIGGER') description\n" +
      "  from all_errors e\n" +
      " where e.owner = :SCHEMA_NAME\n" +
      "   and e.name = :OBJECT_NAME\n" +
      "   and e.type = :OBJECT_TYPE\n" +
      " order by sequence";
  }
  
  public static String getTablespaceList() {
    return
      "select tablespace_name, block_size, initial_extent, next_extent, min_extents, max_extents, status, contents\n" +
      "  from user_tablespaces\n" +
      " order by tablespace_name";
  }
  
  public static String getDbaTablespaceList() {
    return
      "select tablespace_name, block_size, initial_extent, next_extent, min_extents, max_extents, status, contents\n" +
      "  from dba_tablespaces\n" +
      " order by tablespace_name";
  }
  
  public static String getSynonymList(String filter) {
    return String.format(
      "select syns.owner schema_name, syns.synonym_name, syns.table_owner, syns.table_name, syns.db_link,\n" +
      "       nvl((select object_type from all_objects o where o.owner = syns.table_owner and o.object_name = syns.table_name and rownum = 1), '<UNKNOWN>') object_type,\n" +
      "       nvl((select status from all_objects o where o.owner = syns.table_owner and o.object_name = syns.table_name and rownum = 1), '<UNKNOWN>') status\n" +
      "  from all_synonyms syns\n" +
      " where syns.owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by synonym_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getDbLinkList(String filter) {
    return String.format(
      "select owner schema_name, db_link, username, host, created\n" +
      "  from all_db_links\n" +
      " where owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by db_link",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getViewList(String filter) {
    return String.format(
      "select o.owner schema_name, o.object_name view_name, o.status, o.created, o.last_ddl_time, c.comments remarks\n" +
      "  from all_objects o, all_tab_comments c\n" +
      " where o.object_type = 'VIEW'\n" +
      "   and o.owner = :SCHEMA_NAME\n" +
      "   and c.owner(+) = :SCHEMA_NAME\n" +
      "   and c.table_type(+) = o.object_type\n" +
      "   and c.table_name(+) = o.object_name\n" +
      "%s" +
      " order by view_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getViewInfo() {
    return
      "select v.*, o.status, o.created, o.last_ddl_time, c.comments\n" +
      "  from all_objects o, all_tab_comments c, all_views v\n" +
      " where o.object_type = 'VIEW'\n" +
      "   and o.owner = :SCHEMA_NAME\n" +
      "   and o.object_name = :VIEW_NAME\n" +
      "   and c.owner(+) = :SCHEMA_NAME\n" +
      "   and c.table_type(+) = o.object_type\n" +
      "   and c.table_name(+) = o.object_name\n" +
      "   and v.owner = :SCHEMA_NAME\n" +
      "   and v.view_name = o.object_name";
  }
  
  public static String getViewSource() {
    return
      "select view_name, text\n" +
      "  from all_views\n" +
      " where view_name = :VIEW_NAME\n" +
      "   and owner = :SCHEMA_NAME";
  }
  
  public static String getSnapshotSource() {
    return
      "select query text, refresh_mode\n" +
      "  from all_snapshots\n" +
      " where name = :VIEW_NAME\n" +
      "   and owner = :SCHEMA_NAME";
  }
  
  public static String getViewColumnDestList() {
    return
      "select column_name\n" +
      "  from all_tab_columns\n" +
      " where table_name = :VIEW_NAME\n" +
      "   and owner = :SCHEMA_NAME\n" +
      " order by column_id";
  }
  
  public static String getViewInfo(String schemaName, String viewName) {
    return
      "select v.*, o.status, o.created, o.last_ddl_time, c.comments\n" +
      "  from all_objects o, all_tab_comments c, all_views v\n" +
      " where o.object_type = 'VIEW'\n" +
      "   and o.owner = '" +schemaName +"'\n" +
      "   and o.object_name = '" +viewName +"'\n" +
      "   and c.owner(+) = '" +schemaName +"'\n" +
      "   and c.table_type(+) = o.object_type\n" +
      "   and c.table_name(+) = o.object_name\n" +
      "   and v.owner = '" +schemaName +"'\n" +
      "   and v.view_name = o.object_name";
  }
  
  public static String getViewColumnList(String filter) {
    return String.format(
      "select cols.owner schema_name, cols.table_name, cols.column_id, cols.column_name, data_type, data_length, data_scale, data_precision,\n" +
      "       data_type||\n" +
      "       decode( data_precision,\n" +
      "         null, decode( data_type,\n" +
      "           'DATE', null, 'NUMBER', null, 'BLOB', null, 'CLOB', null, 'XMLTYPE', null, decode(cols.data_type_owner, null, null, data_type), null,\n" +
      "           decode( data_length, 0, '', '('||data_length||decode(char_used, 'B', ' BYTE', 'C', 'CHAR', NULL)||')' ) ),\n" +
      "         '('||data_precision||decode( nvl( data_scale, 0 ), 0, ')', ','||data_scale||')' ) ) as display_type,\n" +
      "       case when nullable = 'Y' then 'YES' else 'NO' end nullable, comments remarks, upd.updatable\n" +
      "  from all_col_comments coms, all_updatable_columns upd, all_tab_columns cols\n" +
      " where cols.table_name = :VIEW_NAME\n" +
      "   and cols.owner = :SCHEMA_NAME\n" +
      "   and cols.owner = coms.owner\n" +
      "   and upd.owner = cols.owner\n" +
      "   and upd.table_name = cols.table_name\n" +
      "   and upd.column_name = cols.column_name\n" +
      "   and coms.table_name = cols.table_name\n" +
      "   and coms.column_name = cols.column_name\n" +
      "%s" +
      " order by schema_name, table_name, column_id",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getNlsCharsetList() {
    return
      "SELECT rn id, NLS_CHARSET_NAME(rn) name\n" +
      "  FROM (select rownum rn from all_objects)\n" +
      " where NLS_CHARSET_NAME(rn) is not null";
  }
  
  public static String getSessionPrivilegeList() {
    return "SELECT privilege from session_privs order by privilege";
  }
  
  public static String getSystemPrivilegeMapList() {
    return "select name, privilege, property from system_privilege_map order by name";
  }

  public static String getDirectoryList() {
    return "select owner, directory_name, directory_path from all_directories order by directory_name";
  }

  public static String getDirectoryList(String filter) {
    return String.format(
      "select r.grantee read_for, r.grantor read_from, decode(r.privilege, 'READ', 'YES', 'NO') read,\n" +
      "       w.grantee write_for, w.grantor write_from, decode(w.privilege, 'WRITE', 'YES', 'NO') write,\n" +
      "       d.owner, d.directory_name, d.directory_path, o.status, o.created\n" +
      "  from all_directories d, all_tab_privs r, all_tab_privs w, all_objects o\n" +
      " where d.owner = r.table_schema(+)\n" +
      "   and d.directory_name = r.table_name(+)\n" +
      "   and r.privilege(+) = 'READ'\n" +
      "   and d.owner = w.table_schema(+)\n" +
      "   and d.directory_name = w.table_name(+)\n" +
      "   and w.privilege(+) = 'WRITE'\n" +
      "   and (r.grantee = :SCHEMA_NAME or w.grantee = :SCHEMA_NAME)\n" +
      "   and o.owner = d.owner\n" +
      "   and o.object_name = d.directory_name\n" +
      "%s" +
      " order by directory_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getStoredParametersList(String filter) {
    return String.format(
      "select object_type, param_name, param_value\n" +
      "  from all_stored_settings\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and object_type = :OBJECT_TYPE\n" +
      "   and object_name = :OBJECT_NAME\n" +
      "%s" +
      " order by object_type, param_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getTypeList(String filter, int oraVer) {
    return String.format(
      "SELECT *\n" +
      "  FROM (SELECT ALLT.OWNER SCHEMA_NAME, ALLT.TYPE_NAME, ALLT.TYPECODE,\n" +
      "               DECODE(AH.STATUS, 'INVALID', AH.STATUS, DECODE(DH.PARAM_VALUE, 'DEBUG', DH.PARAM_VALUE, AH.STATUS)) HEAD_STATUS,\n" +
      "               AH.CREATED HEAD_CREATED, AH.LAST_DDL_TIME HEAD_LAST_DDL_TIME,\n" +
      "               DECODE(AB.STATUS, 'INVALID', AB.STATUS, DECODE(DB.PARAM_VALUE, 'DEBUG', DB.PARAM_VALUE, AB.STATUS)) BODY_STATUS,\n" +
      "               AB.CREATED BODY_CREATED, AB.LAST_DDL_TIME BODY_LAST_DDL_TIME\n" +
      "          FROM (SELECT STATUS, OBJECT_NAME, CREATED, LAST_DDL_TIME\n" +
      "                  FROM ALL_OBJECTS\n" +
      "                 WHERE OBJECT_TYPE = 'TYPE'\n" +
      "                   AND OWNER = :SCHEMA_NAME) AH,\n" +
      "               (SELECT STATUS, OBJECT_NAME, CREATED, LAST_DDL_TIME\n" +
      "                  FROM ALL_OBJECTS\n" +
      "                 WHERE OBJECT_TYPE = 'TYPE BODY'\n" +
      "                   AND OWNER = :SCHEMA_NAME) AB,\n" +
      "               ALL_TYPES ALLT,\n" +
      "               (SELECT 'DEBUG' PARAM_VALUE, OBJECT_NAME\n" +
      "                  FROM ALL_STORED_SETTINGS\n" +
      "                 WHERE %2$s\n" +
      "                   AND OBJECT_TYPE = 'TYPE'\n" +
      "                   AND OWNER = :SCHEMA_NAME) DH,\n" +
      "               (SELECT 'DEBUG' PARAM_VALUE, OBJECT_NAME\n" +
      "                  FROM ALL_STORED_SETTINGS\n" +
      "                 WHERE %2$s\n" +
      "                   AND OBJECT_TYPE = 'TYPE BODY'\n" +
      "                   AND OWNER = :SCHEMA_NAME) DB\n" +
      "         WHERE ALLT.OWNER = :SCHEMA_NAME\n" +
      "           AND AH.OBJECT_NAME(+) = ALLT.TYPE_NAME\n" +
      "           AND AB.OBJECT_NAME(+) = ALLT.TYPE_NAME\n" +
      "           AND ALLT.TYPE_NAME = DH.OBJECT_NAME(+)\n" +
      "           AND ALLT.TYPE_NAME = DB.OBJECT_NAME(+))\n" +
      "%1$s" +
      " order by SCHEMA_NAME, TYPE_NAME",
      new Object[]{
        (filter != null ? " where " + filter + "\n" : ""),
        (oraVer >= 10 ? "PARAM_NAME = 'plsql_debug' and PARAM_VALUE = 'TRUE'" : "PARAM_NAME = 'plsql_compiler_flags' AND INSTR(PARAM_VALUE, 'NON_DEBUG') = 0")
      });
  }
  
  public static String getTypeInfo(String schemaName, String typeName) {
    return
      "select * from all_types\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and type_name = '" +typeName +"'";
  }
  
  public static String getTypeAttrList(String filter) {
    return String.format(
      "select attr_no, attr_name, attr_type_name, length, precision, scale\n" +
      "  from all_type_attrs\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and type_name = :TYPE_NAME\n" +
      "%s" +
      " order by attr_no",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getTypeMethodList(String filter) {
    return String.format(
      "select distinct method_no, method_name, method_type method_range, parameters, \n" +
      "       decode(results, 0, 'PROCEDURE', 'FUNCTION') method_type\n" +
      "  from all_type_methods\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and type_name = :TYPE_NAME\n" +
      "%s" +
      " order by method_no",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getPackageMethodArgumentList(String filter) {
    return String.format(
      "select overload, position, argument_name, data_type, default_value, decode(position, 0, 'RESULT', in_out) in_out\n" +
      "  from all_arguments\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and package_name = :PACKAGE_NAME\n" +
      "   and object_name = :METHOD_NAME\n" +
      "   and (overload = :OVERLOAD or :OVERLOAD is null)\n" +
      "%s" +
      " order by overload, position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getPackageDebugList(String filter, int oraVer) {
    return String.format(
      "select *\n" +
      "  from (select ho.owner schema_name, ho.object_name package_name,\n" +
      "               decode(ho.status, 'INVALID', 'INVALID', decode((select 'DEBUG' from all_stored_settings where owner = ho.owner and object_type = ho.object_type and object_name = ho.object_name and %2$s), 'DEBUG', 'DEBUG', ho.status)) head_status,\n" +
      "               ho.created head_created, ho.last_ddl_time head_last_ddl_time,\n" +
      "               decode(bo.status, 'INVALID', 'INVALID', decode((select 'DEBUG' from all_stored_settings where owner = bo.owner and object_type = bo.object_type and object_name = bo.object_name and %2$s), 'DEBUG', 'DEBUG', bo.status)) body_status,\n" +
      "               bo.created body_created, bo.last_ddl_time body_last_ddl_time\n" +
      "          from all_objects ho, all_objects bo\n" +
      "         where ho.owner = :SCHEMA_NAME\n" +
      "           and ho.object_type = 'PACKAGE'\n" +
      "           and bo.owner(+) = :SCHEMA_NAME\n" +
      "           and bo.object_type(+) = 'PACKAGE BODY'\n" +
      "           and bo.object_name(+) = ho.object_name)\n" +
      "%1$s" +
      " order by package_name",
      new Object[] {
        (filter != null ? " where " + filter + "\n" : ""),
        (oraVer >= 10 ? "PARAM_NAME = 'plsql_debug' and PARAM_VALUE = 'TRUE'" : "PARAM_NAME = 'plsql_compiler_flags' AND INSTR(PARAM_VALUE, 'NON_DEBUG') = 0")
      });
  }
  
  public static String getPackageList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select ho.owner schema_name, ho.object_name package_name,\n" +
      "               ho.status head_status,\n" +
      "               ho.created head_created, ho.last_ddl_time head_last_ddl_time,\n" +
      "               bo.status body_status,\n" +
      "               bo.created body_created, bo.last_ddl_time body_last_ddl_time\n" +
      "          from all_objects ho, all_objects bo\n" +
      "         where ho.owner = :SCHEMA_NAME\n" +
      "           and ho.object_type = 'PACKAGE'\n" +
      "           and bo.owner(+) = :SCHEMA_NAME\n" +
      "           and bo.object_type(+) = 'PACKAGE BODY'\n" +
      "           and bo.object_name(+) = ho.object_name)\n" +
      "%s" +
      " order by package_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }
  
  public static String getPackageMethodList(String filter) {
    return String.format(
      "select p.procedure_name method_name, a.overload, decode(count(decode(a.position, 0, 1, null)), 0, 'PROCEDURE', 'FUNCTION') method_type\n" +
      "  from all_procedures p, all_arguments a\n" +
      " where p.owner = :SCHEMA_NAME\n" +
      "   and p.object_name = :PACKAGE_NAME\n" +
      "   and a.owner = p.owner\n" +
      "   and a.package_name = p.object_name\n" +
      "   and a.object_name = p.procedure_name\n" +
      " group by p.procedure_name, a.overload\n" +
      "%s" +
      " order by method_name, overload",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectInfo(String schemaName, String triggerName) {
    return
      "select * from all_objects\n" +
      " where owner = '" +schemaName +"'\n" +
      "   and object_name = '" +triggerName +"'";
  }
  
  public static String getMethodList(String filter) {
    return String.format(
      "select distinct object_name method_name\n" +
      "  from all_procedures\n" +
      " where owner = :SCHEMA_NAME\n" +
      "union\n" +
      "select distinct procedure_name method_name\n" +
      "  from all_procedures\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and procedure_name is not null\n" +
      "%s" +
      " order by 1",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getFunctionList(String filter, int oraVer) {
    return String.format(
      "select *\n" +
      "  from (select p.owner schema_name, p.object_name object_name,\n" +
      "               decode(p.status, 'INVALID', 'INVALID', decode((select 'DEBUG' from all_stored_settings where owner = p.owner and object_type = p.object_type and object_name = p.object_name and %2$s), 'DEBUG', 'DEBUG', p.status)) status,\n" +
      "               p.created created, p.last_ddl_time last_ddl_time\n" +
      "          from all_objects p\n" +
      "         where p.owner = :SCHEMA_NAME\n" +
      "           and p.object_type = :FUNCTION_TYPE)\n" +
      "%1$s" +
      " order by object_name",
      new Object[] {
        (filter != null ? " where " + filter + "\n" : ""),
        (oraVer >= 10 ? "PARAM_NAME = 'plsql_debug' and PARAM_VALUE = 'TRUE'" : "PARAM_NAME = 'plsql_compiler_flags' AND INSTR(PARAM_VALUE, 'NON_DEBUG') = 0")
      });
  }
  
  public static String getFunctionArgumentList(String filter) {
    return String.format(
      "select position, argument_name, data_type, default_value, decode(position, 0, 'RESULT', in_out) in_out\n" +
      "  from all_arguments\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and object_name = :FUNCTION_NAME\n" +
      "%s" +
      " order by position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getDbParamsViewCount() {
    return "select count( 0 ) cnt from all_objects where object_name in ('GV$PARAMETER')";
  }
  
  public static String getDatabaseParameters() {
    return 
      "select name, value\n" +
      "  from gv$parameter\n" +
      " where name in ('plsql_debug')";
  }
  
  public static String getMViewList(String filter) {
    return String.format(
      "select o.owner schema_name, o.object_name view_name, o.status, o.created, o.last_ddl_time\n" +
      "  from all_objects o\n" +
      " where o.object_type = 'MATERIALIZED VIEW'\n" +
      "   and o.owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by view_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getMViewInfo(String schemaName, String viewName) {
    return
      "select v.*, o.status, o.created, o.last_ddl_time\n" +
      "  from all_objects o, all_mviews v\n" +
      " where o.object_type = 'MATERIALIZED VIEW'\n" +
      "   and o.owner = '" +schemaName +"'\n" +
      "   and o.object_name = '" +viewName +"'\n" +
      "   and v.owner = '" +schemaName +"'\n" +
      "   and v.mview_name = o.object_name";
  }
  
  public static String getAllTableList(String filter) {
    return String.format(
      "select tbl.owner schema_name, tbl.table_name, tcm.comments as remarks, tbl.cluster_name, tbl.tablespace_name,\n" +
      "       case when tbl.temporary = 'Y' then 'YES' else 'NO' end temporary,\n" +
      "       case when trim(tbl.cache) = 'Y' then 'YES' else 'NO' end cache\n" +
      "  from all_all_tables tbl, all_tab_comments tcm\n" +
      " where tbl.table_name = tcm.table_name\n" +
      "   and tbl.owner = :SCHEMA_NAME\n" +
      "   and tcm.owner = tbl.owner\n" +
      "%1$s" +
      " order by schema_name, table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getAllTableColumnList(String filter) {
    return String.format(
      "select cols.owner schema_name, cols.table_name, cols.column_id, cols.column_name,\n" +
      "       data_type||\n" +
      "       decode( data_precision,\n" +
      "         null, decode( data_type,\n" +
      "           'DATE', null, 'NUMBER', null, 'BLOB', null, 'CLOB', null, 'XMLTYPE', null, decode(cols.data_type_owner, null, null, data_type), null,\n" +
      "           decode( data_length, 0, '', '('||data_length||decode(char_used, 'B', ' BYTE', 'C', 'CHAR', NULL)||')' ) ),\n" +
      "         '('||data_precision||decode( nvl( data_scale, 0 ), 0, ')', ','||data_scale||')' ) ) as display_type,\n" +
      "       case when nullable = 'Y' then 'YES' else 'NO' end nullable\n" +
      "  from all_tab_columns cols\n" +
      " where cols.table_name = :TABLE_NAME\n" +
      "   and cols.owner = :SCHEMA_NAME\n" +
      "%s" +
      " order by table_name, column_id",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaList(String filter) {
    return String.format(
      "select o.owner, o.object_name name, o.created, o.last_ddl_time, o.status\n" +
      "  from all_objects o\n" +
      " where o.owner = :SCHEMA_NAME\n" +
      "   and o.object_type = :OBJECT_TYPE\n" +
      "%s" +
      " order by name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaInfo(String schemaName, String objectName, String objectType) {
    return String.format(
      "select /*+ ORDERED */ \n" +
      "       j.*, o.created, o.last_ddl_time, o.status\n" +
      "  from all_objects o, all_java_classes j\n" +
      " where o.owner = '" +schemaName +"'\n" +
      "   and o.owner = j.owner\n" +
      "   and o.object_name = '" +objectName +"'\n" +
      "   and o.object_name = j.name\n" +
      "   and o.object_type = '" +objectType +"'");
  }

  public static String getJavaFieldList(String filter) {
    return String.format(
      "select owner, field_index, name, field_name, accessibility, is_static, is_final, is_volatile, field_class\n" +
      "  from all_java_fields\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and name = :OBJECT_NAME\n" +
      "%s" +
      " order by field_index",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaImplementList(String filter) {
    return String.format(
      "select owner, name, interface_index, interface_name\n" +
      "  from all_java_implements\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and name = :OBJECT_NAME\n" +
      "%s" +
      " order by interface_index",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaInnerList(String filter) {
    return String.format(
      "select /*+ ORDERED */ \n" +
      "       j.inner_index, j.owner, j.name, j.simple_name, i.accessibility, i.is_interface\n" +
      "  from all_java_inners j, all_java_inners i\n" +
      " where j.owner = :SCHEMA_NAME\n" +
      "   and j.name = :OBJECT_NAME\n" +
      "   and i.owner = j.owner\n" +
      "   and i.name = j.full_name\n" +
      "%s" +
      " order by inner_index",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaMethodThrowList(String filter) {
    return String.format(
      "select method_index, owner, method_name, exception_index, exception_class\n" +
      "  from all_java_throws j\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and name = :OBJECT_NAME\n" +
      "   and (method_index = :METHOD_INDEX or :METHOD_INDEX is null)\n" +
      "%s" +
      " order by method_index, exception_index",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaMethodList(String filter) {
    return String.format(
      "select distinct method_index, owner, name, method_name, accessibility, is_static, is_final, is_abstract,\n" +
      "       decode(return_class, '-', base_type, return_class) return_type\n" +
      "  from all_java_methods j\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and name = :OBJECT_NAME\n" +
      "%s" +
      " order by method_index",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getJavaMethodArgumentList(String filter) {
    return String.format(
      "select owner, name, method_index, method_name, argument_position, \n" +
      "       decode(argument_class, '-', base_type, argument_class) argument_type\n" +
      "  from all_java_arguments\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and name = :OBJECT_NAME\n" +
      "   and method_index = :METHOD_INDEX\n" +
      "%s" +
      " order by argument_position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getExplainPlan() {
    return
      "SELECT LEVEL, \n" +
      "        '<html><font color=gray>'||LEVEL||'</font> <b>'||OPERATION||'</b>'||\n" +
      "        DECODE( OTHER_TAG, NULL, '', '*'||DECODE( OBJECT_NODE, NULL, '', ' ['||OBJECT_NODE||']' ) )||\n" +
      "        DECODE( OPTIONS, NULL, '', ' ['||\n" +
      "          DECODE(OPTIONS, \n" +
      "            'FULL', '<font color=red>'||OPTIONS||'</font>', \n" +
      "            'FULL SCAN', '<font color=maroon>'||OPTIONS||'</font>',\n" +
      "            'FAST FULL SCAN', '<font color=maroon>'||OPTIONS||'</font>',\n" +
      "            'UNIQUE SCAN', '<font color=green>'||OPTIONS||'</font>', \n" +
      "            'RANGE SCAN', '<font color=green>'||OPTIONS||'</font>', \n" +
      "          OPTIONS)||']' )||\n" +
      "        DECODE( OPTIMIZER, NULL, '', ' '||OPTIMIZER )||\n" +
      "        DECODE( OBJECT_TYPE, NULL, '', ' '||OBJECT_TYPE )||\n" +
      "        DECODE( OBJECT_NAME, NULL, '', ' <b>'||OBJECT_OWNER||'.'||OBJECT_NAME||'</b>'||DECODE( OPERATION, 'TABLE ACCESS', '['||OBJECT_INSTANCE||']', NULL ) )||\n" +
      "        DECODE(PARTITION_START, NULL, '', ' Part=[Start='||PARTITION_START||' Stop='||PARTITION_STOP||']')||\n" +
      "        DECODE(COST, NULL, '', ' [G=<font color=blue>'||COST||'</font>'||\n" +
      "          DECODE(IO_COST, NULL, '', ' IO=<font color=blue>'||IO_COST)||'</font>'||\n" +
      "          DECODE(CPU_COST, NULL, '', ' CPU=<font color=blue>'||CPU_COST)||'</font>'||\n" +
      "          DECODE( CARDINALITY, NULL, '', ' ROWS=<font color=blue>'||CARDINALITY )||'</font>'||\n" +
      "          DECODE( BYTES, NULL, '', ', <font color=blue>'||\n" +
      "            DECODE( TRUNC( BYTES /1024 ), 0, BYTES||' B',\n" +
      "              DECODE( TRUNC( BYTES /(1024 *1024) ), 0, ROUND( BYTES /1024, 2 )||' KB',\n" +
      "                DECODE( TRUNC( BYTES /(1024 *1024 *1024) ), 0, ROUND( BYTES /(1024 *1024), 2 )||' MB',\n" +
      "                  DECODE( TRUNC( BYTES /(1024 *1024 *1024 *1024) ), 0, ROUND( BYTES /(1024 *1024 *1024), 2 )||' GB', TO_CHAR( BYTES, '0.0EEEE' ) ) ) ) )||'</font>' )||']' )||\n" +
      "        '  <font color=gray>'||\n" +
      "        DECODE(ACCESS_PREDICATES, NULL, '', ' '||replace(SUBSTR(ACCESS_PREDICATES, 1, 1000), '<', '&lt;'))||\n" +
      "        DECODE(FILTER_PREDICATES, NULL, '', ' '||replace(SUBSTR(FILTER_PREDICATES, 1, 1000), '<', '&lt;'))||\n" +
      "        '</font>'\n" +
      "        PLAN_OUTPUT,\n" +
      "        OPERATION, OPTIONS, ACCESS_PREDICATES, FILTER_PREDICATES\n" +
      "   FROM PLAN_TABLE\n" +
      "  START WITH ID = 0\n" +
      "    AND STATEMENT_ID = :STATEMENT_ID\n" +
      "CONNECT BY PRIOR ID = PARENT_ID\n" +
      "    AND STATEMENT_ID = :STATEMENT_ID\n" +
      "  ORDER BY ID, POSITION";
  }
  
  public static String getExplainXPlan() {
    return "SELECT * FROM TABLE(dbms_xplan.display('PLAN_TABLE', :STATEMENT_ID, 'ALL'))";
  }
  
  public static String getExceptionsTable() {
    return 
      "select count(0), owner schema_name, table_name\n" +
      "  from all_tab_columns\n" +
      " where column_name in ('ROW_ID', 'OWNER', 'TABLE_NAME', 'CONSTRAINT')\n" +
      " group by owner, table_name\n" +
      "having count(0) >= 4";
  }
  
  public static String getJobList(String filter, boolean dbaRole) {
    return String.format(
      "select job, schema_user schema_name, last_date, this_date, next_date, round(total_time, 5) total_time, broken, interval, failures, what, nls_env, instance\n" +
      "  from " +(dbaRole ? "dba_jobs" : "user_jobs") +"\n" +
      "%s" +
      " order by this_date, next_date",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }
  
  public static String getPrimaryKeyColumns() {
    return
      "SELECT CCOL.COLUMN_NAME\n" +
      "  FROM ALL_CONSTRAINTS CONS, ALL_CONS_COLUMNS CCOL\n" +
      " WHERE CONS.OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND CONS.TABLE_NAME = :TABLE_NAME\n" +
      "   AND CCOL.TABLE_NAME(+) = CONS.TABLE_NAME\n" +
      "   AND CCOL.CONSTRAINT_NAME(+) = CONS.CONSTRAINT_NAME\n" +
      "   AND CCOL.OWNER(+) = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND CONS.CONSTRAINT_TYPE = 'P'\n" +
      " ORDER BY CCOL.POSITION";
  }
  
  public static String getSourceCreatorAll() {
    return
      "begin\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'PRETTY', &PRETTY);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'SQLTERMINATOR', &SQLTERMINATOR);\n" +
      "end;";
  }
  
  public static String getSourceCreatorTable() {
    return
      "begin\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'SEGMENT_ATTRIBUTES', &SEGMENT_ATTRIBUTES);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'STORAGE', &STORAGE);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'TABLESPACE', &TABLESPACE);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'CONSTRAINTS_AS_ALTER', &CONSTRAINTS_AS_ALTER);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'CONSTRAINTS', &CONSTRAINTS);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'REF_CONSTRAINTS', &REF_CONSTRAINTS);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'SIZE_BYTE_KEYWORD', &SIZE_BYTE_KEYWORD);\n" +
      "end;";
  }
  
  public static String getSourceCreatorIndex() {
    return
      "begin\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'SEGMENT_ATTRIBUTES', &SEGMENT_ATTRIBUTES);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'STORAGE', &STORAGE);\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'TABLESPACE', &TABLESPACE);\n" +
      "end;";
  }
  
  public static String getSourceCreatorView() {
    return
      "begin\n" +
      "  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'FORCE', &FORCE);\n" +
      "end;";
  }
  
  public static String getIndextypeNameList() {
    return
      "select '\"'||OWNER||'\".\"'||INDEXTYPE_NAME||'\"' name\n" +
      "  from ALL_INDEXTYPES\n" +
      "union all select cast(null as varchar(100)) from dual\n" +
      " order by 1 nulls first";
  }

  public static String getArgumentsForCall() {
    return
      "select nvl(argument_name, 'RESULT') argument_name, position, \n" +
      "       case when type_name is not null then '\"'||type_owner||'\".\"'||type_name||'\"' else data_type end data_type, in_out\n" +
      "  from all_arguments\n" +
      " where owner = :SCHEMA_NAME\n" +
      "   and case when package_name is not null then package_name||'.' end||object_name = :OBJECT_NAME\n" +
      "   and not (nvl(argument_name, 'RESULT') = 'SELF' and type_name is not null)\n" +
      "   and (overload = :OVERLOAD or :OVERLOAD is null)\n" +
      " order by position";
  }

  public static String getObjectForCall() {
    return
      "select *\n" +
      "  from (select owner schema_name, case when procedure_name is not null then object_name else procedure_name end package_name,\n" +
      "               nvl(procedure_name, object_name) object_name\n" +
      "          from all_procedures\n" +
      "         where owner = :SCHEMA_NAME)\n" +
      " where case when package_name is not null then package_name||'.' end||object_name = :OBJECT_NAME";
  }

  public static String getHighlightTableList() {
    return "select object_name from all_objects where owner = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') and object_type in ('TABLE', 'VIEW', 'MATERIALIZED VIEW')";
  }

  public static String getHighlightTableFromSynonymsList() {
    return "select synonym_name from all_synonyms s, all_objects o where s.owner = UPPER(SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')) and o.owner = s.table_owner and o.object_name = s.table_name and o.object_type IN ('TABLE', 'VIEW', 'MATERIALIZED VIEW')";
  }

  public static String getHighlightUserFunctionList() {
    return "select object_name method_name from all_procedures where owner = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') union select procedure_name method_name from all_procedures where owner = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') and procedure_name is not null";
  }

  public static String getHighlightUserFunctionFromSynonymsList() {
    return "select synonym_name from all_synonyms s, all_objects o where s.owner = UPPER(SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')) and o.owner = s.table_owner and o.object_name = s.table_name and o.object_type IN ('FUNCTION', 'PROCEDURE', 'PACKAGE')";
  }

  public static String getHighlightPublicTableList() {
    return "select table_name from dict";
  }

  public static String getHighlightTypeList() {
    return "select object_name from all_objects where owner = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') and object_type = 'TYPE' and object_name not like 'SYS\\_PLSQL\\_%' escape '\\'";
  }

  public static String getHighlightTypeFromSynonymsList() {
    return "select synonym_name from all_synonyms s, all_objects o where s.owner = UPPER(SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')) and o.owner = s.table_owner and o.object_name = s.table_name and o.object_type = 'TYPE'";
  }

  public static String resolveObject() {
    return
      "SELECT OBJECT_TYPE, OWNER SCHEMA_NAME, OBJECT_NAME, RANK\n" +
      "  FROM (SELECT OBJECT_TYPE, OWNER, OBJECT_NAME, 0 RANK\n" +
      "          FROM SYS.ALL_OBJECTS\n" +
      "         WHERE OBJECT_NAME = :OBJECT_NAME\n" +
      "           AND UPPER(OWNER) = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "           AND OBJECT_TYPE NOT IN ( 'SYNONYM' )\n" +
      "        UNION ALL\n" +
      "        SELECT AO.OBJECT_TYPE, AO.OWNER, AO.OBJECT_NAME, DECODE(UPPER(SYN.OWNER), NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')),10,20)\n" +
      "          FROM SYS.ALL_OBJECTS AO, SYS.ALL_SYNONYMS SYN\n" +
      "         WHERE AO.OWNER = SYN.TABLE_OWNER\n" +
      "           AND AO.OBJECT_NAME = SYN.TABLE_NAME\n" +
      "           AND SYN.SYNONYM_NAME = :OBJECT_NAME\n" +
      "        UNION ALL\n" +
      "        SELECT 'SCHEMA', NULL, USERNAME, 30\n" +
      "          FROM ALL_USERS\n" +
      "         WHERE USERNAME = :OBJECT_NAME)\n" +
      " WHERE OBJECT_TYPE NOT IN ('PACKAGE BODY', 'TYPE BODY')\n" +
      " ORDER BY RANK";
  }

  public static String autocompleteSchemaObjects() {
    return
      "SELECT OWNER, OBJECT_NAME, OBJECT_TYPE, \n" +
      "       DECODE(OBJECT_TYPE,\n" +
      "         'TABLE', (SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE OWNER = O.OWNER AND TABLE_NAME = O.OBJECT_NAME), \n" +
      "         'VIEW', (SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE OWNER = O.OWNER AND TABLE_NAME = O.OBJECT_NAME), \n" +
      "         'SYNONYM', (SELECT TABLE_OWNER||'.'||TABLE_NAME FROM USER_SYNONYMS WHERE SYNONYM_NAME = O.OBJECT_NAME),\n" +
      "         'SEQUENCE', 'LAST VALUE '||(SELECT LAST_NUMBER FROM ALL_SEQUENCES WHERE SEQUENCE_OWNER = O.OWNER AND SEQUENCE_NAME = O.OBJECT_NAME)) DESCRIPTION\n" +
      "  FROM SYS.ALL_OBJECTS O\n" +
      " WHERE OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND OBJECT_TYPE IN ('TYPE', 'VIEW', 'PACKAGE', 'SEQUENCE', 'TABLE', 'MATERIALIZED VIEW', DECODE(OWNER, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'), 'SYNONYM'))\n" +
      " ORDER BY OBJECT_NAME, OBJECT_TYPE";
  }
  
  public static String autocompleteSchemaProcedures() {
    return
      "SELECT OBJECT_NAME, DECODE(GREATEST(POSITION, SEQUENCE), 1, DECODE(ARGUMENT_NAME, NULL, 'FUNCTION', 'PROCEDURE'), NULL) OBJECT_TYPE, ARGUMENT_NAME,\n" +
      "       DECODE(DATA_TYPE,'PL/SQL TABLE',TYPE_NAME,DATA_TYPE) DATA_TYPE, IN_OUT\n" +
      "  FROM SYS.ALL_ARGUMENTS A\n" +
      " WHERE OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND PACKAGE_NAME IS NULL\n" +
      " ORDER BY OBJECT_NAME, SEQUENCE";
  }

  public static String autocompleteSchemaProceduresNoParams() {
    return
      "SELECT OBJECT_NAME\n" +
      "  FROM SYS.ALL_PROCEDURES\n" +
      " WHERE OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND PROCEDURE_NAME IS NULL\n" +
      "MINUS\n" +
      "SELECT OBJECT_NAME\n" +
      "  FROM SYS.ALL_ARGUMENTS\n" +
      " WHERE OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND PACKAGE_NAME IS NULL\n" +
      " ORDER BY OBJECT_NAME";
  }

  public static String autocompleteSchemaPackageProcedures() {
    return
      "SELECT OBJECT_NAME, DECODE(GREATEST(POSITION, SEQUENCE), 1, DECODE(ARGUMENT_NAME, NULL, 'FUNCTION', 'PROCEDURE'), NULL) OBJECT_TYPE, ARGUMENT_NAME,\n" +
      "       DECODE(DATA_TYPE,'PL/SQL TABLE',TYPE_NAME,DATA_TYPE) DATA_TYPE, IN_OUT, OVERLOAD, SEQUENCE\n" +
      "  FROM SYS.ALL_ARGUMENTS A\n" +
      " WHERE OWNER = :SCHEMA_NAME\n" +
      "   AND PACKAGE_NAME = :OBJECT_NAME\n" +
      " ORDER BY OBJECT_NAME, OVERLOAD, SEQUENCE";
  }

  public static String autocompleteSchemaPackageProceduresNoParams() {
    return
      "SELECT OBJECT_NAME\n" +
      "  FROM SYS.ALL_PROCEDURES\n" +
      " WHERE OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND OBJECT_NAME = :OBJECT_NAME\n" +
      "MINUS\n" +
      "SELECT OBJECT_NAME\n" +
      "  FROM SYS.ALL_ARGUMENTS\n" +
      " WHERE OWNER = NVL(:SCHEMA_NAME, SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'))\n" +
      "   AND PACKAGE_NAME = :OBJECT_NAME\n" +
      " ORDER BY OBJECT_NAME";
  }

  public static String autocompleteSchemaTypeAttributes() {
    return
      "SELECT ATTR_NAME OBJECT_NAME, ATTR_TYPE_NAME DATA_TYPE\n" +
      "  FROM SYS.ALL_TYPE_ATTRS\n" +
      " WHERE OWNER = :SCHEMA_NAME\n" +
      "   AND TYPE_NAME = :OBJECT_NAME\n" +
      " ORDER BY OBJECT_NAME";
  }

  public static String autocompleteTableColumns() {
    return
      "SELECT C.COLUMN_NAME, C.DATA_TYPE, \n" +
      "       C.DATA_TYPE||\n" +
      "       DECODE( C.DATA_PRECISION,\n" +
      "         NULL, DECODE( C.DATA_TYPE,\n" +
      "           'DATE', '', 'NUMBER', '', 'BLOB', '', 'CLOB', '', 'XMLTYPE', '', DECODE(C.DATA_TYPE_OWNER, NULL, NULL, DATA_TYPE), '',\n" +
      "           DECODE( C.DATA_LENGTH, 0, '', '('||C.DATA_LENGTH||DECODE(C.CHAR_USED, 'B', ' BYTE', 'C', 'CHAR', NULL)||')' ) ),\n" +
      "         '('||C.DATA_PRECISION||DECODE( NVL( C.DATA_SCALE, 0 ), 0, ')', ','||C.DATA_SCALE||')' ) )||CASE WHEN C.NULLABLE <> 'Y' THEN ' NOT NULL' END AS DISPLAY_TYPE,\n" +
      "       CC.COMMENTS\n" +
      "  FROM SYS.ALL_TAB_COLUMNS C, SYS.ALL_COL_COMMENTS CC\n" +
      " WHERE C.OWNER = :SCHEMA_NAME\n" +
      "   AND C.TABLE_NAME = :OBJECT_NAME\n" +
      "   AND CC.OWNER = C.OWNER\n" +
      "   AND CC.TABLE_NAME = C.TABLE_NAME\n" +
      "   AND CC.COLUMN_NAME = C.COLUMN_NAME\n" +
      " ORDER BY COLUMN_NAME";
  }

  public static String getErrorPosition() {
    return
      "declare\n" +
      "  c integer default dbms_sql.open_cursor;\n" +
      "begin\n" +
      "  begin\n" +
      "    dbms_sql.parse( c, :sql, dbms_sql.native );\n" +
      "  exception\n" +
      "    when others then\n" +
      "      :result := dbms_sql.last_error_position;\n" +
      "  end;\n" +
      "  dbms_sql.close_cursor( c );\n" +
      "end;";
  }

}
