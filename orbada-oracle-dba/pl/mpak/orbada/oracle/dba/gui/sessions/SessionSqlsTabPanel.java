package pl.mpak.orbada.oracle.dba.gui.sessions;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class SessionSqlsTabPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

  private boolean clusters;
  
  public SessionSqlsTabPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    clusters = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"));
    super.init();
  }
  
  public String getTitle() {
    return stringManager.getString("SessionSqlsTabPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "oracle-session-sqls";
  }

  @Override
  public String getSchemaColumnName() {
    if (clusters) {
      return "inst_id";
    }
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "sid";
  }

  @Override
  public String getPropColumnName() {
    return "address";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("last_active_time", stringManager.getString("last-active-time"), 120),
      new QueryTableColumn("sharable_mem", stringManager.getString("sharable-mem"), 100),
      new QueryTableColumn("persistent_mem", stringManager.getString("persistent-mem"), 100),
      new QueryTableColumn("runtime_mem", stringManager.getString("runtime-mem"), 100),
      new QueryTableColumn("executions", stringManager.getString("executions"), 70),
      new QueryTableColumn("disk_reads", stringManager.getString("disk-reads"), 70),
      new QueryTableColumn("buffer_gets", stringManager.getString("buffer-gets"), 70),
      new QueryTableColumn("cpu_time", stringManager.getString("cpu-time"), 70),
      new QueryTableColumn("elapsed_time", stringManager.getString("elapsed-time"), 70),
      new QueryTableColumn("optimizer_mode", stringManager.getString("optimizer-mode"), 130),
      new QueryTableColumn("sql_text", stringManager.getString("sql-text"), 600)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionSqlList(filter.getSqlText(), clusters);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("s.optimizer_mode", stringManager.getString("optimizer-mode"), new String[] {"'ALL_ROWS'", "'FIRST_ROWS'", "'CHOOSE'"}),
      new SqlFilterDefComponent("(s.sharable_mem +s.persistent_mem +s.runtime_mem)", stringManager.getString("total-mem"), (String[])null),
      new SqlFilterDefComponent("s.executions", stringManager.getString("executions"), (String[])null),
      new SqlFilterDefComponent("s.disk_reads", stringManager.getString("disk-reads"), (String[])null),
      new SqlFilterDefComponent("s.buffer_gets", stringManager.getString("buffer-gets"), (String[])null)
    };
  }
  
  @Override
  public void afterOpen(Query query) {
    requestRefresh = true;
  }
  
}
