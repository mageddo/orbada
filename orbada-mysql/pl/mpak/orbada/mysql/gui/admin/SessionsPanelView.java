package pl.mpak.orbada.mysql.gui.admin;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.services.MySQLDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
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
import pl.mpak.util.id.VersionID;

/**
 *
 * @author  akaluza
 */
public class SessionsPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  private VersionID mySQLver;
  private boolean ge517;
  private RefreshPanel refreshPanel;
  
  public SessionsPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    mySQLver = new VersionID(MySQLDbInfoProvider.instance.getVersion(getDatabase()));
    ge517 = mySQLver.compare(5, 1, 7) >= 0;
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
  public void close() throws IOException {
    refreshPanel.cancel();
    super.close();
  }
  
  @Override
  public Component getTabbedPane() {
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return null;
  }

  @Override
  public String getPanelName() {
    return "mysql-sessions";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "id";
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
            if (ge517 && "T".equals(getQuery().fieldByName("THIS").getString())) {
              label.setBackground(new Color(180, 205, 240));
            }
            else if (value != null && value.toString().equalsIgnoreCase("Sleep")) {
              label.setForeground(Color.GRAY);
            }
          } catch (Exception ex) {
            label.setText(label.getText() +" [" +ex.getMessage() +"]");
          }
        }
        return label;
      }
    };
    columns.add(new QueryTableColumn("id", stringManager.getString("thread"), 50, renderer));
    columns.add(new QueryTableColumn("user", stringManager.getString("user"), 100, renderer));
    columns.add(new QueryTableColumn("host", stringManager.getString("host"), 150, renderer));
    columns.add(new QueryTableColumn("db", stringManager.getString("database"), 80, renderer));
    columns.add(new QueryTableColumn("command", stringManager.getString("command-type"), 80, renderer));
    columns.add(new QueryTableColumn("time", stringManager.getString("time"), 50, renderer));
    columns.add(new QueryTableColumn("state", stringManager.getString("state"), 80, renderer));
    columns.add(new QueryTableColumn("info", stringManager.getString("info"), 250, renderer));
    return (QueryTableColumn[])columns.toArray(new QueryTableColumn[columns.size()]);
  }

  @Override
  public String getSql(SqlFilter filter) {
    if (ge517) {
      return Sql.getProcessList(filter.getSqlText());
    }
    return Sql.getShowProcessList();
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    if (ge517) {
      return new SqlFilterDefComponent[] {
        new SqlFilterDefComponent("user", stringManager.getString("user"), (String[])null),
        new SqlFilterDefComponent("host", stringManager.getString("host"), (String[])null),
        new SqlFilterDefComponent("db", stringManager.getString("database"), (String[])null),
        new SqlFilterDefComponent("command", stringManager.getString("command-type"), (String[])null),
        new SqlFilterDefComponent("state", stringManager.getString("state"), (String[])null),
        new SqlFilterDefComponent("info", stringManager.getString("info"), (String[])null)
      };
    }
    return null;
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }
  
}
