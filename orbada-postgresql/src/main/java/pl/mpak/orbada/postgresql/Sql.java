/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql;

import java.util.EnumSet;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public enum ObjectSub {
    Generals, // tabele, indeksy, materialized view, sequence, view
    Columns, // kolumny, widoków, tabel, etc
    Schemas,
    Tablespaces,
    Functions,
    Languages,
    Triggers,
    Types,
    Databases,
    Conversions,
    Operators,
    Collations,
    Extensions,
    Rules,
    OpClasses,
    OpFamilies,
    Casts,
    Roles,
    Constraints,
    DefaultValues
  }
  static EnumSet<ObjectSub> objectSubAll = EnumSet.of(
    ObjectSub.Generals, ObjectSub.Columns, ObjectSub.Schemas, ObjectSub.Tablespaces, ObjectSub.Functions,
    ObjectSub.Languages, ObjectSub.Triggers, ObjectSub.Types, ObjectSub.Databases, ObjectSub.Conversions,
    ObjectSub.Operators, ObjectSub.Collations, ObjectSub.Extensions, ObjectSub.Rules, ObjectSub.OpClasses,
    ObjectSub.OpFamilies, ObjectSub.Casts, ObjectSub.Roles, ObjectSub.Constraints, ObjectSub.DefaultValues);
  static EnumSet<ObjectSub> objectSubSourcable = EnumSet.of(
    ObjectSub.Generals, ObjectSub.Functions, ObjectSub.Triggers, ObjectSub.Rules);
  static EnumSet<ObjectSub> objectSubDepends = EnumSet.of(
    ObjectSub.Generals, ObjectSub.Columns, ObjectSub.Schemas, ObjectSub.Functions,
    ObjectSub.Languages, ObjectSub.Triggers, ObjectSub.Types, ObjectSub.Conversions,
    ObjectSub.Operators, ObjectSub.Collations, ObjectSub.Extensions, ObjectSub.Rules, ObjectSub.OpClasses,
    ObjectSub.OpFamilies, ObjectSub.Casts, ObjectSub.Roles, ObjectSub.Constraints, ObjectSub.DefaultValues);
  static EnumSet<ObjectSub> objectSubObjects = EnumSet.of(
    ObjectSub.Generals, ObjectSub.Schemas, ObjectSub.Tablespaces, ObjectSub.Functions,
    ObjectSub.Languages, ObjectSub.Triggers, ObjectSub.Types, ObjectSub.Databases, ObjectSub.Conversions,
    ObjectSub.Operators, ObjectSub.Collations, ObjectSub.Extensions, ObjectSub.Rules, ObjectSub.Roles,
    ObjectSub.Constraints);

  public static String getSetAppInfo() {
    return "set application_name = 'Orbada PostgreSQL Plugin'";
  }
  
  public static String getSystemFunctionList() {
    return
      "select distinct p.proname proc_name\n" +
      "  from pg_catalog.pg_proc p\n" +
      "       join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      " where n.nspname in ('pg_catalog')\n" +
      "   and not exists \n" +
      "       (select null\n" +
      "          from pg_catalog.pg_type t\n" +
      "         where not exists \n" +
      "               (select null from pg_catalog.pg_class c\n" +
      "                 where c.oid = t.typrelid and c.relkind in ('r', 'v', 'i', 'S', 't'))\n" +
      "                   and t.typelem = 0\n" +
      "                   and t.typtype in ('b', 'd', 'e', 'c', 'r')\n" +
      "                   and t.typname = p.proname)\n" +
      "   and not exists (select * from pg_catalog.pg_trigger t where t.tgfoid = p.oid)\n" +
      "   and not exists (select word from pg_get_keywords() where p.proname = word)";
  }

  public static String getKeywordList() {
    return "select word from pg_get_keywords() where word not in (\n" +getDataTypes() +"\n) and word not in ('version', 'current_schema', 'replace', 'name', 'coalesce')";
  }

  public static String getUserFunctionList() {
    return
      "select p.proname proc_name\n" +
      "  from pg_catalog.pg_proc p\n" +
      "       join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      " where n.nspname = any (current_schemas(false))\n" +
      "   and not exists\n" +
      "       (select null\n" +
      "          from pg_catalog.pg_type t\n" +
      "         where not exists \n" +
      "               (select null from pg_catalog.pg_class c\n" +
      "                 where c.oid = t.typrelid and c.relkind in ('r', 'v', 'i', 'S', 't'))\n" +
      "                   and t.typelem = 0\n" +
      "                   and t.typtype in ('b', 'd', 'e', 'c', 'r')\n" +
      "                   and t.typname = p.proname)\n" +
      "   and p.proname not in ('left', 'like', 'delete')\n" +
      "union\n" +
      "select n.nspname||'.'||p.proname proc_name\n" +
      "  from pg_catalog.pg_proc p\n" +
      "       join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      " where not (n.nspname = any (current_schemas(false))) and n.nspname not in ('public', 'pg_catalog', 'information_schema')\n" +
      "   and not exists\n" +
      "       (select null\n" +
      "          from pg_catalog.pg_type t\n" +
      "         where not exists \n" +
      "               (select null from pg_catalog.pg_class c\n" +
      "                 where c.oid = t.typrelid and c.relkind in ('r', 'v', 'i', 'S', 't'))\n" +
      "                   and t.typelem = 0\n" +
      "                   and t.typtype in ('b', 'd', 'e', 'c', 'r')\n" +
      "                   and t.typname = p.proname)\n" +
      "   and p.proname not in ('left', 'like', 'delete')";
  }
  
  public static String getSystemTables() {
    return
      "select relname as table_name\n" +
      "  from pg_catalog.pg_class c\n" +
      "       join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      " where n.nspname in ('pg_catalog', 'information_schema')\n" +
      "   and c.relkind in ('r', 'v')";
  }
  
  public static String getUserTables() {
    return
      "select n.nspname||'.'||c.relname table_name\n" +
      "  from pg_catalog.pg_class c\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      " where c.relkind in ('r', 'v')\n" +
      "   and pg_catalog.has_table_privilege(c.oid, 'SELECT')\n" +
      "   and not (n.nspname = any (current_schemas(false))) and n.nspname not in ('public', 'pg_catalog', 'information_schema')\n" +
      "union all\n" +
      "select relname as table_name\n" +
      "  from pg_catalog.pg_class c\n" +
      "       join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      " where n.nspname = any (current_schemas(false))\n" +
      "   and c.relkind in ('r', 'v')";
  }
  
  public static String getDataTypes() {
    return
      "with t as (\n" +
      "  select oid, t.typname\n" +
      "    from pg_catalog.pg_type t\n" +
      "   where not exists (select null from pg_catalog.pg_class c where c.oid = t.typrelid and c.relkind in ('r', 'v', 'i', 'S', 't'))\n" +
      "     and t.typelem = 0\n" +
      "     and t.typtype in ('b', 'd', 'e', 'c', 'r'))\n" +
      "select typname type_name\n" +
      "  from t\n" +
      "union\n" +
      "select unnest(string_to_array(replace(replace(pg_catalog.format_type(t.oid, null), '\"', ''), 'information_schema.', ''), ' ')) type_name\n" +
      "  from t";
  }
  
  public static String getCurrentSchema() {
    return "select current_schema() schema_name";
  }
  
  public static String getIsSuperuser() {
    return "select usesuper from pg_user where usename = current_user";
  }
  
  public static String getVersion() {
    return "select version() v";
  }
  
  public static String getTableList(String filter) {
    return String.format(
      "select schema_name, table_name, owner_name, table_space, description, accessible, inheritance, quote_ident(schema_name)||'.'||quote_ident(table_name) full_object_name\n" +
      "  from (select n.nspname as schema_name, c.relname as table_name, pg_catalog.pg_get_userbyid(c.relowner) as owner_name, \n" +
      "               coalesce(t.spcname, (select spcname from pg_database d join pg_tablespace t on t.oid = d.dattablespace where d.datname = case when n.nspname in ('pg_catalog', 'information_schema') then 'postgres' else current_database() end)) as table_space,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(c.relowner, 'USAGE') then 'USAGE'\n" +
      "                 when (select true from pg_catalog.aclexplode(c.relacl) g where grantee = 0 limit 1) then 'PUBLIC'\n" +
      "                 --when pg_catalog.has_table_privilege('public', c.oid, 'SELECT, INSERT, UPDATE, DELETE, REFERENCES') then 'BY PUBLIC ROLE' \n" +
      "                 when pg_catalog.has_table_privilege(c.oid, 'SELECT, INSERT, UPDATE, DELETE') then 'GRANTED' \n" +
      "                 when pg_catalog.has_any_column_privilege(c.oid, 'SELECT, INSERT, UPDATE') then 'COLUMN'\n" +
      "               else 'NO' end accessible,\n" +
      "               d.description,\n" +
      "               (select 'inherits' from pg_catalog.pg_inherits i where i.inhrelid = c.oid union all select 'inherited' from pg_catalog.pg_inherits i where i.inhparent = c.oid limit 1) inheritance\n" +
      "          from pg_catalog.pg_class c\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               left join pg_catalog.pg_tablespace t on t.oid = c.reltablespace\n" +
      "               left join pg_catalog.pg_description d on d.classoid = 'pg_class'::regclass and d.objoid = c.oid and d.objsubid = 0\n" +
      "         where c.relkind = 'r'::\"char\") t\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +
      "%s" +
      " order by schema_name, table_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getTableColumnList(String filter) {
    return String.format(
      "select schema_name, table_name, column_name, column_no, display_datatype, nullable, default_value, storage_type, \n" +
      "       description, collation_name, fk, pk, accessible\n" +
      "  from (select na.nspname as schema_name, cl.relname as table_name, att.attname as column_name, att.attnum as column_no, \n" +
      "               case when ty.typtype = 'd'::\"char\" and tn.nspname <> 'public' then tn.nspname||'.' else '' end||\n" +
      "                 case when (SELECT true FROM pg_class seq, pg_depend d WHERE seq.relkind = 'S' and d.objid=seq.oid AND d.deptype='a' and d.refobjid = att.attrelid and d.refobjsubid = att.attnum) then\n" +
      "                     case att.atttypid when 23 then 'serial' when 20 then 'bigserial' else pg_catalog.format_type(ty.oid, att.atttypmod) end\n" +
      "                   else pg_catalog.format_type(ty.oid, att.atttypmod)\n" +
      "                 end\n" +
      "                 as display_datatype,\n" +
      "               case when att.attnotnull then 'N' else 'Y' end nullable, pg_catalog.pg_get_expr(def.adbin, def.adrelid) as default_value,\n" +
      "               case att.attstorage when 'x' then 'EXTENDED' when 'p' then 'PLAIN' when 'e' then 'EXTERNAL' when 'm' then 'MAIN' else '???' end as storage_type, \n" +
      "               des.description, case when coll.collencoding <> -1 then nspc.nspname||'.'||coll.collname end as collation_name,\n" +
      "               case when exists (select 1 from pg_catalog.pg_constraint where conrelid = att.attrelid and contype='f' and att.attnum = any(conkey)) then 'Y' else 'N' end as fk,\n" +
      "               case when exists (select 1 from pg_catalog.pg_constraint where conrelid = att.attrelid and contype='p' and att.attnum = any(conkey)) then 'Y' else 'N' end as pk,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(cl.relowner, 'USAGE'::text) then 'USAGE'\n" +
      "                 when pg_catalog.has_table_privilege(cl.oid, 'SELECT, INSERT, UPDATE, DELETE') then 'TABLE'\n" +
      "                 when (select true from pg_catalog.aclexplode(att.attacl) g where grantee = 0 limit 1) then 'PUBLIC'\n" +
      "--                 when pg_catalog.has_column_privilege('public', cl.oid, att.attnum, 'SELECT, INSERT, UPDATE') then 'PUBLIC'\n" +
      "                 when pg_catalog.has_column_privilege(cl.oid, att.attnum, 'SELECT, INSERT, UPDATE'::text) then 'GRANTED'\n" +
      "               else 'NO' end as accessible\n" +
      "          from pg_catalog.pg_attribute att\n" +
      "               join pg_catalog.pg_type ty on ty.oid = atttypid\n" +
      "               join pg_catalog.pg_namespace tn on tn.oid = ty.typnamespace\n" +
      "               join pg_catalog.pg_class cl on cl.oid = att.attrelid\n" +
      "               join pg_catalog.pg_namespace na on na.oid = cl.relnamespace\n" +
      "               left outer join pg_catalog.pg_attrdef def on adrelid = att.attrelid and adnum = att.attnum\n" +
      "               left outer join pg_catalog.pg_description des on des.classoid = 'pg_class'::regclass and des.objoid = att.attrelid and des.objsubid = att.attnum\n" +
      "               left outer join pg_catalog.pg_collation coll on att.attcollation = coll.oid\n" +
      "               left outer join pg_catalog.pg_namespace nspc on coll.collnamespace = nspc.oid\n" +
      "         where att.attnum > 0) c\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      "%s" +
      " order by column_no",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getViewList(String filter) {
    return String.format(
      "select schema_name, view_name, owner_name, description, accessible, quote_ident(schema_name)||'.'||quote_ident(view_name) full_object_name\n" +
      "  from (select n.nspname as schema_name, c.relname as view_name, pg_catalog.pg_get_userbyid(c.relowner) as owner_name,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(c.relowner, 'USAGE') then 'USAGE'\n" +
      "                 when (select true from pg_catalog.aclexplode(c.relacl) g where grantee = 0 limit 1) then 'PUBLIC'\n" +
      "                 --when pg_catalog.has_table_privilege('public', c.oid, 'SELECT, INSERT, UPDATE, DELETE') then 'BY PUBLIC ROLE' \n" +
      "                 when pg_catalog.has_table_privilege(c.oid, 'SELECT, INSERT, UPDATE, DELETE') then 'GRANTED' \n" +
      "                 when pg_catalog.has_any_column_privilege(c.oid, 'SELECT, INSERT, UPDATE') then 'COLUMN'\n" +
      "               else 'NO' end accessible,\n" +
      "               d.description\n" +
      "          from pg_catalog.pg_class c\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               left join pg_catalog.pg_description d on d.classoid = 'pg_class'::regclass and d.objoid = c.oid and d.objsubid = 0\n" +
      "         where c.relkind = 'v'::\"char\") t\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +
      "%s" +
      " order by schema_name, view_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getViewColumnList(String filter) {
    return String.format(
      "select schema_name, view_name, column_name, column_no, display_datatype, default_value, description, collation_name, accessible\n" +
      "  from (select na.nspname as schema_name, cl.relname as view_name, att.attname as column_name, att.attnum as column_no, \n" +
      "               pg_catalog.format_type(ty.oid, att.atttypmod) as display_datatype, \n" +
      "               pg_catalog.pg_get_expr(def.adbin, def.adrelid) as default_value,\n" +
      "               des.description, case when coll.collencoding <> -1 then nspc.nspname||'.'||coll.collname end as collation_name,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(cl.relowner, 'USAGE'::text) then 'USAGE'\n" +
      "                 when pg_catalog.has_table_privilege(cl.oid, 'SELECT, INSERT, UPDATE, DELETE') then 'VIEW'\n" +
      "                 when (select true from pg_catalog.aclexplode(att.attacl) g where grantee = 0 limit 1) then 'PUBLIC'\n" +
      "--                 when pg_catalog.has_column_privilege('public', cl.oid, att.attnum, 'SELECT, INSERT, UPDATE') then 'PUBLIC'\n" +
      "                 when pg_catalog.has_column_privilege(cl.oid, att.attnum, 'SELECT, INSERT, UPDATE'::text) then 'GRANTED'\n" +
      "               else 'NO' end as accessible\n" +
      "          from pg_catalog.pg_attribute att\n" +
      "               join pg_catalog.pg_type ty on ty.oid = atttypid\n" +
      "               join pg_catalog.pg_class cl on cl.oid = att.attrelid\n" +
      "               join pg_catalog.pg_namespace na on na.oid = cl.relnamespace\n" +
      "               left outer join pg_catalog.pg_attrdef def on adrelid = att.attrelid and adnum = att.attnum\n" +
      "               left outer join pg_catalog.pg_description des on des.classoid = 'pg_class'::regclass and des.objoid = att.attrelid and des.objsubid = att.attnum\n" +
      "               left outer join pg_catalog.pg_collation coll on att.attcollation = coll.oid\n" +
      "               left outer join pg_catalog.pg_namespace nspc on coll.collnamespace = nspc.oid\n" +
      "         where att.attnum > 0) c\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and view_name = :VIEW_NAME\n" +
      "%s" +
      " order by column_no",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getTableAccessible() {
    return
      "select accessible\n" +
      "  from (select n.nspname as schema_name, c.relname as table_name,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(c.relowner, 'USAGE') then 'USAGE'\n" +
      "                 when pg_catalog.has_table_privilege(c.oid, 'SELECT, INSERT, UPDATE, DELETE') then 'GRANTED' \n" +
      "                 when pg_catalog.has_any_column_privilege(c.oid, 'SELECT, INSERT, UPDATE') then 'COLUMN'\n" +
      "               else 'NO' end accessible\n" +
      "          from pg_catalog.pg_class c\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "         where c.relkind in ('r', 'v')) t\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME";
  }
  
  public static String getTableAccessibleColumnList() {
    return
      "select string_agg(quote_ident(table_name)||'.'||quote_ident(column_name), ', ') columns\n" +
      "  from (select na.nspname as schema_name, cl.relname as table_name, att.attname as column_name, att.attnum as column_no,\n" +
      "               pg_catalog.has_column_privilege(cl.oid, att.attnum, 'SELECT, INSERT, UPDATE'::text) selectable\n" +
      "          from pg_catalog.pg_attribute att\n" +
      "               join pg_catalog.pg_class cl on cl.oid = att.attrelid\n" +
      "               join pg_catalog.pg_namespace na on na.oid = cl.relnamespace\n" +
      "         where att.attnum > 0\n" +
      "         order by column_no) c\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and table_name = :TABLE_NAME\n" +
      "   and selectable";
  }
  
  public static String getIndexList(String filter) {
    return String.format(
      "select schema_name, owner_name, index_name, uniqueness, clustered, index_pk, table_space, table_name, method, index_expr, description\n" +
      "  from (select ns.nspname as schema_name, pg_catalog.pg_get_userbyid(c.relowner) as owner_name, c.relname as index_name,\n" +
      "               case when i.indisunique then 'Y' else 'N' end as uniqueness,\n" +
      "               case when i.indisclustered then 'Y' else 'N' end  as clustered, \n" +
      "               case when i.indisprimary then 'Y' else 'N' end  as index_pk,\n" +
      "               coalesce(s.spcname, (select dt.spcname from pg_catalog.pg_database d left join pg_catalog.pg_tablespace dt on dt.oid = d.dattablespace where d.datname = current_database())) as table_space,\n" +
      "               t.relname table_name, am.amname as method,\n" +
      "               coalesce(\n" +
      "                 '('||array_to_string(array(select pg_catalog.pg_get_indexdef(i.indexrelid, k + 1, true) from pg_catalog.generate_subscripts(i.indkey, 1) as k order by k), ', ')||') WHERE '||pg_catalog.pg_get_expr(i.indpred, i.indrelid, true),\n" +
      "                 array_to_string(array(select pg_catalog.pg_get_indexdef(i.indexrelid, k + 1, true) from pg_catalog.generate_subscripts(i.indkey, 1) as k order by k), ', ')\n" +
      "               ) as index_expr,\n" +
      "               d.description as description\n" +
      "          from pg_catalog.pg_index i\n" +
      "               join pg_catalog.pg_class c on c.oid = i.indexrelid\n" +
      "               join pg_catalog.pg_class t on t.oid = i.indrelid\n" +
      "               join pg_catalog.pg_am am on c.relam = am.oid\n" +
      "               join pg_catalog.pg_namespace ns on ns.oid = c.relnamespace\n" +
      "               left join pg_catalog.pg_tablespace s on s.oid = c.reltablespace\n" +
      "               left join pg_catalog.pg_description d on d.classoid = 'pg_class'::regclass and d.objoid = c.oid and d.objsubid = 0) i\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public' and :TABLE_NAME is null))\n" +
      "   and (table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "%s" +
      " order by index_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getConstraintList(String filter) {
    return String.format(
      "select schema_name, owner_name, constraint_name, table_name, columns, fk_table_name, fk_columns, constraint_type, \n" +
      "       \"deferrable\", \"deferred\", validated, update_rule, delete_rule, match_type, domain_name, constraint_def, description\n" +
      "  from (select ns.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) as owner_name, con.conname constraint_name, \n" +
      "               c.relname table_name, array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select con.conkey[k] col from pg_catalog.generate_subscripts(con.conkey, 1) k) k on k.col = a.attnum and c.oid = a.attrelid), ', ') columns,\n" +
      "               r.relname fk_table_name, array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select con.confkey[k] col from pg_catalog.generate_subscripts(con.confkey, 1) k) k on k.col = a.attnum and c.oid = a.attrelid), ', ') fk_columns,\n" +
      "               case con.contype when 'c' then 'CHECK'::text when 'p' then 'PRIMARY KEY'::text when 'f' then 'FOREIGN KEY'::text when 'u' then 'UNIQUE'::text when 't' then 'TRIGGER'::text when 'x' then 'EXCLUDE'::text else con.contype end constraint_type,\n" +
      "               case when con.condeferrable then 'Y' else 'N' end \"deferrable\",\n" +
      "               case when con.condeferred then 'Y' else 'N' end \"deferred\",\n" +
      "               case when con.convalidated then 'Y' else 'N' end validated,\n" +
      "               case con.confupdtype when 'a' then null when 'r' then 'RESTRICT' when 'c' then 'CASCADE' when 'n' then 'SET NULL' when 'd' then 'SET DEFAULT' end update_rule,\n" +
      "               case con.confdeltype when 'a' then null when 'r' then 'RESTRICT' when 'c' then 'CASCADE' when 'n' then 'SET NULL' when 'd' then 'SET DEFAULT' end delete_rule,\n" +
      "               case con.confmatchtype when 'f' then 'FULL' when 'p' then 'PARTIAL' when 'u' then 'SIMPLE' end match_type,\n" +
      "               t.typname domain_name,\n" +
      "               pg_get_constraintdef(con.oid, true) constraint_def,\n" +
      "               d.description\n" +
      "          from pg_catalog.pg_constraint con\n" +
      "               left join pg_catalog.pg_namespace ns on ns.oid = con.connamespace\n" +
      "               left join pg_catalog.pg_class c on c.oid = con.conrelid\n" +
      "               left join pg_catalog.pg_type t on t.oid = con.contypid\n" +
      "               left join pg_catalog.pg_description d on d.classoid = 'pg_constraint'::regclass and d.objoid = con.oid and d.objsubid = 0\n" +
      "               left join pg_catalog.pg_class r on r.oid = con.confrelid\n" +
      "        union all\n" +
      "        select ns.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) as owner_name, null::text constraint_name, \n" +
      "               c.relname table_name, a.attname columns, null::text fk_table_name, null::text fk_columns,\n" +
      "               'NOT NULL' constraint_type, 'N' \"deferrable\", 'N' \"deferred\", 'Y' validated, null::text update_rule, null::text delete_rule, null::text match_type, null::text domain_name,\n" +
      "               a.attname||' IS NOT NULL' constraint_def, null::text description\n" +
      "          from pg_catalog.pg_namespace ns\n" +
      "               join pg_catalog.pg_class c on ns.oid = c.relnamespace\n" +
      "               join pg_catalog.pg_attribute a on c.oid = a.attrelid\n" +
      "         where a.attnum > 0\n" +
      "           and not a.attisdropped\n" +
      "           and a.attnotnull) c\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public' and :TABLE_NAME is null))\n" +
      "   and (table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "%s" +
      " order by constraint_name, table_name, columns",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getTriggerList(String filter) {
    return String.format(
      "select schema_name, trigger_name, enable_mode, action_timing||' '||action_orientation action_timing, trigger_event, columns, object_name, action_condition, full_function_name, action_orientation, description, quote_ident(trigger_name)||' ON TABLE '||quote_ident(schema_name)||'.'||quote_ident(object_name) full_object_name\n" +
      "  from (select ns.nspname schema_name, t.tgname trigger_name, case t.tgenabled when 'O' then 'ENABLED' when 'D' then 'DISABLED' when 'R' then 'REPLICA' when 'A' then 'ALWAYS' end enable_mode,\n" +
      "               case ((t.tgtype)::integer & 66) when 2 then 'BEFORE'::text when 64 then 'INSTEAD OF'::text else 'AFTER'::text end action_timing,\n" +
      "               array_to_string(array(select em.text from (values (4,'INSERT'::text), (8,'DELETE'::text), (16,'UPDATE'::text)) em(num, text) where (t.tgtype::integer & em.num) <> 0), ' ') trigger_event,\n" +
      "               c.relname object_name, array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select t.tgattr[k] col from pg_catalog.generate_subscripts(t.tgattr, 1) k) k on k.col = a.attnum and c.oid = a.attrelid), ', ') columns,\n" +
      "               case when pg_has_role(c.relowner, 'USAGE'::text) then (select rm.m[1] as m from regexp_matches(pg_get_triggerdef(t.oid), '.{35,} WHEN \\((.+)\\) EXECUTE PROCEDURE'::text) rm(m) limit 1) else null::text end action_condition,\n" +
      "               p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' full_function_name,\n" +
      "               case (t.tgtype::integer & 1) when 1 then 'ROW'::text else 'STATEMENT'::text end action_orientation, d.description\n" +
      "          from pg_catalog.pg_namespace ns\n" +
      "               join pg_catalog.pg_class c on ns.oid = c.relnamespace\n" +
      "               join pg_catalog.pg_trigger t on c.oid = t.tgrelid\n" +
      "               join pg_catalog.pg_proc p on p.oid = t.tgfoid\n" +
      "               left join pg_catalog.pg_description d on d.classoid = 'pg_trigger'::regclass and d.objoid = t.oid and d.objsubid = 0\n" +
      "         where not t.tgisinternal\n" +
      "           and not pg_is_other_temp_schema(ns.oid)) t\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public' and :OBJECT_NAME is null))\n" +
      "   and (object_name = :OBJECT_NAME or :OBJECT_NAME is null)\n" +
      "%s" +
      " order by trigger_name, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getTriggerFunctionList(String filter) {
    return String.format(
      "select schema_name, object_name, ns.nspname function_schema_name, p.proname function_name, pg_get_function_arguments(p.oid) function_arguments,\n" +
      "       array_to_string(array(select tgname from pg_catalog.pg_trigger t where f.foid = t.tgfoid and f.coid = t.tgrelid), ', ') triggers,\n" +
      "       p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' full_function_name\n" +
      "  from (select schema_name, object_name, foid, coid\n" +
      "          from (select distinct ns.nspname schema_name, c.relname object_name, t.tgfoid foid, c.oid coid\n" +
      "                  from pg_catalog.pg_trigger t\n" +
      "                       join pg_catalog.pg_class c on c.oid = t.tgrelid\n" +
      "                       join pg_catalog.pg_namespace ns on ns.oid = c.relnamespace\n" +
      "                 where not t.tgisinternal\n" +
      "                   and not pg_is_other_temp_schema(ns.oid)) t\n" +
      "         where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public' and :OBJECT_NAME is null))\n" +
      "           and (object_name = :OBJECT_NAME or :OBJECT_NAME is null)) f\n" +
      "       join pg_catalog.pg_proc p on p.oid = f.foid\n" +
      "       join pg_catalog.pg_namespace ns on ns.oid = p.pronamespace\n" +
      "%s" +
      " order by function_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getRuleList(String filter) {
    return String.format(
      "select schema_name, object_name, rule_name, object_type, enable_mode, rule_type, is_instead, description\n" +
      "  from (select n.nspname as schema_name, c.relname as object_name, r.rulename rule_name, d.description,\n" +
      "               case c.relkind when 'r' then 'TABLE' when 'v' then 'VIEW' else c.relkind::text end object_type,\n" +
      "               case r.ev_enabled when 'O' then 'ENABLED' when 'D' then 'DISABLED' when 'R' then 'REPLICA' when 'A' then 'ALWAYS' end enable_mode,\n" +
      "               case r.ev_type when 1::char then 'SELECT' when 2::char then 'UPDATE' when 3::char then 'INSERT' when 4::char then 'DELETE' end rule_type,\n" +
      "               case when r.is_instead then 'Y' else 'N' end is_instead\n" +
      "          from pg_catalog.pg_rewrite r\n" +
      "               join pg_catalog.pg_class c on c.oid = r.ev_class\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               left join pg_catalog.pg_description d on d.objoid = r.oid) r\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public' and :OBJECT_NAME is null))\n" +
      "   and (object_name = :OBJECT_NAME or :OBJECT_NAME is null)\n" +
      "%s" +
      " order by schema_name, object_name, rule_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getTriggerSource() {
    return
      "select schema_name, trigger_name, object_name, function_call, description, source\n" +
      "  from (select n.nspname schema_name, quote_ident(t.tgname) trigger_name, \n" +
      "               quote_ident(n.nspname)||'.'||quote_ident(c.relname) object_name,\n" +
      "               p.proname function_call, quote_literal(coalesce(d.description, '')) description,\n" +
      "               pg_get_triggerdef(t.oid, true) source\n" +
      "          from pg_catalog.pg_trigger t\n" +
      "               join pg_catalog.pg_class c on c.oid = t.tgrelid\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               left join pg_catalog.pg_description d on d.classoid = 'pg_trigger'::regclass and d.objoid = t.oid and d.objsubid = 0\n" +
      "               join pg_catalog.pg_proc p on p.oid = t.tgfoid\n" +
      "         where n.nspname = :SCHEMA_NAME\n" +
      "           and c.relname = :OBJECT_NAME\n" +
      "           and t.tgname = :TRIGGER_NAME) t";
  }

  public static String getFunctionSource() {
    return
      "select pg_get_functiondef(p.oid) source, quote_literal(coalesce(d.description, '')) description, \n" +
      "       quote_ident(n.nspname)||'.'||quote_ident(p.proname)||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' function_prot,\n" +
      "       quote_ident(pg_catalog.pg_get_userbyid(p.proowner)) owner_name\n" +
      "  from pg_catalog.pg_proc p\n" +
      "       join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      "       left join pg_catalog.pg_description d on d.classoid = 'pg_proc'::regclass and d.objoid = p.oid and d.objsubid = 0\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' = :FUNCTION_NAME";
  }

  public static String getRuleSource() {
    return
      "select pg_get_ruledef(r.oid, true) source, quote_literal(d.description) description, quote_ident(r.rulename) rule_name,\n" +
      "       quote_ident(n.nspname)||'.'||quote_ident(c.relname) object_name\n" +
      "  from pg_catalog.pg_rewrite r\n" +
      "       join pg_catalog.pg_class c on c.oid = r.ev_class\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "       left join pg_catalog.pg_description d on d.classoid = 'pg_rewrite'::regclass and d.objoid = r.oid and d.objsubid = 0\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and c.relname = :OBJECT_NAME\n" +
      "   and r.rulename = :RULE_NAME";
  }
  
  public static String getViewSource() {
    return
      "select pg_get_viewdef(c.oid, true) source, quote_literal(coalesce(d.description, '')) description, \n" +
      "       quote_ident(n.nspname)||'.'||quote_ident(c.relname) view_name, quote_ident(pg_catalog.pg_get_userbyid(c.relowner)) owner_name\n" +
      "  from pg_catalog.pg_class c\n" +
      "       join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "       left join pg_catalog.pg_description d on d.objoid = c.oid\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and c.relname = :VIEW_NAME";
  }
  
  public static String getTableSizeList(String filter) {
    return String.format(
      "select tablespace_name, owner_name, schema_name, table_name, total_size_b, indexes_size_b, total_size_mb, prc_size, stat_count, record_length, last_analyze, last_vacuum, index_count, description\n" +
      "  from (select tablespace_name, pg_catalog.pg_get_userbyid(relowner) owner_name, schema_name, table_name, (total_size_b +inh_total_size_b)::numeric total_size_b, (indexes_size_b +inh_indexes_size_b)::numeric indexes_size_b,\n" +
      "               round((total_size_b +inh_total_size_b)::numeric /1024 /1024, 2) total_size_mb, \n" +
      "               round((total_size_b +inh_total_size_b)::numeric /case when sum((total_size_b +inh_total_size_b)) over () = 0 then 1 else sum((total_size_b +inh_total_size_b)) over () end *100, 2)::numeric prc_size,\n" +
      "               (n_live_tup +inh_stat_count)::numeric stat_count, case when (n_live_tup +inh_stat_count) > 0 then round((total_size_b -indexes_size_b +inh_total_size_b -inh_indexes_size_b) /(n_live_tup +inh_stat_count), 0) else null end record_length, index_count, description,\n" +
      "               case when coalesce(last_autoanalyze, date'0001-1-1') > coalesce(last_analyze, date'0001-1-1') then last_autoanalyze else last_analyze end last_analyze,\n" +
      "               case when coalesce(last_autovacuum, date'0001-1-1') > coalesce(last_vacuum, date'0001-1-1') then last_autovacuum else last_vacuum end last_vacuum\n" +
      "          from (select t.oid relid, tb.spcname tablespace_name, t.relowner, ns.nspname schema_name, t.relname table_name, \n" +
      "                       pg_total_relation_size(t.oid) total_size_b, pg_indexes_size(t.oid) indexes_size_b,\n" +
      "                       (select count(0) from pg_catalog.pg_index i where i.indrelid = t.oid) index_count,\n" +
      "                       coalesce((select sum(pg_total_relation_size(inhrelid)) from pg_catalog.pg_inherits where inhparent = t.oid), 0) inh_total_size_b,\n" +
      "                       coalesce((select sum(pg_indexes_size(inhrelid)) from pg_catalog.pg_inherits where inhparent = t.oid), 0) inh_indexes_size_b,\n" +
      "                       coalesce((select sum(n_live_tup) from pg_catalog.pg_inherits i left join pg_catalog.pg_stat_all_tables s on s.relid = i.inhrelid where i.inhparent = t.oid), 0) inh_stat_count,\n" +
      "                       s.last_autoanalyze, s.last_analyze, s.last_autovacuum, s.last_vacuum, s.n_live_tup,\n" +
      "                       d.description\n" +
      "                  from pg_catalog.pg_class t\n" +
      "                       left join pg_catalog.pg_namespace ns on ns.oid = t.relnamespace\n" +
      "                       left join pg_catalog.pg_tablespace tb on tb.oid = t.reltablespace\n" +
      "                       left join pg_catalog.pg_description d on d.classoid = t.tableoid and d.objoid = t.oid and d.objsubid = 0\n" +
      "                       join pg_catalog.pg_stat_all_tables s on s.relid = t.oid\n" +
      "                 where t.relkind = 'r'\n" +
      "                   and ns.nspname = :SCHEMA_NAME or (ns.nspname = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and ns.nspname <> 'public')) r\n" +
      "       ) r\n" +
      " where (table_name = :TABLE_NAME or :TABLE_NAME is null)\n" +
      "   and table_name ilike '%%'||coalesce(:search_text, '')||'%%'\n" +
      "%s" +
      " order by total_size_b desc",
      new Object[] {filter != null ? "   and " + filter + "\n" : ""}
    );
  }
  
  public static String getTableDetail() {
    return
      "select c.oid, s.relid, s.schemaname, s.relname,\n" +
      "       c.reltype, c.reloftype, c.relowner, c.relam, c.relfilenode, c.reltablespace, c.relpages, c.reltuples, c.reltoastrelid,\n" +
      "       c.reltoastidxid, c.relhasindex, c.relisshared, c.relpersistence, c.relkind, c.relnatts, c.relchecks, c.relhasoids,\n" +
      "       c.relhaspkey, c.relhasrules, c.relhastriggers, c.relhassubclass, c.relfrozenxid, c.relacl, c.reloptions,\n" +
      "       s.seq_scan, s.seq_tup_read, s.idx_scan, s.idx_tup_fetch, s.n_tup_ins, s.n_tup_upd,\n" +
      "       s.n_tup_del, s.n_tup_hot_upd, s.n_live_tup, s.n_dead_tup, s.last_vacuum, s.last_autovacuum, s.last_analyze, \n" +
      "       s.last_autoanalyze, s.vacuum_count, s.autovacuum_count, s.analyze_count, s.autoanalyze_count,\n" +
      "       sio.heap_blks_read, sio.heap_blks_hit, sio.idx_blks_read, sio.idx_blks_hit, sio.toast_blks_read, sio.toast_blks_hit, \n" +
      "       sio.tidx_blks_read, sio.tidx_blks_hit\n" +
      "  from pg_catalog.pg_stat_all_tables s\n" +
      "       join pg_catalog.pg_statio_all_tables sio on s.schemaname = sio.schemaname and s.relname = sio.relname\n" +
      "       join pg_catalog.pg_class c on c.oid = s.relid\n" +
      " where s.schemaname = :SCHEMA_NAME\n" +
      "   and s.relname = :TABLE_NAME";
  }

  public static String getViewDetail() {
    return
      "select c.oid, c.reltype, c.reloftype, c.relowner, c.relam, c.relfilenode, c.reltablespace, c.relpages, c.reltuples, c.reltoastrelid,\n" +
      "       c.reltoastidxid, c.relhasindex, c.relisshared, c.relpersistence, c.relkind, c.relnatts, c.relchecks, c.relhasoids,\n" +
      "       c.relhaspkey, c.relhasrules, c.relhastriggers, c.relhassubclass, c.relfrozenxid, c.relacl, c.reloptions\n" +
      "  from pg_catalog.pg_class c\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and c.relname = :VIEW_NAME";
  }

  public static String getPrivilegeList(String filter) {
    return String.format(
      "select schema_name, object_name, grantor, grantee, privilege, is_grantable\n" +
      "  from (select schema_name, object_name, \n" +
      "               case when grantor = 0 then 'public' else pg_catalog.pg_get_userbyid(grantor) end grantor,\n" +
      "               case when grantee = 0 then 'public' else pg_catalog.pg_get_userbyid(grantee) end grantee,\n" +
      "               privilege_type privilege, is_grantable\n" +
      "          from (select n.nspname schema_name, c.relname object_name,\n" +
      "                       (aclexplode(c.relacl)).grantor as grantor,\n" +
      "                       (aclexplode(c.relacl)).grantee as grantee,\n" +
      "                       (aclexplode(c.relacl)).privilege_type as privilege_type,\n" +
      "                       (aclexplode(c.relacl)).is_grantable as is_grantable\n" +
      "                  from pg_catalog.pg_class c\n" +
      "                       left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "                 where n.nspname = :SCHEMA_NAME\n" +
      "                   and c.relname = :OBJECT_NAME) g) g\n" +
      "%s" +
      " order by schema_name, object_name, grantee, privilege",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getPrivilegeCommandList(String filter) {
    return String.format(
      "select 'GRANT '||string_agg(privilege, ', ')||' ON TABLE '||quote_ident(schema_name)||'.'||quote_ident(object_name)||' TO '||grantee||case when is_grantable then ' WITH GRANT OPTION' else '' end command\n" +
      "  from (select schema_name, object_name,\n" +
      "               case when grantor = 0 then 'public' else pg_catalog.pg_get_userbyid(grantor) end grantor,\n" +
      "               case when grantee = 0 then 'public' else pg_catalog.pg_get_userbyid(grantee) end grantee,\n" +
      "               privilege_type privilege, is_grantable\n" +
      "          from (select n.nspname schema_name, c.relname object_name,\n" +
      "                       (aclexplode(c.relacl)).grantor as grantor,\n" +
      "                       (aclexplode(c.relacl)).grantee as grantee,\n" +
      "                       (aclexplode(c.relacl)).privilege_type as privilege_type,\n" +
      "                       (aclexplode(c.relacl)).is_grantable as is_grantable\n" +
      "                  from pg_catalog.pg_class c\n" +
      "                       left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "                 where n.nspname = :SCHEMA_NAME\n" +
      "                   and c.relname = :OBJECT_NAME) g) g\n" +
      "%s" +
      " group by schema_name, object_name, grantor, grantee, is_grantable\n" +
      " order by schema_name, object_name, grantee",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getColumnPrivilegeList(String filter) {
    return String.format(
      "select schema_name, object_name, column_no, column_name, grantor, grantee, privilege, is_grantable\n" +
      "  from (select n.nspname schema_name, c.relname object_name, a.attnum column_no, a.attname column_name,\n" +
      "               case when grantor = 0 then 'public' else pg_catalog.pg_get_userbyid(grantor) end grantor,\n" +
      "               case when grantee = 0 then 'public' else pg_catalog.pg_get_userbyid(grantee) end grantee, \n" +
      "               privilege_type privilege, case when is_grantable then 'Y' else 'N' end is_grantable\n" +
      "          from pg_catalog.pg_class c\n" +
      "               join (select a.attrelid, a.attnum, a.attname, a.atttypid,\n" +
      "                            (aclexplode(a.attacl)).grantor as grantor, \n" +
      "                            (aclexplode(a.attacl)).grantee as grantee, \n" +
      "                            (aclexplode(a.attacl)).privilege_type as privilege_type, \n" +
      "                            (aclexplode(a.attacl)).is_grantable as is_grantable\n" +
      "                       from pg_attribute a\n" +
      "                      where a.attnum > 0\n" +
      "                        and not a.attisdropped) a on c.oid = a.attrelid\n" +
      "               join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               join pg_catalog.pg_type ty on ty.oid = a.atttypid\n" +
      "         where n.nspname = :SCHEMA_NAME\n" +
      "           and c.relname = :OBJECT_NAME) g\n" +
      "%s" +
      " order by schema_name, object_name, column_no, grantee, privilege",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getUsingList(String filter) {
    return String.format(
      "select schema_name, object_name, constraint_name, columns, r_schema_name, r_object_name, r_constraint_name, r_columns\n" +
      "  from (select n.nspname schema_name, c.relname object_name, con.conname constraint_name,\n" +
      "               array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select con.conkey[k] col from pg_catalog.generate_subscripts(con.conkey, 1) k) k on k.col = a.attnum and c.oid = a.attrelid), ',') columns,\n" +
      "               fn.nspname r_schema_name, fc.relname r_object_name, fcon.conname r_constraint_name,\n" +
      "               array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select con.confkey[k] col from pg_catalog.generate_subscripts(con.confkey, 1) k) k on k.col = a.attnum and fc.oid = a.attrelid), ',') r_columns\n" +
      "          from pg_catalog.pg_constraint con\n" +
      "               join pg_catalog.pg_class c on c.oid = con.conrelid\n" +
      "               join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               join pg_catalog.pg_class fc on fc.oid = con.confrelid\n" +
      "               join pg_catalog.pg_namespace fn on fn.oid = fc.relnamespace\n" +
      "               left join pg_catalog.pg_constraint fcon on fcon.contype = 'p' and fcon.conrelid = fc.oid\n" +
      "         where con.contype = 'f') c\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and object_name = :OBJECT_NAME\n" +
      "%s" +
      " order by schema_name, object_name, constraint_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getUsedByList(String filter) {
    return String.format(
      "select schema_name, object_name, constraint_name, columns, r_schema_name, r_object_name, r_constraint_name, r_columns\n" +
      "  from (select n.nspname r_schema_name, c.relname r_object_name, con.conname r_constraint_name,\n" +
      "               array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select con.conkey[k] col from pg_catalog.generate_subscripts(con.conkey, 1) k) k on k.col = a.attnum and c.oid = a.attrelid), ',') r_columns,\n" +
      "               fn.nspname schema_name, fc.relname object_name, fcon.conname constraint_name,\n" +
      "               array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select con.confkey[k] col from pg_catalog.generate_subscripts(con.confkey, 1) k) k on k.col = a.attnum and fc.oid = a.attrelid), ',') columns\n" +
      "          from pg_catalog.pg_constraint con\n" +
      "               join pg_catalog.pg_class c on c.oid = con.conrelid\n" +
      "               join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "               join pg_catalog.pg_class fc on fc.oid = con.confrelid\n" +
      "               join pg_catalog.pg_namespace fn on fn.oid = fc.relnamespace\n" +
      "               left join pg_catalog.pg_constraint fcon on fcon.contype = 'p' and fcon.conrelid = fc.oid\n" +
      "         where con.contype = 'f') c\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and object_name = :OBJECT_NAME\n" +
      "%s" +
      " order by schema_name, object_name, constraint_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  private static String appendObjectSub(String sql, String text) {
    return
      sql +
      (sql.length() > 0 ? "\nunion all\n" : "") +
      text;
  }
  
  protected static String getObjectSubList(EnumSet<ObjectSub> e, String version, boolean source) {
    String sql = "";
    if (e.contains(ObjectSub.Generals)) {
      sql = appendObjectSub(sql, 
        "-- podstawowe objekty\n" +
        "select 'pg_class'::regclass classid, c.oid objid, 0 objsubid,\n" +
        "       tbs.spcname tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) owner_name,\n" +
        "       case c.relpersistence when 'u' then 'UNLOGGED ' when 't' then 'TEMPORARY ' else '' end|| -- od 9.1\n" +
        "       case c.relkind \n" +
        "         when 'r' then 'TABLE'\n" +
        "         when 'i' then 'INDEX'\n" +
        "         when 'm' then 'MATERIALIZED VIEW'\n" +
        "         when 'S' then 'SEQUENCE'\n" +
        "         when 'v' then 'VIEW'\n" +
        "         when 'c' then 'COMPOSITE TYPE'\n" +
        "         when 't' then 'TOAST TABLE'\n" +
        "         when 'f' then 'FOREIGN TABLE'\n" +
        "         else c.relkind::text\n" +
        "       end object_type,\n" +
        "       null::text object_attribute,\n" +
        "       coalesce(am.amname, null)::text object_info,\n" +
        "       n.nspname object_owner,\n" +
        "       c.relname::text object_name\n" +
        (source ?
          "       , case c.relkind \n" +
          "           when 'v' then pg_get_viewdef(c.oid, true)\n" +
          "           else null::text\n" +
          "         end source" : "") +
        "  from pg_catalog.pg_class c\n" +
        "       left join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "       left join pg_catalog.pg_am am on c.relam = am.oid\n" +
        "       left join pg_catalog.pg_tablespace tbs on c.reltablespace = tbs.oid"
      );
    }
    if (e.contains(ObjectSub.Columns)) {
      sql = appendObjectSub(sql, 
        "-- kolumny i atrybuty obiektów\n" +
        "select 'pg_class'::regclass classid, c.oid objid, a.attnum objsubid,\n" +
        "       tbs.spcname tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) owner_name,\n" +
        "       case c.relkind\n" +
        "         when 'r' then 'TABLE COLUMN'\n" +
        "         when 'i' then 'INDEX COLUMN'\n" +
        "         when 'm' then 'MATERIALIZED VIEW COLUMN'\n" +
        "         when 'S' then 'SEQUENCE ATTRIBUTE'\n" +
        "         when 'v' then 'VIEW COLUMN'\n" +
        "         when 'c' then 'COMPOSITE TYPE ATTRIBUTE'\n" +
        "         when 't' then 'TOAST TABLE COLUMN'\n" +
        "         when 'f' then 'FOREIGN TABLE COLUMN'\n" +
        "         else c.relkind::text\n" +
        "       end object_type,\n" +
        "       pg_catalog.format_type(ty.oid, a.atttypmod)||coalesce(' defult '||pg_catalog.pg_get_expr(def.adbin, def.adrelid), '') object_attribute,\n" +
        "       null::text object_info,\n" +
        "       c.relname object_owner,\n" +
        "       a.attname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_class c\n" +
        "       join pg_catalog.pg_attribute a on c.oid = a.attrelid\n" +
        "       join pg_catalog.pg_type ty on ty.oid = a.atttypid\n" +
        "       left join pg_catalog.pg_attrdef def on adrelid = a.attrelid and adnum = a.attnum\n" +
        "       left join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "       left join pg_catalog.pg_tablespace tbs on c.reltablespace = tbs.oid"
      );
    }
    if (e.contains(ObjectSub.Schemas)) {
      sql = appendObjectSub(sql, 
        "select 'pg_namespace'::regclass classid, n.oid objid, 0 objsubid, null::name tablespace_name, null::name schema_name, pg_catalog.pg_get_userbyid(n.nspowner) owner_name,\n" +
        "       'SCHEMA' object_type, null::name object_attribute, null::text object_info, pg_catalog.pg_get_userbyid(n.nspowner) object_owner, n.nspname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_namespace n"
      );
    }
    if (e.contains(ObjectSub.Tablespaces)) {
      sql = appendObjectSub(sql,
        "-- przestrzenie danych (not in pg_depend)\n" +
        "select 'pg_tablespace'::regclass classid, t.oid objid, 0 objsubid, null::name tablespace_name, null::name schema_name, pg_catalog.pg_get_userbyid(t.spcowner) owner_name,\n" +
        "       'TABLE SPACE' object_type, null::name object_attribute, \n" +
        "       " +(new VersionID(version).compare(9, 2) < 0 ? "t.spclocation::text" : "pg_tablespace_location(t.oid)::text") +" object_info, -- w wersji 9.2+ nie ma spclocation jest pg_tablespace_location(t.oid)\n" +
        "       null::name object_owner, t.spcname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_tablespace t"
      );
    }
    if (e.contains(ObjectSub.Functions)) {
      sql = appendObjectSub(sql,
        "-- funkcje i funkcje wyzwalaczy\n" +
        "select 'pg_proc'::regclass classid, f.oid objid, 0 subobjid, null::name tablespace_name, n.nspname schema_name, pg_get_userbyid(f.proowner) owner_name,\n" +
        "       case when t.oid is not null then 'TRIGGER ' else case when f.proisagg then 'AGGREGATE ' else '' end end||'FUNCTION' object_type,\n" +
        "       pg_get_function_arguments(f.oid) object_attribute, l.lanname::text object_info, n.nspname object_owner, (f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')')::text obejct_name\n" +
        (source ? "       , case when f.proisagg then null::text else pg_get_functiondef(f.oid) end source\n" : "") +
        "  from pg_catalog.pg_proc f\n" +
        "       join pg_catalog.pg_language l on f.prolang = l.oid\n" +
        "       left join pg_catalog.pg_namespace n on f.pronamespace = n.oid\n" +
        "       left join pg_catalog.pg_trigger t on t.tgfoid = f.oid"
      );
    }
    if (e.contains(ObjectSub.Languages)) {
      sql = appendObjectSub(sql, 
        "-- jêzyki programowania\n" +
        "select 'pg_language'::regclass classid, l.oid objid, 0 objsubid, null::name tablespace_name, null::name schema_name, pg_catalog.pg_get_userbyid(l.lanowner) owner_name,\n" +
        "       'LANGUAGE' object_type, null::name object_attribute, null::text object_info, null::name object_owner, l.lanname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_language l"
      );
    }
    if (e.contains(ObjectSub.Triggers)) {
      sql = appendObjectSub(sql, 
        "-- wyzwalacze\n" +
        "select 'pg_trigger'::regclass classid, t.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) owner_name,\n" +
        "       'TRIGGER' object_type, f.proname object_attribute, \n" +
        "       (case ((t.tgtype)::integer & 66) when 2 then 'BEFORE'::text when 64 then 'INSTEAD OF'::text else 'AFTER'::text end||' '||\n" +
        "        array_to_string(array(select em.text from (values (4,'INSERT'::text), (8,'DELETE'::text), (16,'UPDATE'::text)) em(num, text) where (t.tgtype::integer & em.num) <> 0), ' '))::text object_info,\n" +
        "       c.relname object_owner, t.tgname::text object_name\n" +
        (source ? "       , pg_get_triggerdef(t.oid, true) source\n" : "") +
        "  from pg_catalog.pg_trigger t\n" +
        "       join pg_catalog.pg_class c on c.oid = t.tgrelid\n" +
        "         left join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "       left join pg_catalog.pg_proc f on f.oid = t.tgfoid" +
        (source? "\n where not t.tgisinternal" : "")
      );
    }
    if (e.contains(ObjectSub.Types)) {
      sql = appendObjectSub(sql, 
        "-- typy w tym domeny\n" +
        "select 'pg_type'::regclass classid, t.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(t.typowner) owner_name,\n" +
        "       case t.typtype \n" +
        "         when 'b' then 'BASE TYPE'\n" +
        "         when 'c' then 'COMPOSITE TYPE'\n" +
        "         when 'd' then 'DOMAIN'\n" +
        "         when 'e' then 'ENUM TYPE'\n" +
        "         when 'r' then 'RANGE TYPE'\n" +
        "         when 'p' then 'PSEUDO TYPE'\n" +
        "         else t.typtype::text\n" +
        "       end object_type, t.typdefault object_attribute, \n" +
        "       case t.typcategory\n" +
        "         when 'A' then 'ARRAY'\n" +
        "         when 'B' then 'BOOLEAN'\n" +
        "         when 'C' then null\n" +
        "         when 'D' then 'DATE/TIME'\n" +
        "         when 'E' then null\n" +
        "         when 'G' then 'GEOMETRIC'\n" +
        "         when 'I' then 'NETWORK ADDRESS'\n" +
        "         when 'N' then 'NUMERIC'\n" +
        "         when 'P' then null\n" +
        "         when 'S' then 'STRING'\n" +
        "         when 'T' then 'TIMESPAN'\n" +
        "         when 'U' then 'USER-DEFINED'\n" +
        "         when 'V' then 'BIT-STRING'\n" +
        "         when 'X' then 'UNKNOWN'\n" +
        "         else 'UNDEFINED - '||t.typtype::text\n" +
        "       end::text object_info, \n" +
        "       coalesce(c.relname, n.nspname) object_owner, (pg_catalog.format_type(t.oid, null))::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_type t\n" +
        "       left join pg_catalog.pg_class c on c.oid = t.typrelid\n" +
        "       left join pg_catalog.pg_namespace n on t.typnamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.Databases)) {
      sql = appendObjectSub(sql, 
        "-- bazy danych (not in pg_depend)\n" +
        "select 'pg_database'::regclass classid, d.oid objid, 0 objsubid, null::name tablespace_name, null::name schema_name, null::name owner_name,\n" +
        "       'DATABASE' object_type, null::name object_attribute, null::text object_info, null::name object_owner, d.datname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_database d"
      );
    }
    if (e.contains(ObjectSub.Conversions)) {
      sql = appendObjectSub(sql, 
        "-- konwersje znaków\n" +
        "select 'pg_conversion'::regclass classid, c.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.conowner) owner_name,\n" +
        "       'CONVERSION' object_type, null::name object_attribute, c.conproc::text object_info, n.nspname object_owner, c.conname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_conversion c\n" +
        "       left join pg_catalog.pg_namespace n on c.connamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.Operators)) {
      sql = appendObjectSub(sql, 
        "-- operatory\n" +
        "select 'pg_operator'::regclass classid, o.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(o.oprowner) owner_name,\n" +
        "       'OPERATOR' object_type, null::name object_attribute, \n" +
        "       (case when o.oprleft = 0 then '' else pg_catalog.format_type(o.oprleft, null)||' ' end||o.oprname||' '||pg_catalog.format_type(o.oprright, null)||' = '||pg_catalog.format_type(o.oprresult, null))::text object_info,\n" +
        "       n.nspname object_owner, o.oprname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_operator o\n" +
        "       left join pg_catalog.pg_namespace n on o.oprnamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.Collations)) {
      sql = appendObjectSub(sql, 
        "-- porównanie\n" +
        "select 'pg_collation'::regclass classid, c.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.collowner) owner_name,\n" +
        "       'COLLATION' object_type, null::name object_attribute, null::text object_info, n.nspname object_owner, c.collname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_collation c\n" +
        "       left join pg_catalog.pg_namespace n on c.collnamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.Extensions) && new VersionID(version).compare(9, 1) >= 0) {
      sql = appendObjectSub(sql, 
        "-- rozszerzenia (od 9.1)\n" +
        "select 'pg_extension'::regclass classid, e.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(e.extowner) owner_name,\n" +
        "       'EXTENSION' object_type, null::name object_attribute, null::text object_info, n.nspname object_owner, e.extname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_extension e\n" +
        "       left join pg_catalog.pg_namespace n on e.extnamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.Rules)) {
      sql = appendObjectSub(sql, 
        "-- zasady\n" +
        "select 'pg_rewrite'::regclass classid, r.oid objid, 0 objsubid, tbs.spcname tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) owner_name,\n" +
        "       'RULE' object_type, (case r.ev_enabled when 'O' then 'ENABLED' when 'D' then 'DISABLED' when 'R' then 'REPLICA' when 'A' then 'ALWAYS' end) object_attribute, \n" +
        "       (case r.ev_type when 1::char then 'SELECT' when 2::char then 'UPDATE' when 3::char then 'INSERT' when 4::char then 'DELETE' end)::text object_info, \n" +
        "       c.relname object_owner, r.rulename::text object_name\n" +
        (source ? "       , pg_get_ruledef(r.oid, true) source\n" : "") +
        "  from pg_catalog.pg_rewrite r\n" +
        "       join pg_catalog.pg_class c on c.oid = r.ev_class\n" +
        "       left join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "       left join pg_catalog.pg_tablespace tbs on c.reltablespace = tbs.oid" +
        (source ? "\n where r.rulename != '_RETURN'" : "")
      );
    }
    if (e.contains(ObjectSub.OpClasses)) {
      sql = appendObjectSub(sql, 
        "-- klasa operatora\n" +
        "select 'pg_opclass'::regclass classid, o.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(o.opcowner) owner_name,\n" +
        "       'OPERATOR CLASS' object_type, null::name object_attribute, 'family '||f.opfname::text object_info, n.nspname object_owner, o.opcname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_opclass o\n" +
        "       join pg_catalog.pg_opfamily f on f.oid = o.opcfamily\n" +
        "       left join pg_catalog.pg_namespace n on o.opcnamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.OpFamilies)) {
      sql = appendObjectSub(sql, 
        "-- rodzina operatorów\n" +
        "select 'pg_opfamily'::regclass classid, o.oid objid, 0 objsubid, null::name tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(o.opfowner) owner_name,\n" +
        "       'OPERATOR FAMILY' object_type, null::name object_attribute, null::text object_info, n.nspname object_owner, o.opfname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_opfamily o\n" +
        "       left join pg_catalog.pg_namespace n on o.opfnamespace = n.oid"
      );
    }
    if (e.contains(ObjectSub.Casts)) {
      sql = appendObjectSub(sql, 
        "-- konwersje typów\n" +
        "select 'pg_cast'::regclass classid, c.oid objid, 0 objsubid, null::name tablespace_name, null::name schema_name, null::name owner_name,\n" +
        "       'CAST' object_type, null::name object_attribute, null::text object_info, null::name object_owner, \n" +
        "       (pg_catalog.format_type(c.castsource, null)||' :: '||pg_catalog.format_type(c.casttarget, null))::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_cast c"
      );
    }
    if (e.contains(ObjectSub.Roles)) {
      sql = appendObjectSub(sql, 
        "-- role i u¿ytkownicy (not in pg_depend)\n" +
        "select 'pg_authid'::regclass classid, r.oid objid, 0 objsubid, null::name tablespace_name, null::text schema_name, null::name owner_name,\n" +
        "       case when r.rolcanlogin then 'USER' else 'ROLE' end::text object_type, case when r.rolsuper then 'SUPER USER' end::text object_attribute, null::text object_info, null::name object_owner, r.rolname::text object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_roles r"
      );
    }
    if (e.contains(ObjectSub.Constraints)) {
      sql = appendObjectSub(sql, 
        "-- ograniczenia\n" +
        "select 'pg_constraint'::regclass classid, cn.oid objid, 0 objsubid, tbs.spcname tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) owner_name,\n" +
        "       'CONSTRAINT' object_type, \n" +
        "       case cn.contype when 'c' then 'CHECK'::text when 'p' then 'PRIMARY KEY'::text when 'f' then 'FOREIGN KEY'::text when 'u' then 'UNIQUE'::text when 't' then 'TRIGGER'::text when 'x' then 'EXCLUDE'::text else cn.contype::text end::text object_attribute, \n" +
        "       null::text object_info, c.relname object_owner, cn.conname::text object_name\n" +
        (source ? "       , pg_get_constraintdef(cn.oid, true) source\n" : "") +
        "  from pg_catalog.pg_constraint cn\n" +
        "       join pg_class c on c.oid = cn.conrelid\n" +
        "       left join pg_catalog.pg_namespace n on cn.connamespace = n.oid\n" +
        "       left join pg_catalog.pg_tablespace tbs on c.reltablespace = tbs.oid"
      );
    }
    if (e.contains(ObjectSub.DefaultValues)) {
      sql = appendObjectSub(sql, 
        "-- wartoœci domyœlne\n" +
        "select 'pg_attrdef'::regclass classid, def.oid objid, 0 objsubid, tbs.spcname tablespace_name, n.nspname schema_name, pg_catalog.pg_get_userbyid(c.relowner) owner_name,\n" +
        "       'DEFAULT VALUE' object_type, null::text object_attribute, null::text object_info, c.relname::text object_owner, def.adsrc object_name\n" +
        (source ? "       , null::text source\n" : "") +
        "  from pg_catalog.pg_attrdef def\n" +
        "       join pg_class c on c.oid = def.adrelid\n" +
        "       left join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "       left join pg_catalog.pg_tablespace tbs on c.reltablespace = tbs.oid"
      );
    }
    return sql;
  }
  
  public static String getSearchObjectList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubObjects, version, false) +
      ")\n" +
      "select o.*, d.description\n" +
      "  from o left join pg_catalog.pg_description d on d.classoid = o.classid and d.objoid = o.objid and d.objsubid = o.objsubid\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +
      "   and lower(object_name) like '%%'||lower(coalesce(:search_text, ''))||'%%'\n" +
      "%s" +
      " order by schema_name, object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
    );
  }
  
  public static String getDependClassList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubDepends, version, false) +
        ")\n" +
        "select distinct *\n" +
        "  from (select o.object_type, o.owner_name, o.object_owner, o.object_name, \n" +
        "               ro.object_type r_object_type, ro.object_name r_object_name, ro.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_class c on c.oid = d.refobjid and c.tableoid = d.refclassid\n" +
        "               left outer join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and c.relname = :OBJECT_NAME\n" +
        "        union all\n" +
        "        select ro.object_type, ro.owner_name, ro.object_owner, ro.object_name, \n" +
        "               o.object_type r_object_type, o.object_name r_object_name, o.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_class c on c.oid = d.objid and c.tableoid = d.classid\n" +
        "               left outer join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and c.relname = :OBJECT_NAME) d\n" +
        "%s" +
        " order by object_name, object_type, object_owner",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  public static String getFunctionList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select n.nspname schema_name, pg_get_userbyid(f.proowner) owner_name, l.lanname lang_name, \n" +
      "               f.proname function_name, pg_get_function_arguments(f.oid) arguments,\n" +
      "               f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' full_function_name,\n" +
      "               quote_ident(n.nspname)||'.'||quote_ident(f.proname)||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' full_object_name,\n" +
      "               case when f.proisagg then 'Y' else 'N' end isagg, case when f.proiswindow then 'Y' else 'N' end iswindow,\n" +
      "               case when f.proisstrict then 'Y' else 'N' end isstrict, case when f.proretset then 'Y' else 'N' end isretset,\n" +
      "               case f.provolatile when 'i' then 'IMMUTABLE' when 's' then 'STABLE' when 'v' then 'VOLATILE' else f.provolatile::text end volatile_type,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(f.proowner, 'USAGE') then 'USAGE'\n" +
      "                 when pg_catalog.has_function_privilege('public', f.oid, 'EXECUTE') then 'PUBLIC'\n" +
      "                 when pg_catalog.has_function_privilege(f.oid, 'EXECUTE') then 'GRANTED'\n" +
      "               else 'NO' end accessible,\n" +
      "               description\n" +
      "          from pg_catalog.pg_proc f\n" +
      "               join pg_catalog.pg_language l on f.prolang = l.oid\n" +
      "               left join pg_catalog.pg_namespace n on f.pronamespace = n.oid\n" +
      "               left join pg_catalog.pg_type t on f.prorettype = t.oid\n" +
      "               left join pg_catalog.pg_description d on d.classoid = f.tableoid and d.objoid = f.oid and d.objsubid = 0\n" +
      "         where (t.typname = 'trigger' and :TRIGGER = 'Y') or (t.typname <> 'trigger' and :TRIGGER = 'N') and not f.proisagg) f\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +
      "%s" +
      " order by schema_name, function_name, arguments",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getFunctionArgumentList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select n.nspname schema_name, f.proname function_name, n argument_no, f.proargnames[n] argument_name, pg_catalog.format_type(f.proargtypes[n -1], -1) argument_type,\n" +
      "               case f.proargmodes[n] when 'o' then 'out' when 'b' then 'in/out' else 'in' end argument_mode,\n" +
      "               trim((regexp_split_to_array(pg_get_expr(f.proargdefaults, 0), '[\\t,](?=(?:[^\\'']|\\''[^\\'']*\\'')*$)'))[case when f.pronargs -n > f.pronargdefaults then null else f.pronargdefaults -(f.pronargs -n +1) +1 end]) default_value\n" +
      "          from (select f.oid, pg_catalog.generate_series(1, f.pronargs::int) n, f.*\n" +
      "                  from pg_catalog.pg_proc f\n" +
      "                 where f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' = :FUNCTION_NAME) f\n" +
      "               join pg_catalog.pg_namespace n on n.oid = f.pronamespace\n" +
      "         where n.nspname = :SCHEMA_NAME\n" +
      "        union all\n" +
      "        select n.nspname schema_name, f.proname function_name, 0 argument_no, null::text argument_name, format_type(f.prorettype, -1) argument_type, 'return' argument_mode, null::text default_value\n" +
      "          from pg_catalog.pg_proc f\n" +
      "               join pg_catalog.pg_namespace n on n.oid = f.pronamespace\n" +
      "         where f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' = :FUNCTION_NAME\n" +
      "           and n.nspname = :SCHEMA_NAME) f\n" +
      "%s" +
      " order by argument_no",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  private static String getFunctionPrivilegeListCore(String filter, String aColumns) {
    return String.format(
      "select " +aColumns +"\n" +
      "  from (select oid, schema_name, object_name, \n" +
      "               case when grantor = 0 then 'public' else pg_catalog.pg_get_userbyid(grantor) end grantor,\n" +
      "               case when grantee = 0 then 'public' else pg_catalog.pg_get_userbyid(grantee) end grantee,\n" +
      "               privilege_type privilege, is_grantable\n" +
      "          from (select f.oid, n.nspname schema_name, f.proname object_name,\n" +
      "                       (aclexplode(f.proacl)).grantor as grantor,\n" +
      "                       (aclexplode(f.proacl)).grantee as grantee,\n" +
      "                       (aclexplode(f.proacl)).privilege_type as privilege_type,\n" +
      "                       (aclexplode(f.proacl)).is_grantable as is_grantable\n" +
      "                  from pg_catalog.pg_proc f\n" +
      "                       left join pg_catalog.pg_namespace n on n.oid = f.pronamespace\n" +
      "                 where n.nspname = :SCHEMA_NAME\n" +
      "                   and f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' = :OBJECT_NAME) g) g\n" +
      "%s" +      
      " order by schema_name, object_name, grantee, privilege",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  public static String getFunctionPrivilegeList(String filter) {
    return getFunctionPrivilegeListCore(filter, "schema_name, object_name, grantor, grantee, privilege, is_grantable");
  }
  
  public static String getFunctionPrivilegeCommandList(String filter) {
    return getFunctionPrivilegeListCore(filter, "'GRANT '||privilege||' ON FUNCTION '||quote_ident(schema_name)||'.'||quote_ident(object_name)||'('||coalesce(pg_get_function_identity_arguments(oid), '')||')'||' TO '||grantee||case when is_grantable then ' WITH GRANT OPTION' else '' end command");
  }
  
  public static String getDependFunctionList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubDepends, version, false) +
        ")\n" +
        "select distinct *\n" +
        "  from (select o.object_type, o.owner_name, o.object_owner, o.object_name, \n" +
        "               ro.object_type r_object_type, ro.object_name r_object_name, ro.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_proc f on f.oid = d.refobjid and f.tableoid = d.refclassid\n" +
        "               left outer join pg_catalog.pg_namespace n on f.pronamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' = :OBJECT_NAME\n" +
        "        union all\n" +
        "        select ro.object_type, ro.owner_name, ro.object_owner, ro.object_name, \n" +
        "               o.object_type r_object_type, o.object_name r_object_name, o.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_proc f on f.oid = d.objid and f.tableoid = d.classid\n" +
        "               left outer join pg_catalog.pg_namespace n on f.pronamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' = :OBJECT_NAME) d\n" +
        "%s" +
        " order by object_name, object_type, object_owner",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  public static String getDependTriggerList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubDepends, version, false) +
        ")\n" +
        "select distinct *\n" +
        "  from (select o.object_type, o.owner_name, o.object_owner, o.object_name, \n" +
        "               ro.object_type r_object_type, ro.object_name r_object_name, ro.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_trigger t on t.oid = d.refobjid and t.tableoid = d.refclassid\n" +
        "               join pg_class c on t.tgrelid = c.oid\n" +
        "               left outer join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and c.relname = :TABLE_NAME\n" +
        "           and t.tgname = :OBJECT_NAME\n" +
        "        union all\n" +
        "        select ro.object_type, ro.owner_name, ro.object_owner, ro.object_name, \n" +
        "               o.object_type r_object_type, o.object_name r_object_name, o.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_trigger t on t.oid = d.objid and t.tableoid = d.classid\n" +
        "               join pg_class c on t.tgrelid = c.oid\n" +
        "               left outer join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and c.relname = :TABLE_NAME\n" +
        "           and t.tgname = :OBJECT_NAME) d\n" +
        "%s" +
        " order by object_name, object_type, object_owner",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  public static String getFunctionDetail() {
    return
      "select f.oid, f.*, s.*, n.*\n" +
      "  from pg_catalog.pg_proc f\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = f.pronamespace\n" +
      "       left join pg_stat_user_functions s on s.funcid = f.oid\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and f.proname||'('||coalesce(pg_get_function_identity_arguments(f.oid), '')||')' = :FUNCTION_NAME";
  }

  public static String getFunctionTriggerFunctionList(String filter) {
    return String.format(
      "select schema_name, trigger_name, enable_mode, action_timing||' '||action_orientation action_timing, trigger_event, columns, object_name, action_condition, full_function_name, action_orientation, description\n" +
      "  from (select ns.nspname schema_name, t.tgname trigger_name, case t.tgenabled when 'O' then 'ENABLED' when 'D' then 'DISABLED' when 'R' then 'REPLICA' when 'A' then 'ALWAYS' end enable_mode,\n" +
      "               case ((t.tgtype)::integer & 66) when 2 then 'BEFORE'::text when 64 then 'INSTEAD OF'::text else 'AFTER'::text end action_timing,\n" +
      "               array_to_string(array(select em.text from (values (4,'INSERT'::text), (8,'DELETE'::text), (16,'UPDATE'::text)) em(num, text) where (t.tgtype::integer & em.num) <> 0), ' ') trigger_event,\n" +
      "               c.relname object_name, array_to_string(array(select a.attname from pg_catalog.pg_attribute a join (select t.tgattr[k] col from pg_catalog.generate_subscripts(t.tgattr, 1) k) k on k.col = a.attnum and c.oid = a.attrelid), ', ') columns,\n" +
      "               case when pg_has_role(c.relowner, 'USAGE'::text) then (select rm.m[1] as m from regexp_matches(pg_get_triggerdef(t.oid), '.{35,} WHEN \\((.+)\\) EXECUTE PROCEDURE'::text) rm(m) limit 1) else null::text end action_condition,\n" +
      "               p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' full_function_name,\n" +
      "               case (t.tgtype::integer & 1) when 1 then 'ROW'::text else 'STATEMENT'::text end action_orientation, d.description\n" +
      "          from pg_catalog.pg_namespace ns\n" +
      "               join pg_catalog.pg_class c on ns.oid = c.relnamespace\n" +
      "               join pg_catalog.pg_trigger t on c.oid = t.tgrelid\n" +
      "               join pg_catalog.pg_proc p on p.oid = t.tgfoid\n" +
      "               left join pg_catalog.pg_description d on d.objoid = t.oid\n" +
      "         where not t.tgisinternal\n" +
      "           and not pg_is_other_temp_schema(ns.oid)) t\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and full_function_name = :FUNCTION_NAME\n" +
      "%s" +
      " order by trigger_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
  }
  
  public static String getSequenceList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select n.nspname schema_name, pg_get_userbyid(c.relowner) owner_name, c.relname sequence_name, \n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(c.relowner, 'USAGE') then 'USAGE'\n" +
      "                 when pg_catalog.has_sequence_privilege('public', c.oid, 'SELECT') then 'PUBLIC'\n" +
      "                 when pg_catalog.has_sequence_privilege(c.oid, 'SELECT') then 'GRANTED'\n" +
      "               else 'NO' end accessible,\n" +
      "               d.description\n" +
      "          from pg_class c\n" +
      "               join pg_namespace n on n.oid = c.relnamespace\n" +
      "               left join pg_statio_all_sequences s on s.relid = c.oid\n" +
      "               left join pg_description d on d.classoid = c.tableoid and d.objoid = c.oid and d.objsubid = 0\n" +
      "         where c.relkind = 'S') s\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +
      "%s" +
      " order by schema_name, sequence_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
      );
  }
  
  public static String getSequenceDetails() {
    return
      "select c.oid, s.*, a.*, c.*\n" +
      "  from &SCHEMA_NAME.&SEQUENCE_NAME s,\n" +
      "       pg_class c\n" +
      "       join pg_namespace n on n.oid = c.relnamespace\n" +
      "       left join pg_statio_all_sequences a on relid = c.oid\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and c.relname = :SEQUENCE_NAME";
  }
  
  public static String getTablespaceList(String filter, String version) {
    return String.format(
      "select *\n" +
      "  from (select tablespace_name, owner_name, tablespace_location, \n" +
      "               size_b::numeric, (size_b /1024 /1024)::numeric size_mb, round(size_b /case when sum(size_b) over () = 0 then 1 else sum(size_b) over () end *100, 2) prc_size,\n" +
      "               database_list, accessible, description\n" +
      "          from (select t.spcname tablespace_name, pg_catalog.pg_get_userbyid(t.spcowner) owner_name,\n" +
      "                       case when has_tablespace_privilege(t.spcname, 'CREATE') then pg_tablespace_size(t.oid) else null::int end size_b,\n" +
      "                       " +(new VersionID(version).compare(9, 2) < 0 ? "t.spclocation::text" : "pg_tablespace_location(t.oid)::text") +" tablespace_location,\n" +
      "                       array_to_string(array(select d.datname from pg_database d where d.oid in (select * from pg_tablespace_databases(t.oid) where t.spcname <> 'pg_global') order by datname), ', ') database_list,\n" +
      "                       case\n" +
      "                         when pg_catalog.pg_has_role(t.spcowner, 'USAGE') then 'USAGE'\n" +
      "                         when pg_catalog.has_tablespace_privilege('public', t.oid, 'CREATE') then 'PUBLIC'\n" +
      "                         when pg_catalog.has_tablespace_privilege(t.oid, 'CREATE') then 'GRANTED'\n" +
      "                       else 'NO' end accessible,\n" +
      "                       d.description\n" +
      "                  from pg_catalog.pg_tablespace t\n" +
      "                       left join pg_catalog.pg_description d on d.classoid = t.tableoid and d.objoid = t.oid and d.objsubid = 0) t) t\n" +
      "%s" +      
      " order by tablespace_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
      );
  }
  
  public static String getSequenceAccessible() {
    return
      "select accessible\n" +
      "  from (select n.nspname as schema_name, c.relname as table_name,\n" +
      "               case\n" +
      "                 when pg_catalog.pg_has_role(c.relowner, 'USAGE') then 'USAGE'\n" +
      "                 when pg_catalog.has_table_privilege(c.oid, 'SELECT') then 'GRANTED' \n" +
      "               else 'NO' end accessible\n" +
      "          from pg_catalog.pg_class c\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = c.relnamespace\n" +
      "         where c.relkind in ('S')) t\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and table_name = :SEQUENCE_NAME";
  }
  
  public static String getSchemaList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select schema_name, owner_name, size_b, round(size_b /1024 /1024, 2) size_mb,\n" +
      "               round(size_b /case when sum(size_b) over () = 0 then 1 else sum(size_b) over () end *100, 2) prc_size,\n" +
      "               table_count, object_count, description, accessible\n" +
      "          from (select n.nspname schema_name, pg_get_userbyid(n.nspowner) owner_name, \n" +
      "                       (select sum(pg_total_relation_size(c.oid)) from pg_class c where c.relnamespace = n.oid and c.relkind in ('r')) size_b,\n" +
      "                       (select count(0) from pg_class c where c.relnamespace = n.oid and c.relkind = 'r') table_count,\n" +
      "                       ((select count(0) from pg_class c where c.relnamespace = n.oid and c.relkind in ('r', 'v', 'S')) +\n" +
      "                        (select count(0) from pg_proc f where f.pronamespace = n.oid) +\n" +
      "                        (select count(0) from pg_trigger t join pg_class c on c.oid = t.tgrelid where c.relkind = 'r' and c.relnamespace = n.oid and not t.tgisinternal) +\n" +
      "                        (select count(0) from pg_type t where t.typnamespace = n.oid and t.typtype in ('b', 'd', 'e')))::numeric object_count,\n" +
      "                       d.description,\n" +
      "                       case\n" +
      "                         when pg_catalog.pg_has_role(n.nspowner, 'USAGE') then 'USAGE'\n" +
      "                         when pg_catalog.has_schema_privilege('public', n.oid, 'CREATE') then 'PUBLIC'\n" +
      "                         when pg_catalog.has_schema_privilege(n.oid, 'CREATE') then 'GRANTED'\n" +
      "                         when pg_catalog.has_schema_privilege(n.oid, 'USAGE') then 'BROWSE'\n" +
      "                       else 'NO' end accessible\n" +
      "                  from pg_namespace n\n" +
      "                       left join pg_description d on d.classoid = n.tableoid and d.objoid = n.oid and d.objsubid = 0) n) n\n" +
      "%s" +
      " order by schema_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
      );
  }
  
  public static String getRoleList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select owner_name, super_user, is_user, size_b, round(size_b /1024 /1024, 2) size_mb,\n" +
      "               round(size_b /case when sum(size_b) over () = 0 then 1 else sum(size_b) over () end *100, 2) prc_size,\n" +
      "               table_count, object_count, description\n" +
      "          from (select r.rolname owner_name, r.rolsuper super_user, r.rolcanlogin is_user,\n" +
      "                       (select sum(pg_total_relation_size(c.oid)) from pg_class c where c.relowner = r.oid and c.relkind in ('r')) size_b,\n" +
      "                       (select count(0) from pg_class c where c.relowner = r.oid and c.relkind = 'r') table_count,\n" +
      "                       ((select count(0) from pg_class c where c.relowner = r.oid and c.relkind in ('r', 'v', 'S')) +\n" +
      "                        (select count(0) from pg_proc f where f.proowner = r.oid) +\n" +
      "                        (select count(0) from pg_trigger t join pg_class c on c.oid = t.tgrelid where c.relkind = 'r' and c.relowner = r.oid and not t.tgisinternal) +\n" +
      "                        (select count(0) from pg_type t where t.typowner = r.oid and t.typtype in ('b', 'd', 'e')))::numeric object_count,\n" +
      "                       d.description\n" +
      "                  from pg_roles r\n" +
      "                       left join pg_shdescription d on d.objoid = r.oid and d.classoid = 'pg_authid'::regclass) r) r\n" +
      "%s" +
      " order by owner_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
      );
  }
  
  public static String getSearchSourceList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubSourcable, version, true) +
        ")\n" +
        "select tablespace_name, schema_name, owner_name, object_type, object_owner, object_name, line,\n" +
        "       '<html>'||\n" +
        "         replace(source_line, \n" +
        "           substr(source_line, strpos(upper(source_line), upper(:search_text)), length(:search_text)), \n" +
        "           '<b>'||substr(source_line, strpos(upper(source_line), upper(:search_text)), length(:search_text))||'</b>'\n" +
        "           ) source_line\n" +
        "  from (select tablespace_name, schema_name, owner_name, object_type, object_owner, object_name, line, source[line] source_line\n" +
        "          from (select tablespace_name, schema_name, owner_name, object_type, object_owner, object_name, \n" +
        "                       generate_series(1, array_length(string_to_array(source, E'\\n'), 1)) line,\n" +
        "                       string_to_array(source, E'\\n') source\n" +
        "                  from o\n" +
        "                 where source is not null\n" +
        "                   and (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))) s) s\n" +
        " where source_line ilike '%%'||:search_text||'%%'\n" +
        "%s" +
        " order by schema_name, object_name, line",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
    );
  }
  
  public static String getTriggerDetail() {
    return
      "select t.oid, t.*, c.*\n" +
      "  from pg_trigger t\n" +
      "       join pg_class c on t.tgrelid = c.oid\n" +
      "       left outer join pg_catalog.pg_namespace n on c.relnamespace = n.oid\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and c.relname = :TABLE_NAME\n" +
      "   and t.tgname = :TRIGGER_NAME";
  }
  
  public static String getSessionList(String filter, String version) {
    return String.format(
      "select this, pid, database_name, user_name, application_name, client_host, backend_start, /*xact_start, */query_start, waiting, runned, blocked_pid, state, query\n" +
      "  from (select a.pid = pg_backend_pid() as this, a.pid, (select datname from pg_database where oid = a.datid) as database_name, \n" +
      "               a.usename user_name, a.application_name, coalesce(case when a.client_hostname = '' then null else a.client_hostname end, a.client_addr::text)||':'||a.client_port client_host,\n" +
      "               a.backend_start, a.xact_start, query_start, a.waiting,\n" +
      "               state, case when state in ('active') then round(extract(epoch from now() -a.query_start)::numeric, 0) end runned, \n" +
      "               (select string_agg(kl.pid::text, ', ') from pg_catalog.pg_locks bl join pg_catalog.pg_locks kl on kl.transactionid = bl.transactionid and kl.pid != bl.pid where a.pid = bl.pid and not bl.granted) blocked_pid,\n" +
      "               pg_stat_get_backend_activity(svrid) as query\n" +
      "          from (" +(new VersionID(version).compare(9, 1) <= 0 ? 
                      "select datid, procpid pid, usename, application_name, client_addr, client_hostname, client_port, backend_start, xact_start, query_start, waiting,\n" +
      "                       case current_query when '<IDLE>' then 'idle' when '<IDLE> in transaction' then 'idle in transaction' when '<IDLE> in transaction (aborted)' then 'idle in transaction (aborted)' when '<insufficient privilege>' then null else 'active' end state\n" +
      "                  from pg_stat_activity" :
                      "select datid, pid pid, usename, application_name, client_addr, client_hostname, client_port, backend_start, xact_start, query_start, waiting, state from pg_stat_activity") +") a\n" +
      "               join pg_stat_get_backend_idset() svrid on a.pid = pg_stat_get_backend_pid(svrid)) s\n" +
      "%s" +
      " order by runned desc nulls last, query_start desc",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  public static String getSessionLockList(String filter, String version) {
    return String.format(
      "select pid, schema_name, owner_name, object_type, object_name, virtualxid, transaction, mode, lockwait\n" +
      "  from (select pgl.pid,\n" +
      "               pgc.schema_name, pgc.owner_name, pgc.object_type, coalesce(pgc.object_name, pgl.relation::text) as object_name,\n" +
      "               pgl.virtualxid::text, pgl.virtualtransaction::text as transaction, pgl.mode, not pgl.granted lockwait\n" +
      "          from pg_locks pgl left join (\n" +
            getObjectSubList(EnumSet.of(ObjectSub.Generals), version, false) +
            "\n) pgc on pgl.relation=pgc.objid) l\n" +
      " where pid = :pid::integer\n" +
      "%s" +
      " order by object_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
    );
  }
  
  public static String getTypeList(String filter, String version) {
    return String.format(
      "select schema_name, owner_name, type_name, element, type_type, display_type, alias, labels, providers, description, quote_ident(schema_name)||'.'||quote_ident(type_name) full_object_name\n" +
      "  from (select n.nspname schema_name, t.typname type_name, e.typname as element, pg_catalog.format_type(t.oid, null) as alias, pg_catalog.pg_get_userbyid(t.typowner) owner_name, d.description,\n" +
      "               case t.typtype \n" +
      "                 when 'b' then\n" +
      "                   case t.typcategory\n" +
      "                     when 'A' then 'ARRAY'\n" +
      "                     when 'B' then 'BOOLEAN'\n" +
      "                     when 'C' then 'COMPOSITE'\n" +
      "                     when 'D' then 'DATE/TIME'\n" +
      "                     when 'E' then 'ENUM'\n" +
      "                     when 'G' then 'GEOMETRIC'\n" +
      "                     when 'I' then 'NETWORK ADDRESS'\n" +
      "                     when 'N' then 'NUMERIC'\n" +
      "                     when 'P' then 'PSEUDO'\n" +
      "                     when 'S' then 'STRING'\n" +
      "                     when 'T' then 'TIMESPAN'\n" +
      "                     when 'U' then 'USER-DEFINED'\n" +
      "                     when 'V' then 'BIT'\n" +
      "                     when 'X' then 'UNKNOWN'\n" +
      "                     else t.typcategory::text\n" +
      "                   end\n" +
      "                 when 'c' then 'COMPOSITE'\n" +
      "                 when 'e' then 'ENUM'\n" +
      "                 when 'r' then 'RANGE'\n" +
      "                 when 'p' then 'PSEUDO'\n" +
      "                 when 'd' then 'DOMAIN'\n" +
      "                 else t.typtype::text\n" +
      "               end display_type,\n" +
      "               case t.typtype \n" +
      "                 when 'b' then 'BASE'\n" +
      "                 when 'c' then 'COMPOSITE'\n" +
      "                 when 'e' then 'ENUM'\n" +
      "                 when 'r' then 'RANGE'\n" +
      "                 when 'p' then 'PSEUDO'\n" +
      "                 when 'd' then 'DOMAIN'\n" +
      "                 else t.typtype::text\n" +
      "               end type_type,\n" +
      "               (select array_agg(label) from pg_catalog.pg_seclabels sl1 where sl1.objoid=t.oid) as labels, \n" +
      "               (select array_agg(provider) from pg_catalog.pg_seclabels sl2 where sl2.objoid=t.oid) as providers\n" +
      "          from pg_catalog.pg_type t\n" +
      "               left join pg_catalog.pg_type e on e.oid=t.typelem\n" +
      "               left join pg_catalog.pg_class c on c.oid=t.typrelid and c.relkind <> 'c'\n" +
      "               left join pg_catalog.pg_description d on d.objoid=t.oid and d.classoid='pg_type'::regclass\n" +
      "               join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
      "         where t.typtype != 'd'\n" +
      "           and t.typname not like e'\\\\_%%'\n" +
      "           and c.oid is null) t\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +
      "%s" +
      " order by type_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
    );
  }
  
  public static String getTypeAttributeSubList(String version) {
    return
      "select typid, att_no, schema_name, type_name, attribute_name, display_datatype, att_collation\n" +
      "  from (select typid, att_no, schema_name, type_name, attribute_name, display_datatype, att_collation\n" +
      "          from (select t.oid typid, attnum att_no, n.nspname schema_name, t.typname type_name, attname attribute_name, col.collname att_collation,\n" +
      "                       pg_catalog.format_type(ta.oid, a.atttypmod) display_datatype\n" +
      "                  from pg_catalog.pg_type t\n" +
      "                       join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
      "                       join pg_catalog.pg_attribute a on a.attrelid = t.typrelid\n" +
      "                       join pg_catalog.pg_type ta on a.atttypid = ta.oid\n" +
      "                       join pg_catalog.pg_namespace tn on ta.typnamespace = tn.oid\n" +
      "                       left join pg_catalog.pg_collation col on col.oid = a.attcollation and col.collcollate <> ''\n" +
      "                 where t.typtype = 'c') a\n" +
      "        union all\n" +
      "        select typid, att_no, schema_name, type_name, attribute_name, display_datatype, att_collation\n" +
      "          from (select t.oid typid, e.enumsortorder att_no, n.nspname schema_name, t.typname type_name, e.enumlabel attribute_name, null::name att_collation, null::text display_datatype\n" +
      "                  from pg_catalog.pg_type t\n" +
      "                       join pg_catalog.pg_enum e on t.oid = e.enumtypid\n" +
      "                       join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
      "                 where t.typtype = 'e') a\n" +
      "        union all\n" +
      "        select typid, row_number() over (partition by schema_name, type_name) att_no, schema_name, type_name, attr attribute_name, null::name att_collation, null::text display_datatype\n" +
      "          from (select t.oid typid, n.nspname schema_name, t.typname type_name,\n" +
      "                       unnest(array[\n" +
      "                         'INPUT='||pg_catalog.quote_ident(t.typinput::text),\n" +
      "                         'OUTPUT='||pg_catalog.quote_ident(t.typoutput::text),\n" +
      "                         case when t.typreceive <> 0 then 'RECEIVE='||pg_catalog.quote_ident(t.typreceive::text) end,\n" +
      "                         case when t.typsend <> 0 then 'SEND='||pg_catalog.quote_ident(t.typsend::text) end,\n" +
      "                         case when t.typmodin <> 0 then 'TYPMOD_IN='||pg_catalog.quote_ident(t.typmodin::text) end,\n" +
      "                         case when t.typmodout <> 0 then 'TYPMOD_OUT='||pg_catalog.quote_ident(t.typmodout::text) end,\n" +
      "                         case when t.typanalyze <> 0 then 'ANALYZE='||pg_catalog.quote_ident(t.typanalyze::text) end,\n" +
      "                         case when t.typlen = -1 then 'VARIABLE' else 'INTERNALLENGTH='||t.typlen end,\n" +
      "                         case when t.typbyval then 'PASSEDBYVALUE' end,\n" +
      "                         case t.typalign when 'i' then 'ALIGNMENT=int4' when 's' then 'ALIGNMENT=int2' when 'd' then 'ALIGNMENT=double' end,\n" +
      "                         case t.typstorage when 'x' then 'STORAGE=extended' when 'e' then 'STORAGE=external' when 'm' then 'STORAGE=main' end,\n" +
      "                         case when t.typcategory = 'c' then null else 'CATEGORY='||pg_catalog.quote_literal(t.typcategory::text) end,\n" +
      "                         case when t.typispreferred then 'PREFERRED=true' end,\n" +
      "                         case when t.typdefault is not null then 'DEFAULT='||t.typdefault end,\n" +
      "                         case when e.oid is not null then 'ELEMENT='||pg_catalog.quote_ident(e.typname) end,\n" +
      "                         case when t.typdelim <> ',' then 'DELIMITER='||pg_catalog.quote_literal(t.typdelim::text) end,\n" +
      (new VersionID(version).compare(9, 1) >= 0 ? 
        "                         -- pg 9.1+\n" +
        "                         case when t.typcollation <> 0 then 'COLLATABLE=true' end]) attr\n"
        : "") +
      "                  from pg_catalog.pg_type t\n" +
      "                       join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
      "                       left join pg_catalog.pg_type e on t.typelem = e.oid\n" +
      "                 where t.typtype = 'b') a\n" +
      "              where attr is not null\n" +
      (new VersionID(version).compare(9, 2) >= 0 ?
        "        union all\n" +
        "        -- pg 9.2+\n" +
        "        select typid, row_number() over (partition by schema_name, type_name) att_no, schema_name, type_name, attr attribute_name, null::name att_collation, null::text display_datatype\n" +
        "          from (select t.oid typid, n.nspname schema_name, t.typname type_name,\n" +
        "                       unnest(array[\n" +
        "                         'SUBTYPE='||pg_catalog.format_type(r.rngsubtype, null),\n" +
        "                         case when r.rngsubopc <> 0 then 'SUBTYPE_OPCLASS='||coalesce(pg_catalog.quote_ident(nop.nspname)||'.', '')||pg_catalog.quote_ident(opcname) end,\n" +
        "                         case when r.rngcollation <> 0 then 'COLLATION='||(select collname from pg_catalog.pg_collation where oid = r.rngcollation) end,\n" +
        "                         case when r.rngcanonical <> 0 then 'CANONICAL='||pg_catalog.quote_ident(r.rngcanonical::text) end,\n" +
        "                         case when r.rngsubdiff <> 0 then 'SUBTYPE_DIFF='||pg_catalog.quote_ident(r.rngsubdiff::text) end]) attr\n" +
        "                  from pg_catalog.pg_type t\n" +
        "                       join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
        "                       join pg_catalog.pg_range r on t.oid = r.rngtypid\n" +
        "                         left join pg_catalog.pg_opclass op on op.oid = r.rngsubopc\n" +
        "                           left join pg_catalog.pg_namespace nop on op.opcnamespace = nop.oid and nop.nspname not in ('pg_catalog', 'public', 'information_schema')\n" +
        "                 where t.typtype = 'r') a\n" +
        "              where attr is not null\n"
        : "") +
      "                ) a\n" +
      " order by schema_name, type_name, att_no";
  }

  public static String getTypeAttributeList(String filter, String version) {
    return String.format(
      "with attrs as (\n" +getTypeAttributeSubList(version) +")\n" +
      "select att_no, schema_name, type_name, attribute_name, display_datatype, att_collation\n" +
      "  from attrs\n" +
      " where schema_name = :SCHEMA_NAME\n" +
      "   and type_name = :TYPE_NAME\n" +
      "%s" +
      " order by att_no",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""}
    );
  }

  public static String getTypeSource(String version) {
    return
      "with attrs as (\n" +getTypeAttributeSubList(version) +")\n" +
      "select '--DROP TYPE '||pg_catalog.format_type(t.oid, null)||E'\\n--/\\n'||\n" +
      "       case t.typtype\n" +
      "         when 'b' then 'CREATE TYPE '||pg_catalog.format_type(t.oid, null)||E' (\\n'||string_agg('  '||attribute_name, E',\\n')||E'\\n)'\n" +
      "         when 'c' then 'CREATE TYPE '||pg_catalog.format_type(t.oid, null)||E' AS (\\n'||string_agg('  '||attribute_name||' '||display_datatype||case when att_collation is not null then ' COLLATION '||att_collation else '' end, E',\\n')||E'\\n)'\n" +
      "         when 'e' then 'CREATE TYPE '||pg_catalog.format_type(t.oid, null)||E' AS ENUM (\\n'||string_agg('  '||quote_literal(attribute_name), E',\\n')||E'\\n)'\n" +
      "         when 'r' then 'CREATE TYPE '||pg_catalog.format_type(t.oid, null)||E' AS RANGE (\\n'||string_agg('  '||attribute_name, E',\\n')||E'\\n)'\n" +
      "       end||E'\\n/\\n'||\n" +
      "       'ALTER TYPE '||pg_catalog.format_type(t.oid, null)||' OWNER TO '||pg_catalog.pg_get_userbyid(t.typowner)||E'\\n/\\n'||\n" +
      "       case when d.description is null then '--' else '' end||'COMMENT ON TYPE '||pg_catalog.format_type(t.oid, null)||' IS '||quote_literal(coalesce(d.description, ''))||E'\\n'||case when d.description is null then '--' else '' end||E'/\\n'\n" +
      "       source\n" +
      "  from attrs a\n" +
      "       join pg_catalog.pg_type t on t.oid = a.typid\n" +
      "       left join pg_catalog.pg_description d on d.objoid=t.oid and d.classoid='pg_type'::regclass\n" +
      " where a.schema_name = :SCHEMA_NAME\n" +
      "   and a.type_name = :TYPE_NAME\n" +
      " group by t.typtype, t.typowner, t.oid, d.description";
  }

  public static String getDependTypeList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubDepends, version, false) +
        ")\n" +
        "select distinct *\n" +
        "  from (select o.object_type, o.owner_name, o.object_owner, o.object_name, \n" +
        "               ro.object_type r_object_type, ro.object_name r_object_name, ro.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_type t on t.oid = d.refobjid and t.tableoid = d.refclassid\n" +
        "               left outer join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and t.typname = :OBJECT_NAME\n" +
        "        union all\n" +
        "        select ro.object_type, ro.owner_name, ro.object_owner, ro.object_name, \n" +
        "               o.object_type r_object_type, o.object_name r_object_name, o.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_type t on t.oid = d.objid and t.tableoid = d.classid\n" +
        "               left outer join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and t.typname = :OBJECT_NAME) d\n" +
        "%s" +
        " order by object_name, object_type, object_owner",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
  public static String getTypeDetail() {
    return
      "select t.oid, t.*, d.*, n.*, c.*\n" +
      "  from pg_catalog.pg_type t\n" +
      "       left join pg_catalog.pg_description d on d.objoid=t.oid and d.classoid='pg_type'::regclass\n" +
      "       join pg_catalog.pg_namespace n on t.typnamespace = n.oid\n" +
      "       left join pg_catalog.pg_class c on c.oid=t.typrelid\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and t.typname = :TYPE_NAME";
  }

  public static String getAggregateList(String filter) {
    return String.format(
      "select schema_name, aggregate_name, aggreagte_arguments, owner_name, description, full_aggregate_name, full_object_name\n" +
      "  from (select n.nspname schema_name, p.proname aggregate_name, pg_get_function_identity_arguments(p.oid) aggreagte_arguments,\n" +
      "               pg_catalog.pg_get_userbyid(p.proowner) owner_name, d.description,\n" +
      "               p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' full_aggregate_name,\n" +
      "               quote_ident(n.nspname)||'.'||quote_ident(p.proname)||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' full_object_name\n" +
      "          from pg_catalog.pg_aggregate a\n" +
      "               join pg_catalog.pg_proc p on p.oid = a.aggfnoid\n" +
      "               left join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      "               left join pg_catalog.pg_description d on d.objoid = p.oid and d.classoid = 'pg_catalog.pg_proc'::regclass and d.objsubid = 0) a\n" +
      " where (schema_name = :SCHEMA_NAME or (schema_name = any (current_schemas(false)) and :SCHEMA_NAME = current_schema() and schema_name <> 'public'))\n" +           
      "%s" +
      " order by schema_name, aggregate_name",
      new Object[] {filter != null ? "   and " + filter + "\n" : ""}
    );
  }
  
  public static String getAggregateDetail() {
    return
      "select *\n" +
      "  from pg_catalog.pg_aggregate a\n" +
      "       join pg_catalog.pg_proc p on p.oid = a.aggfnoid\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      "       left join pg_catalog.pg_operator o on o.oid = a.aggsortop\n" +
      "       left join pg_catalog.pg_description d on d.objoid = p.oid and d.classoid = 'pg_catalog.pg_proc'::regclass and d.objsubid = 0\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' = :AGGREGATE_NAME";
  }
  
  public static String getAggregateSource() {
    return
      "select '--DROP AGGREGATE '||quote_ident(n.nspname)||'.'||quote_ident(p.proname)||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||e')\\n--/\\n'||\n" +
      "       'CREATE AGGREGATE '||quote_ident(n.nspname)||'.'||quote_ident(p.proname)||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||e') (\\n'||\n" +
      "       (select string_agg('  '||property, E',\\n')\n" +
      "          from (select unnest(array[\n" +
      "                         'SFUNC = '||pg_catalog.quote_ident(a.aggtransfn::text),\n" +
      "                         'STYPE = '||pg_catalog.format_type(a.aggtranstype, null),\n" +
      "                         'FINALFUNC = '||pg_catalog.quote_ident(case when a.aggfinalfn = 0 then null else a.aggfinalfn::text end),\n" +
      "                         'INITCOND = '||quote_literal(a.agginitval),\n" +
      "                         'SORTOP = '||pg_catalog.quote_ident(o.oprname)\n" +
      "                       ]) property) a\n" +
      "         where property is not null)||e'\\n)\\n/\\n'||\n" +
      "       'ALTER AGGREGATE '||quote_ident(n.nspname)||'.'||quote_ident(p.proname)||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||e') OWNER TO '||pg_catalog.pg_get_userbyid(p.proowner)||e'\\n/\\n'||\n" +
      "       case when d.description is null then '--' else '' end||'COMMENT ON AGGREGATE '||quote_ident(n.nspname)||'.'||quote_ident(p.proname)||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||') IS '||quote_literal(coalesce(d.description, ''))||e'\\n'||\n" +
      "       case when d.description is null then '--' else '' end||e'/\\n'\n" +
      "       source\n" +
      "  from pg_catalog.pg_aggregate a\n" +
      "       join pg_catalog.pg_proc p on p.oid = a.aggfnoid\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      "       left join pg_catalog.pg_operator o on o.oid = a.aggsortop\n" +
      "       left join pg_catalog.pg_description d on d.objoid = p.oid and d.classoid = 'pg_catalog.pg_proc'::regclass and d.objsubid = 0\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' = :AGGREGATE_NAME";
  }

  public static String getAggregateAttributeList() {
    return
      "select unnest(array['SFUNC', 'STYPE', 'FINALFUNC', 'INITCOND', 'SORTOP']) attribute_name,\n" +
      "       unnest(array[a.aggtransfn::text, pg_catalog.format_type(a.aggtranstype, null), a.aggfinalfn::text, a.agginitval, o.oprname]) attribute_value\n" +
      "  from pg_catalog.pg_aggregate a\n" +
      "       join pg_catalog.pg_proc p on p.oid = a.aggfnoid\n" +
      "       left join pg_catalog.pg_namespace n on n.oid = p.pronamespace\n" +
      "       left join pg_catalog.pg_operator o on o.oid = a.aggsortop\n" +
      "       left join pg_catalog.pg_description d on d.objoid = p.oid and d.classoid = 'pg_catalog.pg_proc'::regclass and d.objsubid = 0\n" +
      " where n.nspname = :SCHEMA_NAME\n" +
      "   and p.proname||'('||coalesce(pg_get_function_identity_arguments(p.oid), '')||')' = :AGGREGATE_NAME";
  }

  public static String getDependAggregateList(String filter, String version) {
    return String.format(
      "with o as (\n" +
      getObjectSubList(objectSubDepends, version, false) +
        ")\n" +
        "select distinct *\n" +
        "  from (select o.object_type, o.owner_name, o.object_owner, o.object_name, \n" +
        "               ro.object_type r_object_type, ro.object_name r_object_name, ro.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_catalog.pg_proc t on t.oid = d.refobjid and t.tableoid = d.refclassid\n" +
        "               join pg_catalog.pg_aggregate a on a.aggfnoid = t.oid\n" +
        "               left outer join pg_catalog.pg_namespace n on t.pronamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and t.proname||'('||coalesce(pg_get_function_identity_arguments(t.oid), '')||')' = :OBJECT_NAME\n" +
        "        union all\n" +
        "        select ro.object_type, ro.owner_name, ro.object_owner, ro.object_name, \n" +
        "               o.object_type r_object_type, o.object_name r_object_name, o.object_attribute r_object_attribute\n" +
        "          from pg_depend d\n" +
        "               join o on d.classid = o.classid and d.objid = o.objid and d.objsubid = o.objsubid\n" +
        "               join o ro on d.refclassid = ro.classid and d.refobjid = ro.objid and d.refobjsubid = ro.objsubid\n" +
        "               join pg_catalog.pg_proc t on t.oid = d.objid and t.tableoid = d.classid\n" +
        "               join pg_catalog.pg_aggregate a on a.aggfnoid = t.oid\n" +
        "               left outer join pg_catalog.pg_namespace n on t.pronamespace = n.oid\n" +
        "         where n.nspname = :SCHEMA_NAME\n" +
        "           and t.proname||'('||coalesce(pg_get_function_identity_arguments(t.oid), '')||')' = :OBJECT_NAME) d\n" +
        "%s" +
        " order by object_name, object_type, object_owner",
      new Object[]{filter != null ? " where " + filter + "\n" : ""}
    );
  }
  
}