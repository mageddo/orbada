/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.mpak.oracle.tune;

/**
 *
 * @author akaluza
 */
public class Sql {

  public static String getProfilerRunList(String filter) {
    return String.format(
      "select runid, run_date, round(run_total_time /:PREC, 2) run_total_time, run_comment1\n" +
      "  from plsql_profiler_runs\n" +
      "%s" +
      " order by runid desc",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getProfilerUnitList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select u.runid, u.unit_type, u.unit_owner, u.unit_name, round(sum(u.total_time) /:PREC, 2) total_time,\n" +
      "               case when r.run_total_time > 0 then round(sum(u.total_time) /r.run_total_time *100, 2) else 0 end perc_total_time\n" +
      "          from plsql_profiler_units u, plsql_profiler_runs r\n" +
      "         where r.runid = :RUNID\n" +
      "           and r.runid = u.runid\n" +
      "         group by u.runid, u.unit_type, u.unit_owner, u.unit_name, r.run_total_time)\n" +
      "%s" +
      " order by total_time desc",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }

  public static String getProfilerDataList(String filter) {
    return String.format(
      "select *\n" +
      "  from (select u.runid, u.unit_type, u.unit_owner, u.unit_name, d.line#,\n" +
      "               sum(d.total_occur) total_occur, round(sum(d.total_time) /:PREC, 2) total_time, round(sum(d.min_time) /:PREC, 2) min_time, round(sum(d.max_time) /:PREC, 2) max_time, \n" +
      "               round(case when sum(d.total_occur) > 0 then round(sum(d.total_time) /sum(d.total_occur), 0) else sum(d.total_time) end /:PREC, 2) avg_time,\n" +
      "               case when sum(u.total_time) > 0 then round(sum(d.total_time) /sum(u.total_time) *100, 2) else 0 end perc_total_time\n" +
      "          from plsql_profiler_units u\n" +
      "                 join plsql_profiler_data d on u.runid = d.runid and u.unit_number = d.unit_number\n" +
      "         where u.runid = :runid\n" +
      "           and u.unit_owner = :UNIT_OWNER\n" +
      "           and u.unit_type = :UNIT_TYPE\n" +
      "           and u.unit_name = :UNIT_NAME\n" +
      "         group by u.runid, u.unit_type, u.unit_owner, u.unit_name, d.line#)\n" +
      "%s" +
      " order by total_time desc",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }
  
  public static String getSqlColumnCheckList() {
    return "select column_name from all_tab_columns where owner = 'SYS' and table_name = 'V_$SQL' and column_name in ('PARSING_SCHEMA_NAME', 'LAST_ACTIVE_TIME')";
  }

  public static String getSqlList(String filter, boolean ora10plus, boolean col_last_active_time, boolean col_parsing_schema_name) {
    if (ora10plus) {
      return String.format(
        "select s.sql_id, s.sql_fulltext, s.sharable_mem, s.persistent_mem, s.runtime_mem, s.fetches, s.executions, \n" +
        "       s.parse_calls, s.disk_reads, s.direct_writes, s.buffer_gets, s.application_wait_time, \n" +
        "       s.concurrency_wait_time, s.cluster_wait_time, s.user_io_wait_time, s.plsql_exec_time, s.java_exec_time, s.rows_processed, \n" +
        "       s.optimizer_mode, s.optimizer_cost, " +(col_parsing_schema_name ? "s.parsing_schema_name" : "(select username from all_users where user_id = s.parsing_schema_id)") +" parsing_schema_name, rawtohex(s.address) address, s.hash_value, s.plan_hash_value, s.child_number, \n" +
        "       s.module, s.cpu_time, s.elapsed_time, rawtohex(s.child_address) child_address, s.object_status, \n" +
        "       s.last_load_time" +(col_last_active_time ? ", s.last_active_time" : "") +"\n" +
        "  from v$sql s\n" +
        " where " +(col_parsing_schema_name ? "s.parsing_schema_name" : "(select username from all_users where user_id = s.parsing_schema_id)") +" = :SCHEMA_NAME\n" +
//        " where (s.parsing_schema_name = :SCHEMA_NAME or :SCHEMA_NAME is null)\n" +
        "   and ((upper(s.sql_fulltext) like '%%'||upper(:SEARCH)||'%%' escape '\\') or :SEARCH is null)\n" +
        "%s" +
        " order by " +(col_last_active_time ? ", last_active_time" : "last_load_time") +" desc",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
    }
    else {
      return String.format(
        "select s.sql_text, s.sharable_mem, s.persistent_mem, s.runtime_mem, s.fetches, s.executions, \n" +
        "       s.parse_calls, s.disk_reads, s.buffer_gets, s.rows_processed, \n" +
        "       s.optimizer_mode, s.optimizer_cost, rawtohex(s.address) address, s.hash_value, s.plan_hash_value, s.child_number, \n" +
        "       s.module, s.cpu_time, s.elapsed_time, rawtohex(s.child_address) child_address, s.object_status, \n" +
        "       s.last_load_time\n" +
        "  from v$sql s\n" +
        " where ((upper(s.sql_text) like '%%'||upper(:SEARCH)||'%%' escape '\\') or :SEARCH is null)\n" +
        "%s" +
        " order by last_load_time desc",
        new Object[]{filter != null ? "   and " + filter + "\n" : ""}
      );
    }
  }

  public static String getSqlObjectsList() {
    return
      "select distinct p.object_owner, p.object_name, a.object_type\n" +
      "  from v$sql_plan p, all_objects a\n" +
      " where p.address = hextoraw(:ADDRESS)\n" +
      "   and p.hash_value = :hash_value\n" +
      "   and p.child_number = :child_number\n" +
      "   and a.owner = p.object_owner\n" +
      "   and a.object_name = p.object_name\n" +
      " order by object_owner, object_name";
  }

  public static String getSqlPlanList(boolean ora10plus) {
    return
      "SELECT LEVEL, \n" +
      "        '<html><font color=gray>'||LEVEL||'</font> <b>'||OPERATION||'</b>'||\n" +
      "        DECODE( OTHER_TAG, NULL, '', '*'||DECODE( OBJECT_NODE, NULL, '', ' ['||OBJECT_NODE||']' ) )||\n" +
      "        DECODE( OPTIONS, NULL, '', ' ['||\n" +
      "          DECODE(OPTIONS, \n" +
      "            'FULL', '<font color=red>'||OPTIONS||'</font>', \n" +
      "            'UNIQUE SCAN', '<font color=green>'||OPTIONS||'</font>', \n" +
      "            'RANGE SCAN', '<font color=green>'||OPTIONS||'</font>', \n" +
      "          OPTIONS)||']' )||\n" +
      "        DECODE( OPTIMIZER, NULL, '', ' '||OPTIMIZER )||\n" +
      (ora10plus ? "        DECODE( OBJECT_TYPE, NULL, '', ' '||OBJECT_TYPE )||\n" : "") +
      "        DECODE( OBJECT_NAME, NULL, '', ' <b>'||OBJECT_OWNER||'.'||OBJECT_NAME||'</b>' )||\n" +
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
      "        DECODE(ACCESS_PREDICATES, NULL, '', ' '||ACCESS_PREDICATES)||\n" +
      "        DECODE(FILTER_PREDICATES, NULL, '', ' '||FILTER_PREDICATES)||\n" +
      "        '</font>'\n" +
      "        PLAN_OUTPUT,\n" +
      "        OPERATION, OPTIONS, ACCESS_PREDICATES, FILTER_PREDICATES\n" +
      "   FROM v$sql_plan\n" +
      "CONNECT BY PRIOR id = parent_id\n" +
      "    AND PRIOR address = address\n" +
      "    AND PRIOR hash_value = hash_value\n" +
      "    AND PRIOR child_number = child_number\n" +
      "  START WITH id = 0\n" +
      "    AND address = hextoraw(:ADDRESS)\n" +
      "    AND hash_value = :HASH_VALUE\n" +
      "    AND child_number = :CHILD_NUMBER\n" +
      "  ORDER BY ID, POSITION";
  }

  public static String getStartProfiler() {
    return
      "DECLARE\n" +
      "  l_result  PLS_INTEGER;\n" +
      "BEGIN\n" +
      "  l_result := DBMS_PROFILER.start_profiler(SYSDATE, 'SESSIONID:'||userenv('SESSIONID')||' TERMINA:'||userenv('TERMINAL')||' USER:'||USER, :runid);\n" +
      "END;";
  }

  public static String getStopProfiler() {
    return
      "DECLARE\n" +
      "  l_result  PLS_INTEGER;\n" +
      "BEGIN\n" +
      "  l_result := DBMS_PROFILER.stop_profiler;\n" +
      "  dbms_profiler.rollup_run(:runid);\n" +
      "END;";
  }

  public static String getPauseProfiler() {
    return
      "DECLARE\n" +
      "  l_result PLS_INTEGER;\n" +
      "BEGIN\n" +
      "  l_result := dbms_profiler.flush_data;\n" +
      "  l_result := dbms_profiler.pause_profiler;\n" +
      "  dbms_profiler.rollup_run(:runid);\n" +
      "END;";
  }

  public static String getResumeProfiler() {
    return
      "DECLARE\n" +
      "  l_result PLS_INTEGER;\n" +
      "BEGIN\n" +
      "  l_result := dbms_profiler.resume_profiler;\n" +
      "END;";
  }

  public static String getCorrectTriggerLinesProfiler() {
    return
      "DECLARE\n" +
      "  offset NUMBER;\n" +
      "  CURSOR c1_triggers IS\n" +
      "    SELECT unit_owner, unit_name, unit_type, unit_number\n" +
      "      FROM plsql_profiler_units\n" +
      "     WHERE runid = :runid\n" +
      "       AND unit_type = 'TRIGGER';\n" +
      "BEGIN\n" +
      "  FOR c1 IN c1_triggers LOOP\n" +
      "    SELECT NVL(MIN(line) - 1, -1)\n" +
      "      INTO offset\n" +
      "      FROM all_source\n" +
      "     WHERE owner = c1.unit_owner\n" +
      "       AND name  = c1.unit_name\n" +
      "       AND type  = c1.unit_type\n" +
      "       AND (UPPER(text) LIKE '%BEGIN%' OR UPPER(text) LIKE '%DECLARE%');\n" +
      "    IF offset > 0 THEN\n" +
      "      UPDATE plsql_profiler_data\n" +
      "         SET line# = line# + offset\n" +
      "       WHERE runid = :runid\n" +
      "         AND unit_number = c1.unit_number;\n" +
      "    END IF;\n" +
      "  END LOOP;\n" +
      "  COMMIT;\n" +
      "END;";
  }

  public static String getDeleteRunProfiler() {
    return
      "begin\n" +
      "  delete from plsql_profiler_data where runid = :runid;\n" +
      "  delete from plsql_profiler_units where runid = :runid;\n" +
      "  delete from plsql_profiler_runs where runid = :runid;\n" +
      "  commit;\n" +
      "end;";
  }

  public static String getProfilerTableExists() {
    return
      "select count( 0 ) cnt\n" +
      "  from all_objects\n" +
      " where object_name in ('PLSQL_PROFILER_DATA', 'PLSQL_PROFILER_RUNS', 'PLSQL_PROFILER_UNITS')";
  }

  public static String getProfilerUnitPrecision() {
    return "select case when platform_id = 7 then 1000000000 else 1000000 end prec from v$database";
  }

  public static String getSqlPlanTableExists() {
    return
      "select count( 0 ) cnt\n" +
      "  from all_objects\n" +
      " where object_name in ('V$SQL', 'V_$SQL', 'V$SQL_PLAN', 'V_$SQL_PLAN')";
  }

  public static String getStatsViewCount() {
    return "select count( 0 ) cnt from all_objects where object_name in ('V$MYSTAT', 'V$STATNAME')";
  }

  public static String getStatsRoleCount() {
    return "select count( 0 ) cnt from SESSION_ROLES where role in ('SELECT_CATALOG_ROLE', 'PLUSTRACE')";
  }

  public static String getStatsList() {
    return
      "select name, value\n" +
      "  from V$MYSTAT s, V$STATNAME n\n" +
      " where n.statistic# = s.statistic#\n" +
      "   and value <> 0\n" +
      " order by name";
  }

}
