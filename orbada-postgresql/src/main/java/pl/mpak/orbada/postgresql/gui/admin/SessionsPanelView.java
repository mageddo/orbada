package pl.mpak.orbada.postgresql.gui.admin;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.comp.RefreshEvent;
import pl.mpak.sky.gui.swing.comp.RefreshListener;
import pl.mpak.sky.gui.swing.comp.RefreshPanel;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class SessionsPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  private boolean superuser;
  private String version;
  private RefreshPanel refreshPanel;
  
  public SessionsPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    version = PostgreSQLDbInfoProvider.instance.getVersion(getDatabase());
    superuser = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("superuser", "false"));
    refreshPanel = new RefreshPanel();
    refreshPanel.addRefreshListener(new RefreshListener() {
      @Override
      public void refresh(RefreshEvent e) {
        SessionsPanelView.this.refresh();
      }
    });
    getToolBar().add(refreshPanel);
    super.init();
  }
    
  @Override
  public Component getTabbedPane() {
    return new SessionTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return null;
  }
  
  @Override
  public String getPanelName() {
    return "postgresql-sessions";
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
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    ArrayList<QueryTableColumn> columns = new ArrayList<QueryTableColumn>();
    QueryTableCellRenderer renderer = new QueryTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          try {
            if (!getQuery().fieldByName("waiting").isNull() && getQuery().fieldByName("waiting").getBoolean()) {
              label.setBackground(new Color(251, 121, 121));
            }
            else if (getQuery().fieldByName("this").getBoolean()) {
              label.setBackground(new Color(180, 205, 240));
            }
            else if (getQuery().fieldByName("state").isNull() && value != null) {
              label.setForeground(Color.DARK_GRAY);
            }
          } catch (Exception ex) {
            label.setText(label.getText() +" [" +ex.getMessage() +"]");
          }
        }
        return label;
      }
    };
    columns.add(new QueryTableColumn("pid", stringManager.getString("pid"), 50, renderer));
    columns.add(new QueryTableColumn("database_name", stringManager.getString("database-name"), 80, renderer));
    columns.add(new QueryTableColumn("user_name", stringManager.getString("user-name"), 80, renderer));
    columns.add(new QueryTableColumn("application_name", stringManager.getString("application-name"), 200, renderer));
    columns.add(new QueryTableColumn("client_host", stringManager.getString("client-host"), 150, renderer));
    columns.add(new QueryTableColumn("backend_start", stringManager.getString("session-time"), 120, renderer));
    columns.add(new QueryTableColumn("query_start", stringManager.getString("query-time"), 120, renderer));
    columns.add(new QueryTableColumn("runned", stringManager.getString("runned-sec"), 50, renderer));
    columns.add(new QueryTableColumn("blocked_pid", stringManager.getString("blocked-pid"), 70, renderer));
    columns.add(new QueryTableColumn("state", stringManager.getString("state"), 60, renderer));
    columns.add(new QueryTableColumn("query", stringManager.getString("command"), 350, renderer));
    return (QueryTableColumn[])columns.toArray(new QueryTableColumn[columns.size()]);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionList(filter.getSqlText(), version);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("database_name", stringManager.getString("database-name"), (String[])null),
      new SqlFilterDefComponent("user_name", stringManager.getString("user-name"), (String[])null),
      new SqlFilterDefComponent("client_host", stringManager.getString("client-host"), (String[])null),
      new SqlFilterDefComponent("runned is not null", stringManager.getString("runned-sec")),
      new SqlFilterDefComponent("state", stringManager.getString("state"), (String[])null),
      new SqlFilterDefComponent("query", stringManager.getString("command"), (String[])null),
      new SqlFilterDefComponent("waiting", stringManager.getString("waiting"))
    };
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }
  
  @Override
  public boolean isHorizontal() {
    return false;
  }
  
  @Override
  public void close() throws IOException {
    refreshPanel.cancel();
    super.close();
  }
  
}
