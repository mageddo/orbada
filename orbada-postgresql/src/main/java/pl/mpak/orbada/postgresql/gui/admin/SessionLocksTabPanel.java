package pl.mpak.orbada.postgresql.gui.admin;

import java.awt.Color;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.YesNoCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class SessionLocksTabPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  private boolean superuser;
  private String version;
  
  public SessionLocksTabPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    version = PostgreSQLDbInfoProvider.instance.getVersion(getDatabase());
    superuser = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("superuser", "false"));
    super.init();
  }
  
  @Override
  public String getTitle() {
    return stringManager.getString("SessionLocksTabPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "postgresql-session-locks";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "pid";
  }

  @Override
  public String getPropColumnName() {
    return "object_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 80),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 80),
      new QueryTableColumn("object_type", stringManager.getString("object-type"), 90),
      new QueryTableColumn("object_name", stringManager.getString("object-name"), 170),
      new QueryTableColumn("transaction", stringManager.getString("transaction"), 90),
      new QueryTableColumn("mode", stringManager.getString("lock-mode"), 80),
      new QueryTableColumn("lockwait", stringManager.getString("wait-lock"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(Color.RED, SwingUtil.Color.GREEN)))
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionLockList(filter.getSqlText(), version);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("object_type", stringManager.getString("object-type"), (String[])null),
      new SqlFilterDefComponent("schema_name", stringManager.getString("schema-name"), (String[])null),
      new SqlFilterDefComponent("object_type", stringManager.getString("object-type"), (String[])null),
      new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null),
      new SqlFilterDefComponent("mode", stringManager.getString("lock-mode"), (String[])null),
      new SqlFilterDefComponent("lockwait", stringManager.getString("wait-lock")),
    };
  }
  
  @Override
  public void afterOpen(Query query) {
    requestRefresh = true;
  }
  
}
