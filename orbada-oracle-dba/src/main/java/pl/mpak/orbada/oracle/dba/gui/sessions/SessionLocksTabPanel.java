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
public class SessionLocksTabPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-dba");

  private boolean clusters;
  
  public SessionLocksTabPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    clusters = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"));
    super.init();
  }
  
  public String getTitle() {
    return stringManager.getString("SessionLocksTabPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "oracle-session-locks";
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
    return "object";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("object_type", stringManager.getString("obejct-type"), 150),
      new QueryTableColumn("object", stringManager.getString("object"), 200),
      new QueryTableColumn("lockwait", stringManager.getString("lock-wait"), 150),
      new QueryTableColumn("locked_mode", stringManager.getString("locked-mode"), 150)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionLockList(filter.getSqlText(), clusters);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("o.object_type", stringManager.getString("object-type"), (String[])null),
      new SqlFilterDefComponent("o.owner||'.'||o.object_name", stringManager.getString("object"), (String[])null),
      new SqlFilterDefComponent("nvl( s.lockwait, 'ACTIVE' )", stringManager.getString("lock-wait"), (String[])null),
    };
  }
  
  @Override
  public void afterOpen(Query query) {
    requestRefresh = true;
  }
  
}
