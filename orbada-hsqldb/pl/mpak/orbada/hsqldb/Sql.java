/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb;

import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public static String getTableList(String filter) {
    return String.format(
      "select t.table_schem schema_name, t.table_name, t.hsqldb_type type, t.remarks, \n" +
      "       (case t.read_only when true then 'R/O' else 'R/W' end) access,\n" +
      "       (case t.table_type when 'SYSTEM TABLE' then 'SYSTEM' when 'GLOBAL TEMPORARY' then 'TEMPORARY' else 'USER' end) scope\n" +
      "  from information_schema.system_tables t\n" +
      " where (t.table_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and t.table_type in ('TABLE', 'SYSTEM TABLE', 'GLOBAL TEMPORARY')\n" +
      "%s" +
      " order by table_schem, table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getViewList(String filter, int version) {
    return String.format(
      "select v.table_schema schema_name, v.table_name view_name, v.check_option, v.is_updatable" +
      (version >= HSqlDbInfoProvider.hsqlDb20 ? "" : ",\n       (case v.valid when true then 'VALID' when false then 'INVALID' else null end) status") +
      "\n  from information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "VIEWS" : "SYSTEM_VIEWS") +" v\n" +
      " where (v.table_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "%s" +
      " order by table_schema, table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getViewSource(int version) {
    return
      "select view_definition\n" +
      "  from information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "VIEWS" : "SYSTEM_VIEWS") +"\n" +
      " where table_schema = :SCHEMA_NAME\n" +
      "   and table_name = :VIEW_NAME";  
  }

  public static String getViewColumnUsageList(String filter, int version) {
    return String.format(
      "select view_schema schema_name, view_name, table_schema, table_name, column_name\n" +
      "  from information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "VIEW_COLUMN_USAGE" : "SYSTEM_VIEW_COLUMN_USAGE") +"\n" +
      " where (view_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (view_name = :VIEW_NAME or :VIEW_NAME is null)\n" +
      "%s" +
      " order by schema_name, view_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getViewRoutineUsageList(String filter, int version) {
    return String.format(
      "select " +(version >= HSqlDbInfoProvider.hsqlDb20 ? "view_schema" : "table_schema") +" schema_name, " +(version >= HSqlDbInfoProvider.hsqlDb20 ? "view_name" : "table_name") +" view_name, specific_schema, specific_name\n" +
      "  from information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "VIEW_ROUTINE_USAGE" : "SYSTEM_VIEW_ROUTINE_USAGE") +"\n" +
      " where (" +(version >= HSqlDbInfoProvider.hsqlDb20 ? "view_schema" : "table_schema") +" = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (" +(version >= HSqlDbInfoProvider.hsqlDb20 ? "view_name" : "table_name") +" = :VIEW_NAME or :VIEW_NAME is null)\n" +
      "%s" +
      " order by schema_name, view_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getViewTableUsageList(String filter, int version) {
    return String.format(
      "select view_schema schema_name, view_name, table_schema, table_name\n" +
      "  from information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "VIEW_TABLE_USAGE" : "SYSTEM_VIEW_TABLE_USAGE") +"\n" +
      " where (view_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (view_name = :VIEW_NAME or :VIEW_NAME is null)\n" +
      "%s" +
      " order by schema_name, view_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getColumnList(String filter) {
    return String.format(
      "select cols.ordinal_position, cols.table_schem schema_name, cols.table_name, cols.column_name, cols.data_type, cols.type_name,\n" +
      "       coalesce(types.type_name, cols.type_name)||ifnull(case when types.create_params is not null and types.type_name <> 'TIMESTAMP' \n" +
      "         then '('||replace(replace(replace(types.create_params, 'LENGTH', coalesce(convert(cols.column_size, varchar(30)), '')), 'PRECISION', coalesce(convert(cols.num_prec_radix, varchar(20)), '')), 'SCALE', coalesce(convert(cols.decimal_digits, varchar(20)), ''))||')' \n" +
      "       end, '') as display_type,\n" +
      "       cols.column_def, cols.is_nullable, case when pk.column_name is not null then 'YES' else 'NO' end as pk_column, cols.remarks\n" +
      "  from information_schema.system_columns cols \n" +
      "         left outer join information_schema.SYSTEM_PRIMARYKEYS pk on (cols.table_schem = pk.table_schem and cols.table_name = pk.table_name and cols.column_name = pk.column_name)\n" +
      "         left outer join information_schema.SYSTEM_TYPEINFO types on (cols.data_type = types.data_type and cols.type_name = types.type_name)\n" +
      " where (cols.table_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (cols.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "%s" +
      " order by table_schem, table_name, ordinal_position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getIndexList(String filter) {
    return String.format(
      "select idx.index_name, idx.table_schem schema_name, idx.table_name, idx.column_name, idx.ordinal_position,\n" +
      "       case idx.type when 3 then 'OTHER' when 2 then 'HASHED' when 1 then 'CLUSTERED' end index_type,\n" +
      "       case idx.non_unique when true then 'NO' when false then 'YES' end unique_index,\n" +
      "       case asc_or_desc when 'A' then 'ASC' when 'D' then 'DESC' end ASC_OR_DESC,\n" +
      "       case when pk.column_name is not null then 'YES' else 'NO' end as pk_index\n" +
      "  from information_schema.SYSTEM_INDEXINFO idx left outer join \n" +
      "         information_schema.SYSTEM_PRIMARYKEYS pk on (idx.table_schem = pk.table_schem and idx.table_name = pk.table_name and idx.column_name = pk.column_name)\n" +
      " where (idx.table_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (idx.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "%s" +
      " order by table_schem, table_name, index_name, ordinal_position",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getConstraintList(String filter, int version) {
    return String.format(
      "select tc.constraint_schema, tc.constraint_name, tc.constraint_type, \n" +
      "       tc.table_schema schema_name, tc.table_name, tc.is_deferrable, tc.initially_deferred, \n" +
      "       case \n" +
      "         when cc.check_clause is not null then cc.check_clause\n" +
      "         when tc.constraint_type = 'FOREIGN KEY' then \n" +
      "           '('||(select limit 0 1 cr.fkcolumn_name from information_schema.SYSTEM_CROSSREFERENCE cr where cr.fktable_schem = tc.table_schema and cr.fktable_name = tc.table_name and cr.fk_name = tc.constraint_name)||\n" +
      "           ifnull(','||(select limit 1 1 cr.fkcolumn_name from information_schema.SYSTEM_CROSSREFERENCE cr where cr.fktable_schem = tc.table_schema and cr.fktable_name = tc.table_name and cr.fk_name = tc.constraint_name), '')||\n" +
      "           ifnull(','||(select limit 2 1 cr.fkcolumn_name from information_schema.SYSTEM_CROSSREFERENCE cr where cr.fktable_schem = tc.table_schema and cr.fktable_name = tc.table_name and cr.fk_name = tc.constraint_name), '')||\n" +
      "           case when (select limit 3 1 cr.fkcolumn_name from information_schema.SYSTEM_CROSSREFERENCE cr where cr.fktable_schem = tc.table_schema and cr.fktable_name = tc.table_name and cr.fk_name = tc.constraint_name) is not null then ', ...' else '' end||')'||\n" +
      "           ifnull((select limit 0 1 ' ON \"'||cr.pktable_schem||'\".\"'||cr.pktable_name||'\"'||case when cr.update_rule = 3 and cr.delete_rule = 3 then null else '' end from information_schema.SYSTEM_CROSSREFERENCE cr where tc.table_schema = cr.fktable_schem and tc.table_name = cr.fktable_name and tc.constraint_name = cr.fk_name), '')||\n" +
      "           ifnull((select limit 0 1 ' DELETE '||case cr.delete_rule when 0 then 'CASCADE' when 1 then 'SET NULL' when 2 then 'SET DEFAULT' when 3 then null end from information_schema.SYSTEM_CROSSREFERENCE cr where tc.table_schema = cr.fktable_schem and tc.table_name = cr.fktable_name and tc.constraint_name = cr.fk_name), '')||\n" +
      "           ifnull((select limit 0 1 ' UPDATE '||case cr.update_rule when 0 then 'CASCADE' when 1 then 'SET NULL' when 2 then 'SET DEFAULT' when 3 then null end from information_schema.SYSTEM_CROSSREFERENCE cr where tc.table_schema = cr.fktable_schem and tc.table_name = cr.fktable_name and tc.constraint_name = cr.fk_name), '')\n" +
      "         when tc.constraint_type = 'PRIMARY KEY' then \n" +
      "           (select limit 0 1 pk.column_name from information_schema.SYSTEM_PRIMARYKEYS pk where pk.table_schem = tc.table_schema and pk.table_name = tc.table_name)||\n" +
      "           ifnull(','||(select limit 1 1 pk.column_name from information_schema.SYSTEM_PRIMARYKEYS pk where pk.table_schem = tc.table_schema and pk.table_name = tc.table_name), '')||\n" +
      "           ifnull(','||(select limit 2 1 pk.column_name from information_schema.SYSTEM_PRIMARYKEYS pk where pk.table_schem = tc.table_schema and pk.table_name = tc.table_name), '')||\n" +
      "           case when (select limit 3 1 pk.column_name from information_schema.SYSTEM_PRIMARYKEYS pk where pk.table_schem = tc.table_schema and pk.table_name = tc.table_name) is not null then ', ...' else '' end\n" +
      "       end info\n" +
      "  from information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "TABLE_CONSTRAINTS" : "SYSTEM_TABLE_CONSTRAINTS") +" tc left outer join\n" +
      "         information_schema." +(version >= HSqlDbInfoProvider.hsqlDb20 ? "CHECK_CONSTRAINTS" : "SYSTEM_CHECK_CONSTRAINTS") +" cc on (tc.constraint_schema = cc.constraint_schema and tc.constraint_name = cc.constraint_name)\n" +
      " where (tc.table_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (tc.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "%s" +
      " order by schema_name, table_name, constraint_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTablePrivilegeList(String filter, int version) {
    if (version >= HSqlDbInfoProvider.hsqlDb20) {
      return String.format(
        "select tp.table_schema schema_name, tp.table_name, tp.grantor, tp.grantee, tp.privilege_type privilege, tp.is_grantable admin\n" +
        "  from information_schema.table_privileges tp\n" +
        " where (tp.table_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "   and (tp.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
        "%s" +
        " order by schema_name, table_name, privilege",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""});
    }
    else {
      return String.format(
        "select tp.table_schem schema_name, tp.table_name, tp.grantor, tp.grantee, tp.privilege, tp.is_grantable admin\n" +
        "  from information_schema.SYSTEM_TABLEPRIVILEGES tp\n" +
        " where (tp.table_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "   and (tp.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
        "%s" +
        " order by schema_name, table_name, privilege",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""});
    }
  }

  public static String getColumnPrivilegeList(String filter, int version) {
    if (version >= HSqlDbInfoProvider.hsqlDb20) {
      return String.format(
        "select cp.table_schema schema_name, cp.table_name, cp.column_name, cp.grantor, cp.grantee, cp.privilege_type privilege, cp.is_grantable admin\n" +
        "  from information_schema.COLUMN_PRIVILEGES cp\n" +
        " where (cp.table_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "   and (cp.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
        "%s" +
        " order by schema_name, table_name, column_name, privilege",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""});
    }
    else {
      return String.format(
        "select cp.table_schem schema_name, cp.table_name, cp.column_name, cp.grantor, cp.grantee, cp.privilege, cp.is_grantable admin\n" +
        "  from information_schema.SYSTEM_COLUMNPRIVILEGES cp\n" +
        " where (cp.table_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "   and (cp.table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
        "%s" +
        " order by schema_name, table_name, column_name, privilege",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""});
    }
  }

  public static String getTriggerList(String filter, int version) {
    if (version >= HSqlDbInfoProvider.hsqlDb20) {
      return String.format(
        "select *\n" +
        "  from (select trigger_schema schema_name, trigger_name, action_timing trigger_type, event_manipulation triggering_event, event_object_schema table_schema, \n" +
        "               case when action_timing = 'INSTEAD OF' then 'VIEW' else 'TABLE' end object_type, event_object_table table_name, null column_name, \n" +
        "               action_orientation referencing_names, action_condition when_clause, null status, \n" +
        "               'CREATE TRIGGER '||trigger_name||' '||action_timing||' '||event_manipulation||' ON '||\n" +
        "                 case when event_object_schema <> trigger_schema then event_object_schema||'.' else '' end||event_object_table||\n" +
        "                 case when action_reference_new_row is not null or action_reference_old_row is not null then \n" +
        "                   ' REFERENCING'||case when action_reference_new_row is not null then ' NEW AS '||action_reference_new_row else '' end||\n" +
        "                   case when action_reference_old_row is not null then ' OLD AS '||action_reference_old_row else '' end else '' end||\n" +
        "                 case when action_orientation is not null then ' FOR EACH '||action_orientation else '' end||chr(10)||\n" +
        "                 action_statement source,\n" +
        "               null action_type, null trigger_body\n" +
        "          from information_schema.triggers\n" +
        "         where (event_object_schema = :SCHEMA_NAME or trigger_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "           and (event_object_table = :TABLE_NAME or :TABLE_NAME is null))\n" +
        "%s" +
        " order by schema_name, trigger_name",
        new Object[]{filter != null ? " where " + filter + "\n" : ""});
    }
    else {
      return String.format(
        "select trigger_schem schema_name, trigger_name, trigger_type, triggering_event, table_schem table_schema, \n" +
        "       base_object_type object_type, table_name, column_name, referencing_names, when_clause, status, description source,\n" +
        "       action_type, trigger_body\n" +
        "  from information_schema.SYSTEM_TRIGGERS\n" +
        " where (table_schem = :SCHEMA_NAME or trigger_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "   and (table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
        "%s" +
        " order by schema_name, trigger_name",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""});
    }
  }

  public static String getAliasList(String filter) {
    return String.format(
      "select a.object_schem schema_name, a.alias alias_name, ifnull(p.specific_name, a.object_name) object_name, p.num_input_params, p.num_result_sets, p.remarks, \n" +
      "       a.object_type alias_type, case p.procedure_type when 0 then 'UNKNOWN' when 1 then 'PROCEDURE' when 2 then 'FUNCTION' end procedure_type\n" +
      "  from information_schema.SYSTEM_ALIASES a left outer join\n" +
      "         information_schema.SYSTEM_PROCEDURES p on (a.object_schem = p.procedure_schem and a.alias = p.procedure_name)\n" +
      " where (a.object_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "%s" +
      " order by schema_name, alias_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getProcedureList(String filter, int version) {
    if (version >= HSqlDbInfoProvider.hsqlDb20) {
      return String.format(
        "select *\n" +
        "  from (select p.procedure_schem schema_name, p.procedure_name, \n" +
        "               (select count(0) from information_schema.system_procedurecolumns pc where p.procedure_schem = pc.procedure_schem and p.procedure_name = pc.procedure_name) num_input_params, \n" +
        "               0 num_result_sets, p.remarks, null origin, p.specific_name,\n" +
        "               case p.procedure_type when 0 then 'UNKNOWN' when 1 then 'PROCEDURE' when 2 then 'FUNCTION' end procedure_type\n" +
        "          from information_schema.system_procedures p\n" +
        "         where (p.procedure_schem = :SCHEMA_NAME or :SCHEMA_NAME is null))\n" +
        "%s" +
        " order by schema_name, procedure_name",
        new Object[]{filter != null ? " where " + filter + "\n" : ""});
    }
    else {
      return String.format(
        "select *\n" +
        "  from (select p.procedure_schem schema_name, p.procedure_name, \n" +
        "               (select count(0) from information_schema.system_procedurecolumns pc where p.procedure_schem = pc.procedure_schem and p.procedure_name = pc.procedure_name) num_input_params, \n" +
        "               0 num_result_sets, p.remarks, null origin, p.specific_name,\n" +
        "               case p.procedure_type when 0 then 'UNKNOWN' when 1 then 'PROCEDURE' when 2 then 'FUNCTION' end procedure_type\n" +
        "          from information_schema.system_procedures p\n" +
        "         where (p.procedure_schem = :SCHEMA_NAME or :SCHEMA_NAME is null))\n" +
        "%s" +
        " order by schema_name, procedure_name",
        new Object[]{filter != null ? " where " + filter + "\n" : ""});
    }
  }

  public static String getProcedureParameterList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select c.procedure_schem schema_name, c.procedure_name, c.column_name, c.type_name,\n" +
      "               case c.column_type when 0 then 'UNKNOWN' when 1 then 'IN' when 2 then 'IN/OUT' when 3 then 'OUT' when 4 then 'RETURN' when 5 then 'RESULT' end column_type,\n" +
      "               case c.nullable when 0 then 'NO' when 1 then 'YES' end nullable,\n" +
      "               coalesce(t.type_name, c.type_name)||ifnull(case when t.create_params is not null and t.type_name <> 'TIMESTAMP' \n" +
      "                 then '('||replace(replace(replace(t.create_params, 'LENGTH', coalesce(convert(c.length, varchar(30)), '')), 'PRECISION', coalesce(convert(c.radix, varchar(20)), '')), 'SCALE', coalesce(convert(c.scale, varchar(20)), ''))||')' \n" +
      "               end, '') as display_type, c.remarks\n" +
      "          from information_schema.system_procedurecolumns c, information_schema.system_typeinfo t\n" +
      "         where c.data_type = t.data_type\n" +
      "           and t.type_name <> 'VARCHAR_IGNORECASE'\n" +
      "           and (c.procedure_schem = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "           and (c.procedure_name = :PROCEDURE_NAME or :PROCEDURE_NAME is null))\n" +
      "%s" +
      " order by schema_name, procedure_name, column_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getSequenceList(String filter, int version) {
    return String.format(
      "select sequence_schema schema_name, sequence_name, " +(version >= HSqlDbInfoProvider.hsqlDb20 ? "data_type" : "dtd_identifier data_type") +", maximum_value, minimum_value, increment, cycle_option, start_with\n" +
      "  from information_schema.SYSTEM_SEQUENCES\n" +
      " where (sequence_schema = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "%s" +
      " order by schema_name, sequence_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getAllObjectList(int version) {
    if (version >= HSqlDbInfoProvider.hsqlDb20) {
      return
        "select *\n" +
        "  from (select 'TABLE' object_type, table_schem schema_name, table_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(hsqldb_type as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_TABLES\n" +
        "         where table_type in ('TABLE', 'SYSTEM TABLE', 'GLOBAL TEMPORARY')\n" +
        "        union all\n" +
        "        select 'VIEW' object_type, table_schem schema_name, table_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(null as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_TABLES\n" +
        "         where table_type = 'VIEW'\n" +
        "        union all\n" +
        "        select case procedure_type when 0 then 'UNKNOWN' when 1 then 'PROCEDURE' when 2 then 'FUNCTION' end object_type,\n" +
        "               procedure_schem schema_name, procedure_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(null as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_PROCEDURES\n" +
        "        union all\n" +
        "        select distinct 'INDEX' object_type, table_schem schema_name, index_name object_name, table_schem table_schema, table_name, \n" +
        "               case asc_or_desc when 'A' then 'ASC ' when 'D' then 'DESC ' end||case non_unique when true then '' when false then 'UNIQUE ' end||case type when 3 then 'OTHER' when 2 then 'HASHED' when 1 then 'CLUSTERED' end sub_type\n" +
        "          from information_schema.SYSTEM_INDEXINFO\n" +
        "        union all\n" +
        "        select 'TRIGGER' object_type, trigger_schema schema_name, trigger_name object_name,\n" +
        "               event_object_schema table_schema, event_object_table, cast(null as varchar(200)) sub_type\n" +
        "          from information_schema.TRIGGERS\n" +
        "        union all\n" +
        "        select 'SEQUENCE' object_type, sequence_schema schema_name, sequence_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, data_type sub_type\n" +
        "          from information_schema.SYSTEM_SEQUENCES)";
    }
    else {
      return
        "select *\n" +
        "  from (select 'TABLE' object_type, table_schem schema_name, table_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(hsqldb_type as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_TABLES\n" +
        "         where table_type in ('TABLE', 'SYSTEM TABLE', 'GLOBAL TEMPORARY')\n" +
        "        union all\n" +
        "        select 'VIEW' object_type, table_schem schema_name, table_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(null as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_TABLES\n" +
        "         where table_type = 'VIEW'\n" +
        "        union all\n" +
        "        select case object_type when 'ROUTINE' then 'ALIAS' else object_type end, object_schem schema_name, alias object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(object_name as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_ALIASES\n" +
        "        union all\n" +
        "        select case procedure_type when 0 then 'UNKNOWN' when 1 then 'PROCEDURE' when 2 then 'FUNCTION' end object_type,\n" +
        "               procedure_schem schema_name, procedure_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, cast(origin as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_PROCEDURES\n" +
        "         where origin <> 'ALIAS'\n" +
        "        union all\n" +
        "        select distinct 'INDEX' object_type, table_schem schema_name, index_name object_name, table_schem table_schema, table_name, \n" +
        "               case asc_or_desc when 'A' then 'ASC ' when 'D' then 'DESC ' end||case non_unique when true then '' when false then 'UNIQUE ' end||case type when 3 then 'OTHER' when 2 then 'HASHED' when 1 then 'CLUSTERED' end sub_type\n" +
        "          from information_schema.SYSTEM_INDEXINFO\n" +
        "        union all\n" +
        "        select 'TRIGGER' object_type, trigger_schem schema_name, trigger_name object_name,\n" +
        "               table_schem table_schema, table_name, cast(null as varchar(200)) sub_type\n" +
        "          from information_schema.SYSTEM_TRIGGERS\n" +
        "        union all\n" +
        "        select 'SEQUENCE' object_type, sequence_schema schema_name, sequence_name object_name, cast(null as varchar(500)) table_schema, cast(null as varchar(500)) table_name, dtd_identifier sub_type\n" +
        "          from information_schema.SYSTEM_SEQUENCES)";
    }
  }
  
  public static String getObjectsType(int version) {
    return getObjectsType(null, version);
  }
  
  public static String getObjectsType(String filter, int version) {
    return String.format(
      getAllObjectList(version) +
      "\n where (schema_name = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (object_name = :OBJECT_NAME or :OBJECT_NAME is null)\n" +
      "%s" +
      " order by schema_name, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectsSearch(String filter, int version) {
    return getAllObjectList(version) +String.format(
      "\n where (schema_name = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (object_name = :OBJECT_NAME or :OBJECT_NAME is null or object_name like '%%'||upper(:OBJECT_NAME)||'%%')\n" +
      "%s" +
      " order by schema_name, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getObjectsName(int version) {
    return getAllObjectList(version) +
      "\n where (schema_name = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
      "   and (object_type = :OBJECT_TYPE or :OBJECT_TYPE is null)\n" +
      " order by schema_name, object_name";
  }
  
  public static String getCurrentSchema(int version) {
    return String.format(
      "select value schema_name from information_schema.SYSTEM_SESSIONINFO where key = '%s'",
      new Object[] {(version >= HSqlDbInfoProvider.hsqlDb20 ? "CURRENT SCHEMA" : "SCHEMA")});
  }
  
  public static String getSchemaList() {
    return "select table_schem schema_name from information_schema.SYSTEM_SCHEMAS order by schema_name";
  }

  public static String getSessionList(String filter, int version) {
    if (version >= HSqlDbInfoProvider.hsqlDb20) {
      return String.format(
        "SELECT SESSION_ID, CONNECTED, USER_NAME, IS_ADMIN, AUTOCOMMIT, READONLY, LAST_IDENTITY, TRANSACTION_SIZE, SCHEMA, TRANSACTION, WAITING_FOR_THIS, THIS_WAITING_FOR, CURRENT_STATEMENT,\n" +
        "       CASE WHEN (SELECT VALUE FROM INFORMATION_SCHEMA.SYSTEM_SESSIONINFO WHERE KEY = 'SESSION ID') = SESSION_ID THEN 'T' ELSE 'N' END THIS_SESSION\n" +
        "  FROM INFORMATION_SCHEMA.SYSTEM_SESSIONS\n" +
        "%s" +
        " ORDER BY CONNECTED",
        new Object[]{filter != null ? " where " + filter + "\n" : ""});
    }
    else {
      return String.format(
        "SELECT SESSION_ID, CONNECTED, USER_NAME, IS_ADMIN, AUTOCOMMIT, READONLY, MAXROWS, LAST_IDENTITY, TRANSACTION_SIZE, SCHEMA, 'N' THIS_SESSION\n" +
        "  FROM INFORMATION_SCHEMA.SYSTEM_SESSIONS\n" +
        "%s" +
        " ORDER BY CONNECTED",
        new Object[]{filter != null ? " where " + filter + "\n" : ""});
    }
  }

}
