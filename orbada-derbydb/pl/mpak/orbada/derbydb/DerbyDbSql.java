/*
 * DerbyDbSql.java
 * 
 * Created on 2007-10-28, 16:59:44
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.derbydb;

/**
 *
 * @author akaluza
 */
public class DerbyDbSql {

  public static String getTableList(String filter) {
    return String.format(
      "select t.tablename, s.authorizationid\n" +
      "  from sys.systables t, sys.sysschemas s\n" +
      " where s.schemaid = t.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and t.tabletype in ('T', 'S')\n" +
      "%s" +
      " order by t.tablename",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getViewList(String filter) {
    return String.format(
      "select t.tablename viewname, s.authorizationid\n" +
      "  from sys.sysviews v, sys.systables t, sys.sysschemas s\n" +
      " where v.tableid = t.tableid\n" +
      "   and s.schemaid = t.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "%s" +
      " order by viewname",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  /**
   * <p>Procedury, funkcje lub oba w zale¿noœci od parametru :ALIASTYPE
   * @param filter 
   * @return 
   */
  public static String getProcedureList(String filter) {
    return String.format(
      "select a.alias, (case when a.aliastype = 'P' then 'PROCEDURE' when a.aliastype = 'F' then 'FUNCTION' end) aliastype,\n" +
      "       a.javaclassname||'.'||(substr(varchar(a.aliasinfo, 1024), 1, locate('(', varchar(a.aliasinfo, 1024)) -1)) javaclassname\n" +
      "  from sys.sysaliases a, sys.sysschemas s\n" +
      " where a.schemaid = s.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and a.aliastype = (case when :ALIASTYPE is null then a.aliastype else :ALIASTYPE end)\n" +
      "%s" +
      " order by alias",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getFileList(String filter) {
    return String.format(
      "select s.schemaname, f.filename\n" +
      "  from sys.sysfiles f, sys.sysschemas s\n" +
      " where f.schemaid = s.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      " order by filename",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableColumnList(String filter) {
    return String.format(
      "select c.columnnumber, c.columnname, cast(c.columndatatype as varchar(2048)) columndatatype, cast(c.columndefault as varchar(1024)) columndefault, c.autoincrementvalue, c.autoincrementstart, c.autoincrementinc\n" +
      "  from sys.syscolumns c, sys.systables t, sys.sysschemas s\n" +
      " where t.tableid = c.referenceid\n" +
      "   and s.schemaid = t.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and t.tablename = :TABLENAME\n" +
      "%s" +
      " order by c.columnnumber",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableIndexList(String filter) {
    return String.format(
      "select cols.columnnumber, c.conglomeratename index_name, cols.columnname, varchar(c.descriptor, 1024) descriptor, c.isconstraint,\n" +
      "       (case when locate('UNIQUE ', cast(c.DESCRIPTOR as varchar(100))) = 0 then 0 else 1 end) isunique\n" +
      "  from sys.SYSCONGLOMERATES c, sys.systables t, sys.sysschemas s, sys.syscolumns cols\n" +
      " where t.tableid = c.tableid\n" +
      "   and s.schemaid = t.schemaid\n" +
      "   and c.isindex\n" +
      "   and T.TABLEID = COLS.REFERENCEID\n" +
      "   and (locate('('||rtrim( char(cols.columnnumber, 4) )||')', cast(c.DESCRIPTOR as varchar(100))) > 0 or\n" +
      "        locate('('||rtrim( char(cols.columnnumber, 4) )||',', cast(c.DESCRIPTOR as varchar(100))) > 0 or\n" +
      "        locate(' '||rtrim( char(cols.columnnumber, 4) )||',', cast(c.DESCRIPTOR as varchar(100))) > 0 or\n" +
      "        locate(' '||rtrim( char(cols.columnnumber, 4) )||')', cast(c.DESCRIPTOR as varchar(100))) > 0)\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and (t.tablename = :TABLENAME or :TABLENAME is null)\n" +
      "%s" +
      " order by index_name, columnnumber",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableConstraintList(String filter) {
    return String.format(
      "select t.tablename, cs.constraintname, \n" +
      "       (case when cs.type = 'P' then 'Primary Key' when cs.type = 'F' then 'Foreign Key' when cs.type = 'C' then 'Check' when cs.type = 'U' then 'Unique' end) type,\n" +
      "       (case when cs.state = 'E' then 'Y' else 'N' end) enabled,\n" +
      "       tfk.tablename tablename_fk, csfk.constraintname constraintname_fk,\n" +
      "       (case when fk.deleterule = 'R' then 'NO ACTION' when fk.deleterule = 'S' then 'RESTRICT' when fk.deleterule = 'C' then 'CASCADE' when fk.deleterule = 'U' then 'SET NULL' end) deleterule_fk,\n" +
      "       (case when fk.updaterule = 'R' then 'NO ACTION' when fk.updaterule = 'S' then 'RESTRICT' end) updaterule_fk,\n" +
      "       chk.checkdefinition,\n" +
      "       varchar(\n" +
      "         (case when chk.checkdefinition is not null then chk.checkdefinition else '' end)||\n" +
      "         (case when fk.deleterule is null or fk.deleterule = 'R' then '' else 'ON DELETE '||tfk.tablename||' '||(case when fk.deleterule = 'S' then 'RESTRICT' when fk.deleterule = 'C' then 'CASCADE' when fk.deleterule = 'U' then 'SET NULL' end) end)||\n" +
      "         (case when fk.updaterule is null or fk.updaterule = 'R' then '' else 'ON UPDATE '||tfk.tablename||' RESTRICTED' end), 512) remarks\n" +
      "  from sys.sysconstraints cs \n" +
      "         left outer join sys.sysforeignkeys fk on (cs.constraintid = fk.constraintid)\n" +
      "         left outer join sys.syschecks chk on (cs.constraintid = chk.constraintid)\n" +
      "         left outer join sys.sysconstraints csfk on (fk.keyconstraintid = csfk.constraintid)\n" +
      "         left outer join sys.systables tfk on (csfk.tableid = tfk.tableid),\n" +
      "       sys.systables t, sys.sysschemas s\n" +
      " where cs.tableid = t.tableid\n" +
      "   and s.schemaid = cs.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and (t.tablename = :TABLENAME or :TABLENAME is null)\n" +
      "%s" +
      " order by cs.constraintname",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getTableTriggerList(String filter) {
    return String.format(
      "select trg.triggername, trg.creationtimestamp, trg.tableid, t.tablename,\n" +
      "       (case when trg.state = 'E' then 'ENABLED' when trg.state = 'D' then 'DISABLED' end) enabled,\n" +
      "       (case when trg.firingtime = 'A' then 'AFTER' when trg.firingtime = 'B' then 'NO CASCADE BEFORE' end) firingtime,\n" +
      "       (case when trg.event = 'D' then 'DELETE' when trg.event = 'U' then 'UPDATE' when trg.event = 'I' then 'INSERT' end) event,\n" +
      "       'FOR EACH '||(case when trg.type = 'R' then 'ROW MODE DB2SQL' when trg.type = 'S' then 'STATEMENT' end) type,\n" +
      "       (case when trg.referencingold = 1 then (case when trg.type = 'S' then 'OLD_TABLE' else 'OLD' end)||' AS '||trg.OLDREFERENCINGNAME end) referencingold,\n" +
      "       (case when trg.referencingnew = 1 then (case when trg.type = 'S' then 'NEW_TABLE' else 'NEW' end)||' AS '||trg.NEWREFERENCINGNAME end) referencingnew,\n" +
      "       trg.triggerdefinition, trg.referencedcolumns\n" +
      "  from sys.systriggers trg, sys.sysschemas s, sys.systables t\n" +
      " where trg.schemaid = s.schemaid\n" +
      "   and t.tableid = trg.tableid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and (t.tablename = :TABLENAME or :TABLENAME is null)\n" +
      "%s" +
      " order by triggername",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getViewSource() {
    return "select v.viewdefinition\n" +
      "  from sys.sysviews v, sys.systables t, sys.sysschemas s\n" +
      " where v.tableid = t.tableid\n" +
      "   and s.schemaid = t.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and t.tablename = :VIEWNAME";
  }

  public static String getProcedureSource() {
    return "select 'CREATE '||(case when a.aliastype = 'P' then 'PROCEDURE' when a.aliastype = 'F' then 'FUNCTION' end)||\n" +
      "       ' \"'||s.schemaname||'\".\"'||a.alias||'\" '||substr(varchar(a.aliasinfo, 1024), locate('(', varchar(a.aliasinfo, 1024)))||\n" +
      "       ' EXTERNAL NAME '''||a.javaclassname||'.'||(substr(varchar(a.aliasinfo, 1024), 1, locate('(', varchar(a.aliasinfo, 1024)) -1))||'''' source\n" +
      "  from sys.sysaliases a, sys.sysschemas s\n" +
      " where a.schemaid = s.schemaid\n" +
      "   and s.schemaname = :SCHEMANAME\n" +
      "   and a.alias = :PROCNAME";
  }

  public static String getObjectList() {
    return "select objectid, schemaname, objectname, rtrim( objecttype ) objecttype, filename, tableschema, tablename\n" +
      "  from (select t.tableid objectid, s.schemaname, t.tablename objectname, \n" +
      "               (case when t.tabletype in ('T', 'S') then 'TABLE' when t.tabletype = 'V' then 'VIEW' end) objecttype, \n" +
      "               cast(null as varchar(1)) filename, \n" +
      "               cast(null as varchar(1)) tableschema, cast(null as varchar(1)) tablename\n" +
      "          from sys.systables t, sys.sysschemas s\n" +
      "         where s.schemaid = t.schemaid\n" +
      "        union all\n" +
      "        select a.aliasid objectid, s.schemaname, a.alias objectname, \n" +
      "               (case when a.aliastype = 'P' then 'PROCEDURE' when a.aliastype = 'F' then 'FUNCTION' end) objecttype,\n" +
      "               cast(null as varchar(1)) filename, \n" +
      "               cast(null as varchar(1)) tableschema, cast(null as varchar(1)) tablename\n" +
      "          from sys.sysaliases a, sys.sysschemas s\n" +
      "         where s.schemaid = a.schemaid\n" +
      "        union all\n" +
      "        select trg.triggerid objectid, s.schemaname, trg.triggername objectname, 'TRIGGER' objecttype,\n" +
      "               cast(null as varchar(1)) filename, si.schemaname tableschema, t.tablename\n" +
      "          from sys.systriggers trg, sys.sysschemas s, sys.systables t, sys.sysschemas si\n" +
      "         where s.schemaid = trg.schemaid\n" +
      "           and t.tableid = trg.tableid\n" +
      "           and t.schemaid = si.schemaid\n" +
      "        union all\n" +
      "        select i.conglomerateid objectid, s.schemaname schemaname, i.conglomeratename objectname, 'INDEX' objecttype, \n" +
      "               cast(null as varchar(1)) filename, si.schemaname tableschema, t.tablename\n" +
      "          from sys.sysconglomerates i, sys.sysschemas s, sys.systables t, sys.sysschemas si\n" +
      "         where s.schemaid = i.schemaid\n" +
      "           and i.isindex\n" +
      "           and t.tableid = i.tableid\n" +
      "           and t.schemaid = si.schemaid\n" +
      "        union all\n" +
      "        select f.fileid objectid, s.schemaname, f.filename objectname, 'FILE' objecttype, filename, \n" +
      "               cast(null as varchar(1)) tableschema, cast(null as varchar(1)) tablename\n" +
      "          from sys.sysfiles f, sys.sysschemas s\n" +
      "         where s.schemaid = f.schemaid) x";
  }

  public static String getObjectType() {
    return getObjectList() + "\n" +
      " where x.schemaname = :SCHEMANAME\n" +
      "   and x.objectname = :OBJECTNAME";
  }

  public static String getObjectsByType() {
    return "select objectid, schemaname, objectname, filename, tableschema, tablename\n" +
      "  from (" + getObjectList() + ") x\n" +
      " where schemaname = :schemaname\n" +
      "   and objecttype = :objecttype";
  }
}
