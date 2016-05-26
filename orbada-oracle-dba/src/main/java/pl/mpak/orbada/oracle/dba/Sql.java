/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public static String getSessionList(String filter, boolean clusters) {
    return String.format(
      "select \n" +
      (clusters ? "       s.inst_id,\n" : "") +
      "       s.status, s.serial#, s.type, s.username, substr( s.osuser, 1, 255 ) as osuser, s.server, s.machine, s.terminal,\n" +
      "       s.program, p.program osprogram, s.logon_time, lockwait, si.physical_reads,\n" +
      "       si.block_gets, si.consistent_gets, si.block_changes, si.consistent_changes,\n" +
      "       s.process, p.spid, p.pid, si.sid, rawtohex( s.sql_address ) as sql_address,\n" +
      "       s.sql_hash_value, s.module, s.audsid, s.client_info, s.action, s.event, s.blocking_session, s.wait_class,\n" +
      "       case\n" +
      "         when s.command >= 0 and s.command <= 99 then\n" +
      "         case s.command\n" +
      "           when 0 then null when 1 then 'CRE TAB' when 2 then 'INSERT' when 3 then 'SELECT' when 4 then 'CRE CLUSTER' \n" +
      "           when 5 then 'ALT CLUSTER' when 6 then 'UPDATE' when 7 then 'DELETE' when 8 then 'DRP CLUSTER' when 9 then 'CRE INDEX' \n" +
      "           when 10 then 'DROP INDEX' when 11 then 'ALT INDEX' when 12 then 'DROP TABLE' when 13 then 'CRE SEQ' when 14 then 'ALT SEQ' \n" +
      "           when 15 then 'ALT TABLE' when 16 then 'DROP SEQ' when 17 then 'GRANT' when 18 then 'REVOKE' when 19 then 'CRE SYN' \n" +
      "           when 20 then 'DROP SYN' when 21 then 'CRE VIEW' when 22 then 'DROP VIEW' when 23 then 'VAL INDEX' when 24 then 'CRE PROC' \n" +
      "           when 25 then 'ALT PROC' when 26 then 'LOCK TABLE' when 28 then 'RENAME' when 29 then 'COMMENT' \n" +
      "           when 30 then 'AUDIT' when 31 then 'NOAUDIT' when 32 then 'CRE DBLINK' when 33 then 'DROP DBLINK' when 34 then 'CRE DB' \n" +
      "           when 35 then 'ALTER DB' when 36 then 'CRE RBS' when 37 then 'ALT RBS' when 38 then 'DROP RBS' when 39 then 'CRE TBLSPC' \n" +
      "           when 40 then 'ALT TBLSPC' when 41 then 'DROP TBLSPC' when 42 then 'ALT SESSION' when 43 then 'ALT USER' when 44 then 'COMMIT' \n" +
      "           when 45 then 'ROLLBACK' when 46 then 'SAVEPOINT' when 47 then 'PL/SQL EXEC' when 48 then 'SET XACTN' when 49 then 'SWITCH LOG'\n" +
      "           when 50 then 'EXPLAIN' when 51 then 'CRE USER' when 52 then 'CRE ROLE' when 53 then 'DROP USER' when 54 then 'DROP ROLE' \n" +
      "           when 55 then 'SET ROLE' when 56 then 'CRE SCHEMA' when 57 then 'CRE CTLFILE' when 58 then 'ALT TRACING' when 59 then 'CRE TRG' \n" +
      "           when 60 then 'ALT TRG' when 61 then 'DRP TRG' when 62 then 'ANALYZE TAB' when 63 then 'ANALYZE IDX' when 64 then 'ANALYZE CLUS' \n" +
      "           when 65 then 'CRE PROFILE' when 66 then 'DRP PROFILE' when 67 then 'ALT PROFILE' when 68 then 'DRP PROC' when 69 then 'DRP PROC' \n" +
      "           when 70 then 'ALT RESOURCE' when 71 then 'CRE SNPLOG' when 72 then 'ALT SNPLOG' when 73 then 'DROP SNPLOG' when 74 then 'CREATE SNAP'\n" +
      "           when 75 then 'ALT SNAP' when 76 then 'DROP SNAP' when 79 then 'ALT ROLE' when 79 then 'ALT ROLE' \n" +
      "           when 85 then 'TRUNC TAB' when 86 then 'TRUNC CLUST' when 88 then 'ALT VIEW' \n" +
      "           when 91 then 'CRE FUNC' when 92 then 'ALT FUNC' when 93 then 'DROP FUNC' when 94 then 'CRE PKG' \n" +
      "           when 95 then 'ALT PKG' when 96 then 'DROP PKG' when 97 then 'CRE PKG BODY' when 98 then 'ALT PKG BODY' when 99 then 'DRP PKG BODY'\n" +
      "           else to_char(s.command)\n" +
      "         end\n" +
      "         when s.command >= 100 and s.command <= 199 then\n" +
      "         case s.command\n" +
      "           when 100 then 'LOGON' when 101 then 'LOGOFF' when 102 then 'LOGOFF BY CLEANUP' when 103 then 'SESSION REC' when 104 then 'SYSTEM AUDIT' \n" +
      "           when 105 then 'SYSTEM NOAUDIT' when 106 then 'AUDIT DEFAULT' when 107 then 'NOAUDIT DEFAULT' when 108 then 'SYSTEM GRANT' when 109 then 'SYSTEM REVOKE' \n" +
      "           when 110 then 'CRE PUB SYNONYM' when 111 then 'DROP PUB SYNONYM' when 112 then 'CRE PUB DBLINK' when 113 then 'DROP PUB DBLINK' when 114 then 'GRANT ROLE' \n" +
      "           when 115 then 'REVOKE ROLE' when 116 then 'EXEC PROC' when 117 then 'USER COMMENT' when 118 then 'ENABLE TRG' when 119 then 'DISABLE TRG' \n" +
      "           when 120 then 'ENABLE ALL TRGS' when 121 then 'DISABLE ALL TRGS' when 122 then 'NETWORK ERROR' when 123 then 'EXEC TYPE' \n" +
      "           when 157 then 'CRE DIRECTORY' when 158 then 'DROP DIRECTORY' when 159 then 'CRE LIBRARY' \n" +
      "           when 160 then 'CRE JAVA' when 161 then 'ALT JAVA' when 162 then 'DROP JAVA' when 163 then 'CRE OPERATOR' when 164 then 'CRE INDEXTYPE' \n" +
      "           when 165 then 'DROP INDEXTYPE' when 167 then 'DROP OPERATOR' when 168 then 'ASSOC STATS' when 169 then 'DISASSOC STATS' \n" +
      "           when 170 then 'CALL METHOD' when 171 then 'CRE SUMMARY' when 172 then 'ALT SUMMARY' when 173 then 'DROP SUMMARY' when 174 then 'CRE DIM' \n" +
      "           when 175 then 'ALT DIM' when 176 then 'DROP DIM' when 177 then 'CRE CONTEXT' when 178 then 'DROP CONTEXT' when 179 then 'ALT OUTLINE'\n" +
      "           when 180 then 'CRE OUTLINE' when 181 then 'DROP OUTLINE' when 182 then 'UPD INDEXES' when 183 then 'ALT OPERATOR' \n" +
      "           when 186 then 'MERGE'\n" +
      "           else to_char(s.command)\n" +
      "         end\n" +
      "         when s.command = -67 then 'MERGE' \n" +
      "         when s.command = -86 then 'CALL'\n" +
      "         else to_char(s.command) end command,\n" +
      "         case when s.status = 'ACTIVE' then last_call_et else null end wait_sec\n" +
      "  from " +(clusters ? "gv$session s, gv$process p, gv$sess_io si" : "v$session s, v$process p, v$sess_io si") +"\n" +
      " where s.paddr = p.addr\n" +
      "   and si.sid(+) = s.sid\n" +
      (clusters ? "   and s.inst_id = p.inst_id and p.inst_id = si.inst_id\n" : "") +
      "%s" +
      " order by s.status, si.consistent_gets +si.block_gets desc",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getSessionLockList(String filter, boolean clusters) {
    return String.format(
      "select /*+ ordered */\n" +
      (clusters ? "       s.inst_id,\n" : "") +
      "       o.owner schema_name, o.owner||'.'||o.object_name object, l.os_user_name os_username, l.oracle_username, s.program, s.machine, nvl( s.lockwait, 'ACTIVE' ) lockwait,\n" +
      "       DECODE(locked_mode, 0, 'None', 1, 'Null', 2, 'Row-Shared', 3, 'Row-Exclusive', 4, 'Share', 5, 'Share Row-Exclusive', 6, 'Exclusive', TO_CHAR(l.locked_mode)) locked_mode,\n" +
      "       o.object_type, s.serial#, s.sid, s.audsid\n" +
      "  from " +(clusters ? "gv$locked_object l, gv$session s, all_objects o" : "v$locked_object l, v$session s, all_objects o") +"\n" +
      " where l.object_id = o.object_id\n" +
      "   and s.sid = l.session_id\n" +
      (clusters ? "   and s.inst_id = l.inst_id\n   and (s.inst_id = :inst_id or :inst_id is null)" : "") +
      "   and (s.sid = :sid or :sid is null)\n" +
      "%s" +
      " order by object asc, lockwait desc",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getSessionSqlList(String filter, boolean clusters) {
    return String.format(
      "select /*+ ORDERED */ " +(clusters ? "s.inst_id, " : "") +"s.sql_fulltext sql_text, o.address, o.hash_value, o.sid, s.last_active_time,\n" +
      "       s.sharable_mem, s.persistent_mem, s.runtime_mem, s.executions, s.disk_reads, s.buffer_gets, s.optimizer_mode,\n" +
      "       s.cpu_time, s.elapsed_time\n" +
      "  from " +(clusters ? "gv$open_cursor o, gv$sql s" : "v$open_cursor o, v$sql s") +"\n" +
      " where o.address = s.address\n" +
      "   and o.hash_value = s.hash_value\n" +
      "   and o.sid = :sid\n" +
      (clusters ? "   and o.inst_id = :inst_id\n   and o.inst_id = s.inst_id\n" : "") +
      "%s" +
      " order by last_active_time desc",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getSessionStatList(String filter, boolean clusters) {
    return String.format(
      "select " +(clusters ? "s.inst_id, " : "") +"s.sid, n.name, s.value, s.statistic#\n" +
      "  from " +(clusters ? "gv$sesstat s, gv$statname n" : "v$sesstat s, v$statname n") +"\n" +
      " where s.sid = :sid\n" +
      (clusters ? "   and and s.inst_id = :inst_id\n   and n.inst_id = s.inst_id\n" : "") +
      "   and n.statistic# = s.statistic#\n" +
      "%s" +
      " order by name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getLockViewRole() {
    return "select count( distinct table_name ) cnt from all_tab_privs where table_name in ('V_$SESSION', 'V_$PROCESS', 'V_$LOCKED_OBJECT') and privilege = 'SELECT'";
  }

  public static String getSessionViewRole() {
    return "select count( distinct table_name ) cnt from all_tab_privs where table_name in ('V_$SESSION', 'V_$PROCESS', 'V_$SESS_IO') and privilege = 'SELECT'";
  }

  public static String getGSessionViewRole() {
    return "select count( * ) cnt from all_objects where object_name in ('GV$SESSION', 'GV$PROCESS', 'GV$SESS_IO')";
  }

  public static String getSqlViewRole() {
    return "select count( distinct table_name ) cnt from all_tab_privs where table_name in ('V_$OPEN_CURSOR', 'V_$SQL') and privilege = 'SELECT'";
  }

  public static String getSessStatViewRole() {
    return "select count( distinct table_name ) cnt from all_tab_privs where table_name in ('V_$SESSTAT', 'V_$STATNAME') and privilege = 'SELECT'";
  }

  public static String getClusterDetect_V() {
    return "select count( 0 ) gvcount from v$instance";
  }

  public static String getClusterDetect_GV() {
    return "select count( 0 ) gvcount from gv$instance";
  }

  public static String getParameterList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select p.inst_id, p.num, p.name, p.type, decode(p.type, 1, 'BOOLEAN', 2, 'STRING', 3, 'INTEGER', 4, 'PARAMETER FILE', 5, 'RESERVED', 6, 'BIG INTEGER') display_type,\n" +
      "               p.display_value, p.isdefault, p.isses_modifiable, p.issys_modifiable, p.isinstance_modifiable, p.ismodified, p.isadjusted, p.isdeprecated, p.description, \n" +
      "               p.update_comment\n" +
      "          from gv$parameter p)\n" +
      " where (name like '%%'||:USER_TEXT||'%%' or description like '%%'||:USER_TEXT||'%%' or update_comment like '%%'||:USER_TEXT||'%%')\n" +
      "%s" +
      " order by name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }
  
  public static String getSessionId() {
    return "select sid, serial# serial, audsid from v$session where audsid = userenv( 'SESSIONID' )";
  }
  
  public static String getDataFileList() {
    return
      "select f.file_name, f.file_id, f.tablespace_name, f.blocks, t.block_size, f.bytes, f.status, f.online_status, sum(fs.blocks) free_blocks, sum(fs.bytes) free_bytes\n" +
      "  from dba_data_files f, dba_tablespaces t, dba_free_space fs\n" +
      " where f.tablespace_name = t.tablespace_name\n" +
      "   and fs.file_id = f.file_id\n" +
      " group by f.file_name, f.file_id, f.tablespace_name, f.blocks, t.block_size, f.bytes, f.status, f.online_status\n" +
      " order by tablespace_name, file_name";
  }
  
}
