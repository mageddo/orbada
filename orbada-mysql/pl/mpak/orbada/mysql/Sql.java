/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql;

/**
 *
 * @author akaluza
 */
public class Sql {

  public static String getHelpList(String filter) {
    return String.format(
      "select * from (\n" +
      "  select h.help_topic_id, h.name, c.name category_name, h.description, h.example, h.url\n" +
      "    from mysql.help_topic h, mysql.help_category c\n" +
      "   where h.help_category_id = c.help_category_id) h\n" +
      "%s" +
      " order by name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getDatabaseList() {
    return "select schema_name from information_schema.schemata order by schema_name";
  }
  
  public static String getEngineList() {
    return "select * from information_schema.engines order by engine";
  }

  public static String getTableList(String filter) {
    return String.format(
      "select table_schema, table_name, table_type, engine, row_format, create_time, update_time, table_comment\n" +
      "  from information_schema.tables\n" +
      " where table_schema = :schema_name\n" +
      "   and (table_type like '%%TABLE%%' or table_type = 'SYSTEM VIEW')\n" +
      "%s" +
      " order by table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getColumnList(String filter) {
    return String.format(
      "select ordinal_position, table_schema, table_name, column_name, column_default, \n" +
      "       is_nullable, data_type, column_type, character_set_name, collation_name, \n" +
      "       column_key, extra, column_comment\n" +
      "  from information_schema.columns\n" +
      " where table_schema = :schema_name\n" +
      "   and table_name = :table_name\n" +
      "%s" +
      " order by ordinal_position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableIndexesList(String filter) {
    return String.format(
      "select table_schema, table_name, uniquines, index_schema, index_name, index_type, comment, columns\n" +
      "  from (select table_schema, table_name, case when non_unique = 0 then 'YES' else null end uniquines, nullable,\n" +
      "               index_schema, index_name, index_type, comment,\n" +
      "               group_concat(concat(column_name, case when collation = 'A' then '' else ' DESC' end) order by seq_in_index) columns\n" +
      "          from information_schema.statistics\n" +
      "         where table_schema = :schema_name\n" +
      "           and table_name = :table_name\n" +
      "         group by table_schema, table_name, index_schema, index_name) i\n" +
      "%s" +
      " order by index_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getTableConstraintsList(String filter) {
    return String.format(
      "select constraint_schema, constraint_name, table_schema, table_name, constraint_type, columns,\n" +
      "       referenced_table_schema, referenced_table_name, ref_columns, update_rule, delete_rule\n" +
      "  from (select tc.constraint_schema, tc.constraint_name, tc.table_schema, tc.table_name, tc.constraint_type,\n" +
      "               cu.referenced_table_schema, cu.referenced_table_name,\n" +
      "               group_concat(cu.column_name order by cu.ordinal_position) columns,\n" +
      "               group_concat(cu.referenced_column_name order by cu.ordinal_position) ref_columns,\n" +
      "               r.update_rule, r.delete_rule\n" +
      "          from information_schema.table_constraints tc \n" +
      "                 left outer join information_schema.referential_constraints r on (tc.constraint_schema = r.constraint_schema and tc.constraint_name = r.constraint_name),\n" +
      "               information_schema.key_column_usage cu\n" +
      "         where tc.constraint_schema = cu.constraint_schema\n" +
      "           and tc.table_schema = cu.table_schema\n" +
      "           and tc.table_name = cu.table_name\n" +
      "           and tc.constraint_name = cu.constraint_name\n" +
      "           and tc.table_schema = :schema_name\n" +
      "           and tc.table_name = :table_name\n" +
      "         group by tc.table_schema, tc.table_name, tc.constraint_name) c\n" +
      "%s" +
      " order by constraint_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getTableTriggersList(String filter) {
    return String.format(
      "select trigger_schema, trigger_name, action_timing, event_manipulation, \n" +
      "       event_object_schema, event_object_table, action_orientation, action_order, action_condition\n" +
      "  from information_schema.triggers\n" +
      " where event_object_schema = :schema_name\n" +
      "   and event_object_table = :table_name\n" +
      "%s" +
      " order by action_order",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTriggersList(String filter) {
    return String.format(
      "select trigger_schema, trigger_name, action_timing, event_manipulation, \n" +
      "       event_object_schema, event_object_table, action_orientation, action_order, action_condition\n" +
      "  from information_schema.triggers\n" +
      " where trigger_schema = :schema_name\n" +
      "%s" +
      " order by action_order",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTriggersSource(String databaseName, String triggerName) {
    return String.format(
      "show create trigger %s.%s",
      new Object[]{databaseName, triggerName});
  }

  public static String getTablePrivilegesList(String filter) {
    return String.format(
      "select grantee, table_schema, table_name, privilege_type, is_grantable\n" +
      "  from information_schema.table_privileges\n" +
      " where table_schema = :schema_name\n" +
      "   and table_name = :table_name\n" +
      "%s" +
      " order by privilege_type",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getColumnPrivilegesList(String filter) {
    return String.format(
      "select grantee, table_schema, table_name, column_name, privilege_type, is_grantable\n" +
      "  from information_schema.column_privileges\n" +
      " where table_schema = :schema_name\n" +
      "   and table_name = :table_name\n" +
      "%s" +
      " order by column_name, privilege_type",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableReferencedFromList(String filter) {
    return String.format(
      "select * from (\n" +
      "select rc.constraint_schema, rc.constraint_name, rc.table_name,\n" +
      "       group_concat(ri.column_name order by ri.seq_in_index) columns,\n" +
      "       group_concat(i.column_name order by i.ordinal_position) ref_columns\n" +
      "  from information_schema.referential_constraints rc, information_schema.table_constraints tc, information_schema.statistics ri, information_schema.key_column_usage i\n" +
      " where rc.unique_constraint_schema = tc.constraint_schema\n" +
      "   and rc.unique_constraint_name = tc.constraint_name\n" +
      "   and rc.referenced_table_name = tc.table_name\n" +
      "   and ri.table_schema = tc.constraint_schema\n" +
      "   and ri.table_name = tc.table_name\n" +
      "   and ri.index_name = tc.constraint_name\n" +
      "   and i.table_schema = rc.constraint_schema\n" +
      "   and i.table_name = rc.table_name\n" +
      "   and i.constraint_name = rc.constraint_name\n" +
      "   and tc.table_schema = :schema_name\n" +
      "   and tc.table_name = :table_name\n" +
      " group by rc.constraint_schema, rc.constraint_name, rc.table_name) x\n" +
      "%s" +
      " order by constraint_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getTableReferenceToList(String filter) {
    return String.format(
      "select * from (\n" +
      "select rc.constraint_schema, rc.constraint_name, rc.referenced_table_name table_name,\n" +
      "       group_concat(i.column_name order by i.ordinal_position) columns,\n" +
      "       group_concat(ri.column_name order by ri.seq_in_index) ref_columns\n" +
      "  from information_schema.referential_constraints rc, information_schema.statistics ri, information_schema.key_column_usage i\n" +
      " where rc.constraint_schema = :schema_name\n" +
      "   and rc.table_name = :table_name\n" +
      "   and ri.table_schema = rc.unique_constraint_schema\n" +
      "   and ri.table_name = rc.referenced_table_name\n" +
      "   and ri.index_name = rc.unique_constraint_name\n" +
      "   and i.table_schema = rc.constraint_schema\n" +
      "   and i.table_name = rc.table_name\n" +
      "   and i.constraint_name = rc.constraint_name\n" +
      " group by rc.constraint_schema, rc.constraint_name, rc.referenced_table_name) x\n" +
      "%s" +
      " order by constraint_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getTableDetails(String databaseName, String triggerName) {
    return String.format(
      "select * from information_schema.tables where table_schema = '%s' and table_name = '%s'",
      new Object[]{databaseName, triggerName});
  }

  public static String getTableSource(String databaseName, String triggerName) {
    return String.format(
      "show create table %s.%s",
      new Object[]{databaseName, triggerName});
  }

  public static String getViewSource(String databaseName, String triggerName) {
    return String.format(
      "show create view %s.%s",
      new Object[]{databaseName, triggerName});
  }

  public static String getViewList(String filter) {
    return String.format(
      "select table_schema, table_name, is_updatable, definer, check_option, security_type\n" +
      "  from information_schema.views\n" +
      " where table_schema = :schema_name\n" +
      "%s" +
      " order by table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getProcedureList(String filter) {
    return String.format(
      "select routine_schema, routine_name, is_deterministic, sql_data_access, definer, security_type, created, last_altered, routine_comment\n" +
      "  from information_schema.routines\n" +
      " where routine_schema = :schema_name\n" +
      "   and routine_type = :object_type\n" +
      "%s" +
      " order by routine_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  private static String getRoutineParameterSql() {
    return
      "select p.db, p.name, p.type, p.param_no,\n" +
      "      case \n" +
      "        when locate('IN ', p.param) = 1 then substr(trim(p.param), 4)\n" +
      "        when locate('OUT ', p.param) = 1 then substr(trim(p.param), 5)\n" +
      "        when locate('INOUT ', p.param) = 1 then substr(trim(p.param), 7)\n" +
      "        else p.param\n" +
      "      end param,\n" +
      "      case \n" +
      "        when locate('IN ', p.param) = 1 then 'IN'\n" +
      "        when locate('OUT ', p.param) = 1 then 'OUT'\n" +
      "        when locate('INOUT ', p.param) = 1 then 'INOUT'\n" +
      "      end param_type\n" +
      "  from (select db, name, type, no.value param_no, trim(replace(replace(substring(substring_index(param_list, ',', no.value), length(substring_index(param_list, ',', no.value - 1)) + 1), ',', ''), char(10), ' ')) param\n" +
      "          from mysql.proc, \n" +
      "              (select 1 value union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9 union all select 10\n" +
      "               union all select 11 value union all select 12 union all select 13 union all select 14 union all select 15 union all select 16 union all select 17 union all select 18 union all select 19 union all select 20) no\n" +
      "         where replace(substring(substring_index(param_list, ',', no.value), length(substring_index(param_list, ',', no.value - 1)) + 1), ',', '') <> '') p\n" +
      " union all\n" +
      "select db, name, type, 0, returns param, 'RETURNS' param_type\n" +
      "  from mysql.proc\n" +
      " where type = 'FUNCTION'";
  }

  public static String getDatabaseRoutineParameterList() {
    return
      "select * from (" +getRoutineParameterSql() +") p\n" +
      " where p.db = :schema_name\n" +
      " order by name, param_no";
  }

  public static String getRoutineParameterList(String filter) {
    return String.format(
      "select * from (" +getRoutineParameterSql() +") p\n" +
      " where p.db = :schema_name\n" +
      "   and p.type = :object_type\n" +
      "   and p.name = :object_name\n" +
      "%s" +
      " order by name, param_no",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getRoutinePrivilegeList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select concat(user, '@', host) grantee, db routine_schema, routine_name, routine_type, grantor, proc_priv privilege, timestamp granted\n" +
      "          from mysql.procs_priv\n" +
      "         where db = :schema_name\n" +
      "           and routine_type = :object_type\n" +
      "           and routine_name = :object_name) p\n" +
      "%s" +
      " order by privilege",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getProcedureSource(String databaseName, String triggerName) {
    return String.format(
      "show create procedure %s.%s",
      new Object[]{databaseName, triggerName});
  }

  public static String getFunctionSource(String databaseName, String triggerName) {
    return String.format(
      "show create function %s.%s",
      new Object[]{databaseName, triggerName});
  }

  public static String getCheckTable(String tableName, String kind) {
    return String.format(
      "check table %s %s",
      new Object[]{tableName, kind});
  }

  public static String getAllObjectList() {
    return
      "select *\n" +
      "  from (select table_schema object_schema, table_name object_name, 'TABLE' object_type, table_comment comment, null table_schema, null table_name\n" +
      "          from information_schema.tables\n" +
      "         where (table_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "           and table_type <> 'VIEW'\n" +
      "        union all\n" +
      "        select table_schema object_schema, table_name object_name, 'VIEW' object_type, null comment, null table_schema, null table_name\n" +
      "          from information_schema.views\n" +
      "         where (table_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "        union all\n" +
      "        select routine_schema object_schema, routine_name object_name, routine_type object_type, routine_comment comment, null table_schema, null table_name\n" +
      "          from information_schema.routines\n" +
      "         where (routine_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "        union all\n" +
      "        select trigger_schema object_schema, trigger_name object_name, 'TRIGGER' object_type, null comment, event_object_schema table_schema, event_object_table table_name\n" +
      "          from information_schema.triggers\n" +
      "         where (trigger_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "        union all\n" +
      "        select null object_schema, schema_name object_name, 'DATABASE' object_type, null comment, null table_schema, null table_name\n" +
      "          from information_schema.schemata\n" +
      "        union all\n" +
      "        select null object_schema, engine object_name, 'ENGINE' object_type, comment, null table_schema, null table_name\n" +
      "          from information_schema.engines\n" +
      "        union all\n" +
      "        select event_schema object_schema, event_name object_name, 'EVENT' object_type, null comment, null table_schema, null table_name\n" +
      "          from information_schema.events\n" +
      "         where (event_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "        union all\n" +
      "        select index_schema object_schema, index_name object_name, 'INDEX' object_type, comment, table_schema, table_name\n" +
      "          from (select distinct index_schema, index_name, table_schema, table_name, comment from information_schema.statistics) indexes\n" +
      "         where (index_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)) o";
  }

  public static String getObjectsType() {
    return getObjectsType(null);
  }

  public static String getObjectsType(String filter) {
    return String.format(
      getAllObjectList() +
      "\n where (object_name = :OBJECT_NAME or :OBJECT_NAME is null)\n" +
      "%s" +
      " order by object_schema, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getObjectsSearch(String filter) {
    return getAllObjectList() +String.format(
      "\n where (object_name = :OBJECT_NAME or :OBJECT_NAME is null or upper(object_name) like concat('%%', upper(:OBJECT_NAME), '%%'))\n" +
      "%s" +
      " order by object_schema, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getObjectsName() {
    return getAllObjectList() +
      "\n where (object_type = :OBJECT_TYPE or :OBJECT_TYPE is null)\n" +
      " order by object_schema, object_name";
  }

  public static String getStatsList() {
    return
      "select variable_name, variable_value\n" +
      "  from information_schema.session_status\n" +
      " where variable_value <> '0'\n" +
      "   and variable_value is not null\n" +
      " order by variable_name";
  }

  public static String getGlobalStatsList() {
    return
      "select variable_name, variable_value\n" +
      "  from information_schema.global_status\n" +
      " where variable_value <> '0'\n" +
      "   and variable_value is not null\n" +
      " order by variable_name";
  }

  public static String getProcessList(String filter) {
    return String.format(
      "select id, user, host, db, command, time, state, info, case when id = @@pseudo_thread_id then 'T' else 'F' end THIS\n" +
      "  from information_schema.processlist\n" +
      "%s" +
      " order by time",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getShowProcessList() {
    return "SHOW PROCESSLIST";
  }

}
