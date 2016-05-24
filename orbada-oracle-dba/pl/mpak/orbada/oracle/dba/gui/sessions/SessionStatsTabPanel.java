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
public class SessionStatsTabPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

  private boolean clusters;
  
  public SessionStatsTabPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    clusters = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"));
    super.init();
  }
  
  public String getTitle() {
    return stringManager.getString("SessionStatsTabPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "oracle-session-stats";
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
    return "statistic#";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("name", stringManager.getString("name"), 300),
      new QueryTableColumn("value", stringManager.getString("value"), 200)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionStatList(filter.getSqlText(), clusters);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("n.name", stringManager.getString("name"), (String[])null),
      new SqlFilterDefComponent("s.value", stringManager.getString("value"), (String[])null)
    };
  }
  
  @Override
  public void afterOpen(Query query) {
    requestRefresh = true;
  }
  
}
